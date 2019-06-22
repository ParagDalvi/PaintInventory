package com.magicpounds.paintinventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.magicpounds.paintinventory.data.PaintContract;

public class SalesCursorAdapter extends CursorAdapter {
    public SalesCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_view_sales, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameText = (TextView) view.findViewById(R.id.productNameTextView);
        TextView quantityText = (TextView) view.findViewById(R.id.quantityTextView);
        TextView profitTextView = (TextView) view.findViewById(R.id.profit);
        TextView costText = (TextView) view.findViewById(R.id.costTextView);
        TextView dateText = (TextView) view.findViewById(R.id.date);
        TextView timeText = (TextView) view.findViewById(R.id.time);

        int nameColumnIndex = cursor.getColumnIndex(PaintContract.PaintEntry.COLUMN_PAINT_NAME_SALES);
        int profitColumnIndex = cursor.getColumnIndex(PaintContract.PaintEntry.COLUMN_PAINT_PROFIT_SALES);
        int quantityColumnIndex = cursor.getColumnIndex(PaintContract.PaintEntry.COLUMN_PAINT_QUANTITY_SALES);
        int costColumnIndex = cursor.getColumnIndex(PaintContract.PaintEntry.COLUMN_PAINT_COST_SALES);
        int dateColumIndex = cursor.getColumnIndex(PaintContract.PaintEntry.COLUMN_PAINT_DATE_SALES);
        int timeColumnIndex = cursor.getColumnIndex(PaintContract.PaintEntry.COLUMN_PAINT_TIME_SALES);


        String name = cursor.getString(nameColumnIndex);
        String profit = cursor.getString(profitColumnIndex);
        String quantity = cursor.getString(quantityColumnIndex);
        String cost = cursor.getString(costColumnIndex);
        String date = cursor.getString(dateColumIndex);
        String time = cursor.getString(timeColumnIndex);


        nameText.setText(name);
        profitTextView.setText(profit);
        quantityText.setText(" " + quantity + "*");
        costText.setText(cost);
        dateText.setText(date);
        timeText.setText(time);

    }
}
