package com.magicpounds.paintinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.magicpounds.paintinventory.data.PaintContract;

public class SalesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, NavigationView.OnNavigationItemSelectedListener {

    private static final int SALES_LOADER = 1;
    SalesCursorAdapter salesCursorAdapter;
    long longPressedId = -1;
    Button getStartedBtn;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        setTitle("Sales");

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = getContentResolver().query(PaintContract.PaintEntry.CONTENT_URI_INVENTORY, null, null, null,null);
                Intent intent = new Intent(SalesActivity.this, EditSalesActivity.class);
                if(cursor.getCount() > 0)
                    startActivity(intent);
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(SalesActivity.this);
                    builder.setTitle("Add products first");
                    builder.setMessage("You have to add products first to make a sale");

                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        getStartedBtn = (Button) findViewById(R.id.getStartedBtn);
        drawerLayout = findViewById(R.id.drawerId);
        navigationView = findViewById(R.id.navigationId);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView salesListView = (ListView) findViewById(R.id.salesListView);
        salesCursorAdapter = new SalesCursorAdapter(this, null);
        salesListView.setAdapter(salesCursorAdapter);

        getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = getContentResolver().query(PaintContract.PaintEntry.CONTENT_URI_INVENTORY, null, null, null,null);
                Intent intent = new Intent(SalesActivity.this, EditSalesActivity.class);
                if(cursor.getCount() > 0)
                    startActivity(intent);
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(SalesActivity.this);
                    builder.setTitle("Add products first");
                    builder.setMessage("You first have to add products to make a sale");

                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        View emptyView = findViewById(R.id.emptyView);
        salesListView.setEmptyView(emptyView);

        registerForContextMenu(salesListView);
        salesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longPressedId = id;
                return false;
            }
        });

        getLoaderManager().initLoader(SALES_LOADER, null, this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.salesListView) {
            getMenuInflater().inflate(R.menu.long_press_menu_sales, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_this_sale:
                Uri uri = ContentUris.withAppendedId(PaintContract.PaintEntry.CONTENT_URI_SALES, longPressedId);
                Cursor cursor = getContentResolver().query(uri, null, null,  null, null);
                int deletedNo = getContentResolver().delete(uri, null, null);
                String name = "";
                while(cursor.moveToNext())
                {
                    int index = cursor.getColumnIndex(PaintContract.PaintEntry.COLUMN_PAINT_NAME_SALES);
                    name = cursor.getString(index);
                }
                if (deletedNo > 0)
                {
                    Toast.makeText(this, name + " has been Deleted", Toast.LENGTH_LONG).show();
                }

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sale_activiity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.makeNewSale:
                // Do nothing for now
                Cursor cursor = getContentResolver().query(PaintContract.PaintEntry.CONTENT_URI_INVENTORY, null, null, null,null);
                Intent intent = new Intent(SalesActivity.this, EditSalesActivity.class);
                if(cursor.getCount() > 0)
                    startActivity(intent);
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(SalesActivity.this);
                    builder.setTitle("Add products first");
                    builder.setMessage("You have to add products first to make a sale");

                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                return true;
            case R.id.clearAllSales:
                deleteAllSales();
                return true;

            case R.id.inventory:
                Intent intent1 = new Intent(SalesActivity.this, MainActivity.class);
                this.startActivity(intent1);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllSales() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_sales);
        builder.setPositiveButton(R.string.delete_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getContentResolver().delete(PaintContract.PaintEntry.CONTENT_URI_SALES, null, null);
            }
        });
        builder.setNegativeButton(R.string.delete_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Intent intent;
        switch (menuItem.getItemId()) {
            case R.id.menu_insert_new:
                intent = new Intent(this, EditActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_sales:
                intent = new Intent(this, SalesActivity.class);
                startActivity(intent);
                return true;
            case R.id.inventory:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.add_a_sale:
                Cursor cursor = getContentResolver().query(PaintContract.PaintEntry.CONTENT_URI_INVENTORY, null, null, null,null);
                intent = new Intent(SalesActivity.this, EditSalesActivity.class);
                if(cursor.getCount() > 0)
                    startActivity(intent);
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(SalesActivity.this);
                    builder.setTitle("Add products first");
                    builder.setMessage("You have to add products first to make a sale");

                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                return true;
        }
        return false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                PaintContract.PaintEntry._ID_SALES,
                PaintContract.PaintEntry.COLUMN_PAINT_NAME_SALES,
                PaintContract.PaintEntry.COLUMN_PAINT_PROFIT_SALES,
                PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_SALES,
                PaintContract.PaintEntry.COLUMN_PAINT_COST_SALES,
                PaintContract.PaintEntry.COLUMN_PAINT_DATE_SALES,
                PaintContract.PaintEntry.COLUMN_PAINT_TIME_SALES,
        };
        return new CursorLoader(this,
                PaintContract.PaintEntry.CONTENT_URI_SALES,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        salesCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        salesCursorAdapter.swapCursor(null);
    }
}


