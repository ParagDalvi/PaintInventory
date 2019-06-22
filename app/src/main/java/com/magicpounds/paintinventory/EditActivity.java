package com.magicpounds.paintinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.magicpounds.paintinventory.data.PaintContract;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PAINT_LOADER = 0;
    private Uri mCurrentPetUri = null;


    EditText nameEditText;
    EditText quantityEditText;
    EditText costEditText;
    Button reduceBtn;
    Button increaseBtn;
    FloatingActionButton saveFloatBtn;
    int initialQty = 0;
    List<String> listOfProductsInInventory = new ArrayList<>();

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
        setContentView(R.layout.activity_edit);

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        quantityEditText = (EditText) findViewById(R.id.quantityEditTextView);
        costEditText = (EditText) findViewById(R.id.costEditText);
        reduceBtn = (Button) findViewById(R.id.reduceQuantityBtn);
        increaseBtn = (Button) findViewById(R.id.increaseQuantityBtn);
        saveFloatBtn = (FloatingActionButton) findViewById(R.id.saveFloadtingBtn);

        Intent intent = getIntent();
        mCurrentPetUri = intent.getData();

        if (mCurrentPetUri == null) {
            setTitle(R.string.add_new_paint);
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.edit_existing_product);
        }

        Cursor cursorToGetListOfProducts = getContentResolver().query(PaintContract.PaintEntry.CONTENT_URI_INVENTORY,
                null, null, null, null);
        while (cursorToGetListOfProducts.moveToNext()) {
            int index = cursorToGetListOfProducts.getColumnIndex(PaintContract.PaintEntry.COLUMN_PAINT_NAME_INVENTORY);
            String name = cursorToGetListOfProducts.getString(index);
            listOfProductsInInventory.add(name);
        }

        quantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                initialQty = (int) Integer.parseInt(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        reduceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPetUri == null) {
                    initialQty--;
                    if (initialQty < 1) {
                        initialQty = 1;
                    }
                    quantityEditText.setText(Integer.toString(initialQty));
                } else {
                    initialQty = Integer.parseInt(quantityEditText.getText().toString());
                    initialQty--;
                    if (initialQty < 1) {
                        initialQty = 1;
                    }
                    quantityEditText.setText(Integer.toString(initialQty));
                }
            }
        });

        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPetUri == null) {
                    initialQty++;
                    if (initialQty < 1) {
                        initialQty = 1;
                    }
                    quantityEditText.setText(Integer.toString(initialQty));
                } else {
                    initialQty = Integer.parseInt(quantityEditText.getText().toString());
                    initialQty++;
                    if (initialQty < 1) {
                        initialQty = 1;
                    }
                    quantityEditText.setText(Integer.toString(initialQty));
                }
            }
        });


        getLoaderManager().initLoader(EXISTING_PAINT_LOADER, null, this);

        nameEditText.setOnTouchListener(mTouchListener);
        quantityEditText.setOnTouchListener(mTouchListener);
        costEditText.setOnTouchListener(mTouchListener);
        increaseBtn.setOnTouchListener(mTouchListener);
        reduceBtn.setOnTouchListener(mTouchListener);

        saveFloatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePaint();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
// User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.menuItemDeleteOne:
                // Do nothing for now
                if (mCurrentPetUri != null) {
                    long deletedInt = getContentResolver().delete(mCurrentPetUri, null, null);
                    if (deletedInt > 0) {
                        String nameString = nameEditText.getText().toString().trim();
                        Toast.makeText(this, nameString + " " + getString(R.string.item_deleted),
                                Toast.LENGTH_LONG).show();
                    }
                }
                finish();

                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.savePaint:
                // Do nothing for now
                savePaint();
                return true;

            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mPaintHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentPetUri == null) {
            MenuItem menuItem = menu.findItem(R.id.menuItemDeleteOne);
            menuItem.setVisible(false);
        }
        return true;
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


    private void savePaint() {

        String nameString = nameEditText.getText().toString().trim();

        if (mCurrentPetUri == null && listOfProductsInInventory.contains(nameString)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("You cannot add multiple products of the same name. Please change the name");
            builder.setTitle("Product already exists");

            // Set Cancelable false
            // for when the user clicks on the outside
            // the Dialog Box then it will remain show
            builder.setCancelable(false);

            // Set the Negative button with No name
            // OnClickListener method is use
            // of DialogInterface interface.
            builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // If user click no
                    // then dialog box is canceled.
                    dialog.cancel();
                }
            });

            // Create the Alert dialog
            AlertDialog alertDialog = builder.create();

            // Show the Alert Dialog box
            alertDialog.show();
            return;
        }

        String quantityString = quantityEditText.getText().toString().trim();
        String costString = costEditText.getText().toString().trim();

        if (mCurrentPetUri == null && (
                TextUtils.isEmpty(nameString)
                || TextUtils.isEmpty(quantityString)
                || TextUtils.isEmpty(costString))) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Enter all the details");
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
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
        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        values.put(PaintContract.PaintEntry.COLUMN_PAINT_NAME_INVENTORY, nameString);
        values.put(PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_INVENTORY, Integer.parseInt(quantityString));
        values.put(PaintContract.PaintEntry.COLUMN_PAINT_COST_INVENTORY, Integer.parseInt(costString));


        // Insert a new pet into the provider, returning the content URI for the new pet.
        Uri newUri;
        int rowsUpdated = 0;
        if (mCurrentPetUri == null) {
            newUri = getContentResolver().insert(PaintContract.PaintEntry.CONTENT_URI_INVENTORY, values);
        } else {
            rowsUpdated = getContentResolver().update(mCurrentPetUri, values, null, null);
            newUri = null;
        }

        // Show a toast message depending on whether or not the insertion was successful
        if (rowsUpdated == 0 && newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.error_saving_product),
                    Toast.LENGTH_LONG).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, nameString + " " + getString(R.string.success_saving_product),
                    Toast.LENGTH_LONG).show();
        }

        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        if (mCurrentPetUri == null) {
            return null;
        }

        String[] projection = {
                PaintContract.PaintEntry._ID_INVENTORY,
                PaintContract.PaintEntry.COLUMN_PAINT_NAME_INVENTORY,
                PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_INVENTORY,
                PaintContract.PaintEntry.COLUMN_PAINT_COST_INVENTORY};

        return new CursorLoader(this,
                mCurrentPetUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(PaintContract.PaintEntry.COLUMN_PAINT_NAME_INVENTORY);
            int quantityColumnIndex = cursor.getColumnIndex(PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_INVENTORY);
            int costColumnIndex = cursor.getColumnIndex(PaintContract.PaintEntry.COLUMN_PAINT_COST_INVENTORY);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int cost = cursor.getInt(costColumnIndex);

            // Update the views on the screen with the values from the database
            nameEditText.setText(name);
            quantityEditText.setText(Integer.toString(quantity));
            costEditText.setText(Integer.toString(cost));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        nameEditText.setText("");
        quantityEditText.setText("");
        costEditText.setText("");
    }
}
