package com.magicpounds.paintinventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.magicpounds.paintinventory.data.PaintContract;

import org.w3c.dom.Text;

public class PaintCursorAdapter extends CursorAdapter {

    public PaintCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_view_inventory, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameText = (TextView) view.findViewById(R.id.productNameTextView);
        TextView quantityText = (TextView) view.findViewById(R.id.quantityTextView);
        TextView costText = (TextView) view.findViewById(R.id.productCostTextView);

        int nameColumnIndex = cursor.getColumnIndex(PaintContract.PaintEntry.COLUMN_PAINT_NAME_INVENTORY);
        int quantityColumnIndex = cursor.getColumnIndex(PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_INVENTORY);
        int costColumIndex = cursor.getColumnIndex(PaintContract.PaintEntry.COLUMN_PAINT_COST_INVENTORY);

        String name = cursor.getString(nameColumnIndex);
        String quantity = cursor.getString(quantityColumnIndex);
        String cost = cursor.getString(costColumIndex);

        nameText.setText(name);
        quantityText.setText(quantity);
        costText.setText("Rs." + cost);

    }
}
