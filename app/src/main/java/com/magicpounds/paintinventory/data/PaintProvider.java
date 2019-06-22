package com.magicpounds.paintinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class PaintProvider extends ContentProvider {

    private static final int PAINTS = 100;
    private static final int PAINTS_ID = 101;

    private static final int SALES = 200;
    private static final int SALES_ID = 201;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // TODO: Add 2 content URIs to URI matcher
        sUriMatcher.addURI(PaintContract.CONTENT_AUTHORITY, PaintContract.INVENTORY_TABLE, PAINTS);
        sUriMatcher.addURI(PaintContract.CONTENT_AUTHORITY, PaintContract.INVENTORY_TABLE + "/#", PAINTS_ID);

        sUriMatcher.addURI(PaintContract.CONTENT_AUTHORITY, PaintContract.SALES_TABLE, SALES);
        sUriMatcher.addURI(PaintContract.CONTENT_AUTHORITY, PaintContract.SALES_TABLE + "/#", SALES_ID);
    }


    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        PaintDbHelper mDbHelper = new PaintDbHelper(getContext());
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PAINTS:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                // TODO: Perform database query on pets table
                cursor = database.query(PaintContract.PaintEntry.TABLE_NAME_INVENTORY, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PAINTS_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = PaintContract.PaintEntry._ID_INVENTORY + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(PaintContract.PaintEntry.TABLE_NAME_INVENTORY, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case SALES:
                cursor = database.query(PaintContract.PaintEntry.TABLE_NAME_SALES, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case SALES_ID:
                selection = PaintContract.PaintEntry._ID_SALES + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(PaintContract.PaintEntry.TABLE_NAME_SALES, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PAINTS:
                return PaintContract.PaintEntry.CONTENT_LIST_TYPE_INVENTORY;
            case PAINTS_ID:
                return PaintContract.PaintEntry.CONTENT_ITEM_TYPE_INVENTORY;
            case SALES:
                return PaintContract.PaintEntry.CONTENT_LIST_TYPE_SALES;
            case SALES_ID:
                return PaintContract.PaintEntry.CONTENT_ITEM_TYPE_SALES;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PAINTS:
                return insertPaint(uri, contentValues);
            case SALES:
                return insertSales(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertSales(Uri uri, ContentValues values) {
        String name = values.getAsString(PaintContract.PaintEntry.COLUMN_PAINT_NAME_SALES);
        if (name == null){
            throw new IllegalArgumentException("Pet requires a name");
        }

        Integer profit = values.getAsInteger(PaintContract.PaintEntry.COLUMN_PAINT_PROFIT_SALES);
        if (profit != null && profit < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }

        Integer quantity = values.getAsInteger(PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_SALES);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }

        Integer cost = values.getAsInteger(PaintContract.PaintEntry.COLUMN_PAINT_COST_SALES);
        if (cost != null && cost < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }

        String date = values.getAsString(PaintContract.PaintEntry.COLUMN_PAINT_DATE_SALES);
        if (date == null){
            throw new IllegalArgumentException("Pet requires a name");
        }

        String time = values.getAsString(PaintContract.PaintEntry.COLUMN_PAINT_TIME_SALES);
        if (time == null){
            throw new IllegalArgumentException("Pet requires a name");
        }


        // TODO: Insert a new pet into the pets database table with the given ContentValues
        PaintDbHelper mDbHelper = new PaintDbHelper(getContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(PaintContract.PaintEntry.TABLE_NAME_SALES, null, values);

        if (id == -1) {
            Log.e("PetPrvider class", "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertPaint(Uri uri, ContentValues values) {

        String name = values.getAsString(PaintContract.PaintEntry.COLUMN_PAINT_NAME_INVENTORY);
        if (name == null){
            throw new IllegalArgumentException("Pet requires a name");
        }

        Integer quantity = values.getAsInteger(PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_INVENTORY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }

        Integer cost = values.getAsInteger(PaintContract.PaintEntry.COLUMN_PAINT_COST_INVENTORY);
        if (cost != null && cost < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }


        // TODO: Insert a new pet into the pets database table with the given ContentValues
        PaintDbHelper mDbHelper = new PaintDbHelper(getContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(PaintContract.PaintEntry.TABLE_NAME_INVENTORY, null, values);

        if (id == -1) {
            Log.e("PetPrvider class", "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        PaintDbHelper mDbHelper = new PaintDbHelper(getContext());
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case PAINTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(PaintContract.PaintEntry.TABLE_NAME_INVENTORY, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case PAINTS_ID:
                // Delete a single row given by the ID in the URI
                selection = PaintContract.PaintEntry._ID_INVENTORY + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(PaintContract.PaintEntry.TABLE_NAME_INVENTORY, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case SALES:
                rowsDeleted = database.delete(PaintContract.PaintEntry.TABLE_NAME_SALES, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case SALES_ID:
                selection = PaintContract.PaintEntry._ID_SALES + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(PaintContract.PaintEntry.TABLE_NAME_SALES, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
            throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PAINTS:
                return updatePaint(uri, contentValues, selection, selectionArgs);
            case PAINTS_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PaintContract.PaintEntry._ID_INVENTORY + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePaint(uri, contentValues, selection, selectionArgs);
            case SALES:
                return updateSales(uri, contentValues, selection, selectionArgs);
            case SALES_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PaintContract.PaintEntry._ID_SALES + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateSales(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateSales(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // TODO: Update the selected pets in the pets database table with the given ContentValues
        String name = values.getAsString(PaintContract.PaintEntry.COLUMN_PAINT_NAME_SALES);
        if (values.containsKey(PaintContract.PaintEntry.COLUMN_PAINT_NAME_SALES) && name == null){
            throw new IllegalArgumentException("Sold product requires a name");
        }

        Integer profit = values.getAsInteger(PaintContract.PaintEntry.COLUMN_PAINT_PROFIT_SALES);
        if(values.containsKey(PaintContract.PaintEntry.COLUMN_PAINT_PROFIT_SALES) && (profit != null && profit<0))
        {
            throw new IllegalArgumentException("Sold product requires profit");
        }

        Integer quantity = values.getAsInteger(PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_SALES);
        if (values. containsKey(PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_SALES) && (quantity != null && quantity < 0)) {
            throw new IllegalArgumentException("Sold product requires quantity");
        }

        Integer cost = values.getAsInteger(PaintContract.PaintEntry.COLUMN_PAINT_COST_SALES);
        if (values. containsKey(PaintContract.PaintEntry.COLUMN_PAINT_COST_SALES) && (cost != null && cost < 0)) {
            throw new IllegalArgumentException("Sold product requires cost");
        }

        String date = values.getAsString(PaintContract.PaintEntry.COLUMN_PAINT_DATE_SALES);
        if (values.containsKey(PaintContract.PaintEntry.COLUMN_PAINT_DATE_SALES) && date == null){
            throw new IllegalArgumentException("Sold product requires a date");
        }

        String time = values.getAsString(PaintContract.PaintEntry.COLUMN_PAINT_TIME_SALES);
        if (values.containsKey(PaintContract.PaintEntry.COLUMN_PAINT_NAME_SALES) && time == null){
            throw new IllegalArgumentException("Sold product requires time");
        }

        // TODO: Return the number of rows that were affected
        PaintDbHelper mDbHelper = new PaintDbHelper(getContext());
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(PaintContract.PaintEntry.TABLE_NAME_SALES, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Returns the number of database rows affected by the update statement
        return rowsUpdated;
    }

    private int updatePaint(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // TODO: Update the selected pets in the pets database table with the given ContentValues
        String name = values.getAsString(PaintContract.PaintEntry.COLUMN_PAINT_NAME_INVENTORY);
        if (values.containsKey(PaintContract.PaintEntry.COLUMN_PAINT_NAME_INVENTORY) && name == null){
            throw new IllegalArgumentException("Pet requires a name");
        }

        Integer quantity = values.getAsInteger(PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_INVENTORY);
        if (values. containsKey(PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_INVENTORY) && (quantity != null && quantity < 0)) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }

        Integer cost = values.getAsInteger(PaintContract.PaintEntry.COLUMN_PAINT_COST_INVENTORY);
        if (values. containsKey(PaintContract.PaintEntry.COLUMN_PAINT_COST_INVENTORY) && (cost != null && cost < 0)) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }

        // TODO: Return the number of rows that were affected
        PaintDbHelper mDbHelper = new PaintDbHelper(getContext());
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(PaintContract.PaintEntry.TABLE_NAME_INVENTORY, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Returns the number of database rows affected by the update statement
        return rowsUpdated;
    }
}
