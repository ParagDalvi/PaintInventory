package com.magicpounds.paintinventory;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.magicpounds.paintinventory.data.PaintContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class EditSalesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PAINT_LOADER = 0;
    EditText quantityEditText;
    Button reduceBtn;
    Button increaseBtn;
    FloatingActionButton saveFloatBtn;
    Spinner productSelectionSpinner;
    int initialQty = 0;
    int quantityAfterSale;
    TextView insufficientQty,inventoryQtyTextView ;



    private boolean mPaintHasChanged = false;


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPaintHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sales);

        quantityEditText = (EditText) findViewById(R.id.quantityEditTextView);
        reduceBtn = (Button) findViewById(R.id.reduceQuantityBtn);
        increaseBtn = (Button) findViewById(R.id.increaseQuantityBtn);
        saveFloatBtn = (FloatingActionButton) findViewById(R.id.saveFloadtingBtn);
        insufficientQty = (TextView) findViewById(R.id.insufficentQtyTextView);
        inventoryQtyTextView = (TextView) findViewById(R.id.inventoryQtyTextView);

        setTitle("Make a Sale");

        quantityEditText.setText("0");

        productSelectionSpinner = (Spinner) findViewById(R.id.productSelectionSpinner);

        Cursor cursor = getContentResolver().query(PaintContract.PaintEntry.CONTENT_URI_INVENTORY,
                null, null, null, null);
        List<String> productListString = new ArrayList<String>();

        while (cursor.moveToNext()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(PaintContract.PaintEntry.COLUMN_PAINT_NAME_INVENTORY);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);

            productListString.add(name);
        }

        Collections.sort(productListString);

        quantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String qty = quantityEditText.getText().toString();
                if(!TextUtils.isEmpty(qty))
                    initialQty = (int) Integer.parseInt(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //for list of product names
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, productListString);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.notifyDataSetChanged();
        productSelectionSpinner.setAdapter(adapter);

        reduceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialQty--;

                if (initialQty < 0) {
                    initialQty = 0;
                }

                if (initialQty == 0) {
                    insufficientQty.setText("Quantity can't be 0 to make a sale");
                    insufficientQty.setVisibility(View.VISIBLE);
                } else {
                    insufficientQty.setVisibility(View.GONE);
                }

                quantityEditText.setText(Integer.toString(initialQty));
            }
        });

        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String productName = productSelectionSpinner.getSelectedItem().toString();
                String[] simpllyArray = new String[1];
                simpllyArray[0] = productName;

                String[] projection = {PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_INVENTORY};

                Cursor cursor = getContentResolver().query(PaintContract.PaintEntry.CONTENT_URI_INVENTORY, projection,
                        PaintContract.PaintEntry.COLUMN_PAINT_NAME_INVENTORY + "=?", simpllyArray, null);

                int quantityOfInventory = 0;
                while (cursor.moveToNext()) {
                    int quantityColumnIndex = cursor.getColumnIndex(PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_INVENTORY);
                    quantityOfInventory = cursor.getInt(quantityColumnIndex);
                }

                if (initialQty != quantityOfInventory) {
                    initialQty++;
                }

                if (initialQty < 0) {
                    initialQty = 0;
                }
                if (initialQty == 0) {
                    insufficientQty.setText("Quantity can't nbe 0 to make a sale");
                    insufficientQty.setVisibility(View.VISIBLE);
                } else {
                    insufficientQty.setVisibility(View.GONE);
                }
                if(initialQty == quantityOfInventory)
                {
                    insufficientQty.setText("Maximum quantity reached");
                    insufficientQty.setVisibility(View.VISIBLE);
                }

                quantityEditText.setText(Integer.toString(initialQty));
            }
        });

        productSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String productName = productSelectionSpinner.getSelectedItem().toString();
                String[] simpllyArray = new String[1];
                simpllyArray[0] = productName;

                String[] projection = {PaintContract.PaintEntry.COLUMN_PAINT_COST_INVENTORY,
                        PaintContract.PaintEntry._ID_INVENTORY,
                        PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_INVENTORY};

                Cursor cursor = getContentResolver().query(PaintContract.PaintEntry.CONTENT_URI_INVENTORY, projection,
                        PaintContract.PaintEntry.COLUMN_PAINT_NAME_INVENTORY + "=?", simpllyArray, null);
                Log.e("hahahaha", "onItemSelected: "+ productName );
                int qty = 0;
                while (cursor.moveToNext())
                {
                    int index = cursor.getColumnIndex(PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_INVENTORY);
                    qty = cursor.getInt(index);
                }
                inventoryQtyTextView.setText("Quantity: " + Integer.toString(qty));
                initialQty = 0;
                quantityEditText.setText(Integer.toString(initialQty));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        productSelectionSpinner.setOnTouchListener(mTouchListener);
        quantityEditText.setOnTouchListener(mTouchListener);
        increaseBtn.setOnTouchListener(mTouchListener);
        reduceBtn.setOnTouchListener(mTouchListener);


        saveFloatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSale();
            }
        });

        getLoaderManager().initLoader(EXISTING_PAINT_LOADER, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_sales_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_sale) {
            saveSale();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mPaintHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void saveSale() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space


        String productName = productSelectionSpinner.getSelectedItem().toString();
        String[] simpllyArray = new String[1];
        simpllyArray[0] = productName;

        String quantityString = quantityEditText.getText().toString().trim();

        if (Integer.parseInt(quantityString) == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Quantity is 0");
            builder.setMessage("Cannot make a sale if the quantity is 0");
            builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked the "Keep editing" button, so dismiss the dialog
                    // and continue editing the pet.
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });

            // Create and show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return;
        }


        ContentValues values = new ContentValues();
        String[] projection = {PaintContract.PaintEntry.COLUMN_PAINT_COST_INVENTORY,
                PaintContract.PaintEntry._ID_INVENTORY,
                PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_INVENTORY};

        Cursor cursor = getContentResolver().query(PaintContract.PaintEntry.CONTENT_URI_INVENTORY, projection,
                PaintContract.PaintEntry.COLUMN_PAINT_NAME_INVENTORY + "=?", simpllyArray, null);

        int costFromInventoryTable = 0;
        int quantityFromInventoryTable = 0;
        long idOfInventoryTable = 0;
        while (cursor.moveToNext()) {
            int costColumnIndex = cursor.getColumnIndex(PaintContract.PaintEntry.COLUMN_PAINT_COST_INVENTORY);
            int quantityColumIndex = cursor.getColumnIndex(PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_INVENTORY);
            int IDColumnIndex = cursor.getColumnIndex(PaintContract.PaintEntry._ID_INVENTORY);

            idOfInventoryTable = cursor.getInt(IDColumnIndex);
            costFromInventoryTable = cursor.getInt(costColumnIndex);
            quantityFromInventoryTable = cursor.getInt(quantityColumIndex);
        }

        if(initialQty > quantityFromInventoryTable)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You don't have these many products");
            builder.setNegativeButton("Reduce quantity", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked the "Keep editing" button, so dismiss the dialog
                    // and continue editing the pet.
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });

            // Create and show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return;
        }
        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.

        values.put(PaintContract.PaintEntry.COLUMN_PAINT_NAME_SALES, productName);
        values.put(PaintContract.PaintEntry.COLUMN_PAINT_PROFIT_SALES, Integer.parseInt(quantityString) * costFromInventoryTable);
        values.put(PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_SALES, Integer.parseInt(quantityString));
        values.put(PaintContract.PaintEntry.COLUMN_PAINT_COST_SALES, costFromInventoryTable);


        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yy");
        String currentDate = sdfDate.format(new Date());

        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");
        String currentTime = sdfTime.format(new Date());

        values.put(PaintContract.PaintEntry.COLUMN_PAINT_DATE_SALES, currentDate);
        values.put(PaintContract.PaintEntry.COLUMN_PAINT_TIME_SALES, currentTime);

        quantityAfterSale = quantityFromInventoryTable - Integer.parseInt(quantityString);

        ContentValues inventoryUpdate = new ContentValues();
        inventoryUpdate.put(PaintContract.PaintEntry.COLUMN_PAINT_NAME_INVENTORY, productName);
        inventoryUpdate.put(PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_INVENTORY, quantityAfterSale);
        inventoryUpdate.put(PaintContract.PaintEntry.COLUMN_PAINT_COST_INVENTORY, costFromInventoryTable);

        Uri updateInventoryUri = ContentUris.withAppendedId(PaintContract.PaintEntry.CONTENT_URI_INVENTORY, idOfInventoryTable);


        int rowsUpdatedOfInventory = getContentResolver().update(updateInventoryUri, inventoryUpdate, null, null);


        // Insert a new pet into the provider, returning the content URI for the new pet.
        Uri newUri;
        int rowsUpdated = 0;
        newUri = getContentResolver().insert(PaintContract.PaintEntry.CONTENT_URI_SALES, values);


        // Show a toast message depending on whether or not the insertion was successful
        if (rowsUpdated == 0 && newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.error_saving_product),
                    Toast.LENGTH_LONG).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, productName + " " + getString(R.string.success_saving_product),
                    Toast.LENGTH_LONG).show();
        }
        finish();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                PaintContract.PaintEntry._ID_SALES,
                PaintContract.PaintEntry.COLUMN_PAINT_NAME_SALES,
                PaintContract.PaintEntry.COLUMN_PAINT_PROFIT_SALES,
                PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_SALES,
                PaintContract.PaintEntry.COLUMN_PAINT_COST_SALES};

        return new CursorLoader(this,
                PaintContract.PaintEntry.CONTENT_URI_SALES,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        /*
         *
         *
         *
         *
         * Dont know if this is required
         *
         *
         *
         *
         *
         */

//        if (cursor.moveToFirst()) {
//            // Find the columns of pet attributes that we're interested in
//            int nameColumnIndex = cursor.getColumnIndex(PaintContract.PaintEntry.COLUMN_PAINT_NAME_SALES);
//            int quantityColumnIndex = cursor.getColumnIndex(PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_SALES);
//
//            // Extract out the value from the Cursor for the given column index
//            String name = cursor.getString(nameColumnIndex);
//            int quantity = cursor.getInt(quantityColumnIndex);
//
//            // Update the views on the screen with the values from the database
//            productSelectionSpinner.setSelection(nameColumnIndex);
//            quantityEditText.setText(Integer.toString(quantity));
//        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // quantityEditText.setText("");
    }
}