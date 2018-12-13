package com.example.abhishekkoranne.retail_inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.abhishekkoranne.retail_inventory.data.InventoryContract.InventoryEntry;

public class InventoryCursorAdapter extends CursorAdapter {

    private Context mContext;
    private InventoryItemClickListener mListener;

    public InventoryCursorAdapter(Context context, Cursor c, InventoryItemClickListener listener) {
        super(context, c, 0);
        mContext = context;
        mListener = listener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView bookTextView = view.findViewById(R.id.book_name);
        TextView priceTextView = view.findViewById(R.id.price);
        final TextView quantityTextView = view.findViewById(R.id.quantity);

        int bookColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);

        String bookName = cursor.getString(bookColumnIndex);
        float price = cursor.getFloat(priceColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);

        bookTextView.setText(bookName);
        priceTextView.setText(String.valueOf(price));
        quantityTextView.setText(String.valueOf(quantity));

        Button saleButton = view.findViewById(R.id.sale_button);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantity = quantityTextView.getText().toString();
                if (Integer.valueOf(quantity) > 0) {
                    int newQuantity = Integer.valueOf(quantity) - 1;
                    //Gets the position of button
                    View relativeLayout = (View) view.getParent();
                    View linearLayout = (View) relativeLayout.getParent();
                    ListView listView = (ListView) linearLayout.getParent();
                    int rows = mListener.onBookSold(listView.getPositionForView(view), newQuantity);
                    if (rows > 0) {
                        Toast.makeText(mContext, "One Book Sold", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Book cannot be sold due to unknown error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "Book not in stock", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    interface InventoryItemClickListener {
        int onBookSold(int position, int newQuantity);
    }
}
