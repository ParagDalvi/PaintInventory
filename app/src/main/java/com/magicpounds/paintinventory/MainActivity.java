package com.magicpounds.paintinventory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.FontRequestEmojiCompatConfig;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;
import android.support.text.emoji.widget.EmojiAppCompatTextView;
import android.support.v4.provider.FontRequest;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.magicpounds.paintinventory.data.PaintContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, NavigationView.OnNavigationItemSelectedListener {

    private static final int PAINT_LOADER = 0;
    PaintCursorAdapter mPaintCursorAdapter;
    Button getStartedBtn;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    ListView paintListView;
    long longPressesId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);
        setContentView(R.layout.activity_main);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        paintListView = (ListView) findViewById(R.id.catalogListView);
        registerForContextMenu(paintListView);
        getStartedBtn = (Button) findViewById(R.id.getStartedBtn);
        drawerLayout = findViewById(R.id.drawerId);
        navigationView = findViewById(R.id.navigationId);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        View emptyView = findViewById(R.id.emptyView);
        paintListView.setEmptyView(emptyView);

        getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        mPaintCursorAdapter = new PaintCursorAdapter(this, null);
        paintListView.setAdapter(mPaintCursorAdapter);

        paintListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                Uri currentPetUri = ContentUris.withAppendedId(PaintContract.PaintEntry.CONTENT_URI_INVENTORY, id);
                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });

        paintListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longPressesId = id;
                return false;
            }
        });

        getLoaderManager().initLoader(PAINT_LOADER, null, this);
    }

    /***
     *
     *
     *
     *
     *
     *
     * @param menu
     * @param v
     * @param menuInfo
     */
    //for long press on listview
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.catalogListView) {
            getMenuInflater().inflate(R.menu.long_press_menu_inventory, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.add:
                // add stuff here
                return true;
            case R.id.edit:
                if (longPressesId > 0) {
                    Intent intent = new Intent(MainActivity.this, EditActivity.class);
                    Uri currentPetUri = ContentUris.withAppendedId(PaintContract.PaintEntry.CONTENT_URI_INVENTORY, longPressesId);
                    intent.setData(currentPetUri);
                    startActivity(intent);
                }
                return true;
            case R.id.delete:
                if (longPressesId > 0) {
                    Uri uri = ContentUris.withAppendedId(PaintContract.PaintEntry.CONTENT_URI_INVENTORY, longPressesId);
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    String name = "";
                    while (cursor.moveToNext()) {
                        int index = cursor.getColumnIndex(PaintContract.PaintEntry.COLUMN_PAINT_NAME_INVENTORY);
                        name = cursor.getString(index);
                    }
                    int deletedNo = getContentResolver().delete(uri, null, null);
                    if (deletedNo > 0)
                        Toast.makeText(this, name + " Deleted ", Toast.LENGTH_LONG).show();
                    longPressesId = -1;
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        super.onResume();
    }

    /***
     *
     *
     *
     *
     *
     * @param menu
     * @return
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

// User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.menuItemInsertNew:
                // Do nothing for now
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                this.startActivity(intent);
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.menuItemDeleteAll:
                // Do nothing for now
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllProducts() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_question);
        builder.setPositiveButton(R.string.delete_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getContentResolver().delete(PaintContract.PaintEntry.CONTENT_URI_INVENTORY, null, null);
            }
        });
        builder.setNegativeButton(R.string.delete_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                PaintContract.PaintEntry._ID,
                PaintContract.PaintEntry.COLUMN_PAINT_NAME_INVENTORY,
                PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_INVENTORY,
                PaintContract.PaintEntry.COLUMN_PAINT_COST_INVENTORY
        };
        return new CursorLoader(this,
                PaintContract.PaintEntry.CONTENT_URI_INVENTORY,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPaintCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPaintCursorAdapter.swapCursor(null);
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
                intent = new Intent(this, EditSalesActivity.class);
                if(cursor.getCount() > 0)
                    startActivity(intent);
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
}
