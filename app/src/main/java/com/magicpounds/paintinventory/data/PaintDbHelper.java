package com.magicpounds.paintinventory.data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;


public class PaintDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "AllTables.db";
    static final int DATABASE_VERSION = 1;

    public PaintDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_PAINTS_INVENTORY_TABLE = "CREATE TABLE " + PaintContract.PaintEntry.TABLE_NAME_INVENTORY + "("
                + PaintContract.PaintEntry._ID_INVENTORY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PaintContract.PaintEntry.COLUMN_PAINT_NAME_INVENTORY + " TEXT NOT NULL, "
                + PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_INVENTORY + " INTEGER NOT NULL, "
                + PaintContract.PaintEntry.COLUMN_PAINT_COST_INVENTORY + " INTEGER NOT NULL); ";

        db.execSQL(SQL_CREATE_PAINTS_INVENTORY_TABLE);

        String SQL_CREATE_PAINTS_SALES_TABLE = "CREATE TABLE " + PaintContract.PaintEntry.TABLE_NAME_SALES+ "("
                + PaintContract.PaintEntry._ID_INVENTORY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PaintContract.PaintEntry.COLUMN_PAINT_NAME_SALES + " TEXT NOT NULL, "
                + PaintContract.PaintEntry.COLUMN_PAINT_PROFIT_SALES + " INTEGER NOT NULL, "
                + PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_SALES + " INTEGER NOT NULL, "
                + PaintContract.PaintEntry.COLUMN_PAINT_COST_SALES + " INTEGER NOT NULL, "
                + PaintContract.PaintEntry.COLUMN_PAINT_DATE_SALES + " TEXT NOT NULL, "
                + PaintContract.PaintEntry.COLUMN_PAINT_TIME_SALES + " TEXT NOT NULL) ";

        db.execSQL(SQL_CREATE_PAINTS_SALES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
