package com.magicpounds.paintinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class PaintContract {

    private PaintContract() {
    }
    public static final String CONTENT_AUTHORITY = "com.magicpounds.paintinventory";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String INVENTORY_TABLE = "inventory";

    public static final String SALES_TABLE = "sales";


    public static class PaintEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE_INVENTORY =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + INVENTORY_TABLE;

        public static final String CONTENT_ITEM_TYPE_INVENTORY =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + INVENTORY_TABLE;

        public static final Uri CONTENT_URI_INVENTORY = Uri.withAppendedPath(BASE_CONTENT_URI, INVENTORY_TABLE);

        public static final String TABLE_NAME_INVENTORY = "inventory";
        public static final String _ID_INVENTORY = BaseColumns._ID;
        public static final String COLUMN_PAINT_NAME_INVENTORY = "name";
        public static final String COLUMN_PAINT_QUANTITY_INVENTORY = "quantity";
        public static final String COLUMN_PAINT_COST_INVENTORY = "cost";


        ///sales table

        public static final String CONTENT_LIST_TYPE_SALES =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + SALES_TABLE;

        public static final String CONTENT_ITEM_TYPE_SALES=
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + SALES_TABLE;

        public static final Uri CONTENT_URI_SALES = Uri.withAppendedPath(BASE_CONTENT_URI, SALES_TABLE);


        public static final String TABLE_NAME_SALES = "sales";
        public static final String _ID_SALES = BaseColumns._ID;
        public static final String COLUMN_PAINT_NAME_SALES = "name";
        public static final String COLUMN_PAINT_PROFIT_SALES = "profit";
        public static final String COLUMN_PAINT_QUANTITY_SALES = "quantity";
        public static final String COLUMN_PAINT_COST_SALES = "cost";
        public static final String COLUMN_PAINT_DATE_SALES = "date";
        public static final String COLUMN_PAINT_TIME_SALES = "time";

    }
}
