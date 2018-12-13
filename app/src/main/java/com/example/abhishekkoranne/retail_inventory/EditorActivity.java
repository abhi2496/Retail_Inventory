package com.example.abhishekkoranne.retail_inventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.abhishekkoranne.retail_inventory.data.InventoryContract.InventoryEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;
    private EditText editBookName;
    private EditText editBookPrice;
    private EditText editBookQuantity;
    private EditText editSupplierName;
    private EditText editSupplierContact;

    private Uri currentProductUri;
    private boolean productHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            productHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        currentProductUri = intent.getData();

        if (currentProductUri == null) {
            setTitle("Add Book");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit Book");
            getLoaderManager().initLoader(PRODUCT_LOADER,null,  this);
        }

        editBookName = (EditText) findViewById(R.id.editBookName);
        editBookPrice = (EditText) findViewById(R.id.editBookPrice);
        editBookQuantity = (EditText) findViewById(R.id.editBookQuantity);

        editSupplierName = (EditText) findViewById(R.id.editSupplierName);
        editSupplierContact = (EditText) findViewById(R.id.editSupplierContact);

        editBookName.setOnTouchListener(mTouchListener);
        editBookPrice.setOnTouchListener(mTouchListener);
        editBookQuantity.setOnTouchListener(mTouchListener);
        editSupplierName.setOnTouchListener(mTouchListener);
        editSupplierContact.setOnTouchListener(mTouchListener);


    }

    private void saveBook() {

        String nameString = editBookName.getText().toString().trim();
        String price = editBookPrice.getText().toString().trim();
        String quantity = editBookQuantity.getText().toString().trim();
        String supplierString = editSupplierName.getText().toString().trim();
        String supplierContactString = editSupplierContact.getText().toString().trim();

        if (currentProductUri == null
                && TextUtils.isEmpty(nameString)
                && TextUtils.isEmpty(price)
                && TextUtils.isEmpty(quantity)
                && TextUtils.isEmpty(supplierString)
                && TextUtils.isEmpty(supplierContactString)) {
            Toast.makeText(this, "Fields are empty", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(price) ||
                TextUtils.isEmpty(quantity) || TextUtils.isEmpty(supplierString) ||
                TextUtils.isEmpty(supplierContactString)) {
            Toast.makeText(this, "Field can't be empty", Toast.LENGTH_SHORT).show();
        } else {

            // Create a ContentValues object where column names are the keys,
            // and book attributes from the editor are the values.
            ContentValues values = new ContentValues();

            values.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
            values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, price);
            values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity);
            values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, supplierString);
            values.put(InventoryEntry.COLUMN_SUPPLIER_NUMBER, supplierContactString);

            if (currentProductUri == null) {
                Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
                if (newUri == null) {
                    Toast.makeText(EditorActivity.this, "Insertion failed", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditorActivity.this, "Book added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);

                if (rowsAffected == 0) {
                    Toast.makeText(this, "Failed!!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Book Updated",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                saveBook();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();

                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!productHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard changes?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
        if (currentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(currentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, "Delete Book Failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Book deleted",
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if (!productHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
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
/*
        return new CursorLoader(this,   // Parent activity context
                currentProductUri,         // Query the content URI for the current product
                projection,                // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
*/

        return new CursorLoader(this,
                currentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        int bookNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME );
        int supplierPhoneNoColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NUMBER);

        if (cursor.moveToNext()) {
            String bookName = cursor.getString(bookNameColumnIndex);
            Float price = cursor.getFloat(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String phoneNo = cursor.getString(supplierPhoneNoColumnIndex);

            editBookName.setText(bookName);
            editBookPrice.setText(String.valueOf(price));
            editBookQuantity.setText(String.valueOf(quantity));
            editSupplierName.setText(supplierName);
            editSupplierContact.setText(phoneNo);
        }

    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        editBookName.setText("");
        editBookPrice.setText("");
        editBookQuantity.setText("");
        editSupplierName.setText("");
        editSupplierContact.setText("");
    }

/*
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        int bookNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME );
        int supplierPhoneNoColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NUMBER);

        if (cursor.moveToNext()) {
            String bookName = cursor.getString(bookNameColumnIndex);
            Float price = cursor.getFloat(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String phoneNo = cursor.getString(supplierPhoneNoColumnIndex);

            editBookName.setText(bookName);
            editBookPrice.setText(String.valueOf(price));
            editBookQuantity.setText(String.valueOf(quantity));
            editSupplierName.setText(supplierName);
            editSupplierContact.setText(phoneNo);
        }
    }
*/

/*
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        editBookName.setText("");
        editBookPrice.setText("");
        editBookQuantity.setText("");
        editSupplierName.setText("");
        editSupplierContact.setText("");

    }
*/
}