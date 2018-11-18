package com.example.abhishekkoranne.retail_inventory;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.abhishekkoranne.retail_inventory.data.InventoryContract.InventoryEntry;
import com.example.abhishekkoranne.retail_inventory.data.InventoryDbHelper;

public class EditorActivity extends AppCompatActivity {

    private EditText editBookName;
    private EditText editBookPrice;
    private EditText editBookQuantity;
    private EditText editSupplierName;
    private EditText editSupplierContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        editBookName = (EditText) findViewById(R.id.editBookName);
        editBookPrice = (EditText) findViewById(R.id.editBookPrice);
        editBookQuantity = (EditText) findViewById(R.id.editBookQuantity);

        editSupplierName = (EditText) findViewById(R.id.editSupplierName);
        editSupplierContact = (EditText) findViewById(R.id.editSupplierContact);
    }

    private void insertBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = editBookName.getText().toString().trim();

        String priceString = editBookPrice.getText().toString().trim();
        int price = Integer.parseInt(priceString);

        String quantityString = editBookQuantity.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString);

        String supplierString = editSupplierName.getText().toString().trim();
        String supplierContactString = editSupplierContact.getText().toString().trim();

        // Create database helper
        InventoryDbHelper inventoryDbHelper = new InventoryDbHelper(this);

        // Gets the database in write mode
        SQLiteDatabase db = inventoryDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and book attributes from the editor are the values.
        ContentValues values = new ContentValues();

        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, supplierString);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NUMBER, supplierContactString);

        // Insert a new row for pet in the database, returning the ID of that new row.
        long newRowId = db.insert(InventoryEntry.TABLE_NAME, null, values);
        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving book", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Book saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
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
                insertBook();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
