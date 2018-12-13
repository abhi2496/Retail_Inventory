package com.example.abhishekkoranne.retail_inventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.abhishekkoranne.retail_inventory.data.InventoryContract.InventoryEntry;

public class InventoryDetails extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor>{

    private Uri mCurrentProductUri;
    private static final int PRODUCT_LOADER = 0;
    private TextView mBookTextView;
    private TextView mPriceTextView;
    private TextView mSupplierNameTextView;
    private TextView mSupplierPhoneNoTextView;
    private TextView mQuantityTextView;
    private Button mOrderButton;
    private Button mIncrementButton;
    private Button mDecrementButton;
    private int quantityToBeChanged = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_details);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        mBookTextView = findViewById(R.id.details_book_name);
        mPriceTextView = findViewById(R.id.details_book_price);
        mSupplierNameTextView = findViewById(R.id.details_supplier_name);
        mSupplierPhoneNoTextView = findViewById(R.id.details_supplier_phone_no);
        mQuantityTextView = findViewById(R.id.details_quantity);
        mOrderButton = findViewById(R.id.order_button);
        mIncrementButton = findViewById(R.id.increment_button);
        mDecrementButton = findViewById(R.id.decrement_button);

        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(InventoryDetails.this, EditorActivity.class);
                intent.setData(mCurrentProductUri);
                startActivity(intent);
                finish();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(InventoryDetails.this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this book?");
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, "Delete book failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Book deleted",
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_NUMBER
        };

        return new CursorLoader(this,   // Parent activity context
                mCurrentProductUri,         // Query the content URI for the current product
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        int bookNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
        int supplierPhoneNoColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NUMBER);

        if (cursor.moveToNext()) {
            String bookName = cursor.getString(bookNameColumnIndex);
            Float price = cursor.getFloat(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            final String phoneNo = cursor.getString(supplierPhoneNoColumnIndex);

            mBookTextView.setText(bookName);
            mPriceTextView.setText("₹ " + String.valueOf(price));
            mQuantityTextView.setText(String.valueOf(quantity));
            mSupplierNameTextView.setText(supplierName);
            mSupplierPhoneNoTextView.setText(phoneNo);

            mIncrementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String quantity = mQuantityTextView.getText().toString();
                    int newQuantity = Integer.valueOf(quantity) + quantityToBeChanged;
                    if (newQuantity >= 0) {
                        ContentValues values = new ContentValues();
                        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                        getContentResolver().update(mCurrentProductUri, values, null, null);
                        mQuantityTextView.setText(String.valueOf(newQuantity));
                    }
                }
            });

            mDecrementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String quantity = mQuantityTextView.getText().toString();
                    int newQuantity = Integer.valueOf(quantity) - quantityToBeChanged;
                    if (newQuantity >= 0) {
                        ContentValues values = new ContentValues();
                        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                        getContentResolver().update(mCurrentProductUri, values, null, null);
                        mQuantityTextView.setText(String.valueOf(newQuantity));
                    } else {
                        Toast.makeText(InventoryDetails.this,"Books can't be negative", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = "tel:" + phoneNo;
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(uri));
                    startActivity(intent);
                }
            });

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
