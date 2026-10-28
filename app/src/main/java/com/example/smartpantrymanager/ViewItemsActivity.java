package com.example.smartpantrymanager;

import android.os.Bundle;
import android.database.Cursor;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.text.InputType;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;

public class ViewItemsActivity extends AppCompatActivity {

    private LinearLayout itemsContainer;
    private PantryDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_items);

        itemsContainer = findViewById(R.id.itemsContainer);
        databaseHelper = new PantryDatabaseHelper(this);

        displayPantryItems();
    }

    private void displayPantryItems() {
        itemsContainer.removeAllViews();

        Cursor cursor = databaseHelper.getAllPantryItems();

        if (cursor == null) {
            Toast.makeText(this,
                    "Could not load pantry items.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (cursor.getCount() == 0) {
            TextView emptyMessage = new TextView(this);
            emptyMessage.setText("Your pantry is empty. Add an item first!");
            emptyMessage.setTextSize(18);
            emptyMessage.setPadding(16, 24, 16, 24);
            itemsContainer.addView(emptyMessage);
            cursor.close();
            return;
        }

        try {
            while (cursor.moveToNext()) {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COL_ID));

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COL_NAME));

                int quantity = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COL_QUANTITY));

                String category = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COL_CATEGORY));

                String expiry = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                PantryDatabaseHelper.COL_EXPIRY));

                LinearLayout itemLayout = new LinearLayout(this);
                itemLayout.setOrientation(LinearLayout.VERTICAL);
                itemLayout.setPadding(20, 20, 20, 20);

                LinearLayout.LayoutParams itemParams =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);

                itemParams.setMargins(0, 0, 0, 20);
                itemLayout.setLayoutParams(itemParams);

                TextView itemDetails = new TextView(this);
                itemDetails.setText(
                        "Item: " + name +
                                "\nQuantity: " + quantity +
                                "\nCategory: " + category +
                                "\nExpiry Date: " + expiry +
                                "\nItem ID: " + id
                );
                itemDetails.setTextSize(16);
                itemDetails.setPadding(0, 0, 0, 12);

                itemLayout.addView(itemDetails);

                LinearLayout buttonLayout = new LinearLayout(this);
                buttonLayout.setOrientation(LinearLayout.HORIZONTAL);


                Button editButton = new Button(this);
                editButton.setText("♡ Edit");
                editButton.setTextColor(Color.WHITE);
                editButton.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                Color.parseColor("#E88BAF")
                        )
                );

                Button deleteButton = new Button(this);
                deleteButton.setText("✿ Delete");
                deleteButton.setTextColor(Color.WHITE);
                deleteButton.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                Color.parseColor("#8064A2")
                        )
                );


                LinearLayout.LayoutParams buttonParams =
                        new LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                1);

                buttonParams.setMargins(4, 0, 4, 0);

                editButton.setLayoutParams(buttonParams);
                deleteButton.setLayoutParams(buttonParams);

                editButton.setOnClickListener(
                        v -> showEditDialog(
                                id, name, quantity, category, expiry));

                deleteButton.setOnClickListener(
                        v -> confirmDelete(id, name));

                buttonLayout.addView(editButton);
                buttonLayout.addView(deleteButton);

                itemLayout.addView(buttonLayout);
                itemsContainer.addView(itemLayout);
            }
        } finally {
            cursor.close();
        }
    }

    private void showEditDialog(
            int id,
            String oldName,
            int oldQuantity,
            String oldCategory,
            String oldExpiry) {

        LinearLayout formLayout = new LinearLayout(this);
        formLayout.setOrientation(LinearLayout.VERTICAL);
        formLayout.setPadding(40, 20, 40, 10);

        EditText nameInput = new EditText(this);
        nameInput.setHint("Item name");
        nameInput.setText(oldName);

        EditText quantityInput = new EditText(this);
        quantityInput.setHint("Quantity");
        quantityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        quantityInput.setText(String.valueOf(oldQuantity));

        EditText categoryInput = new EditText(this);
        categoryInput.setHint("Category");
        categoryInput.setText(oldCategory);

        EditText expiryInput = new EditText(this);
        expiryInput.setHint("Expiry date (yyyy-MM-dd)");
        expiryInput.setText(oldExpiry);

        formLayout.addView(nameInput);
        formLayout.addView(quantityInput);
        formLayout.addView(categoryInput);
        formLayout.addView(expiryInput);

        new AlertDialog.Builder(this)
                .setTitle("Edit Pantry Item")
                .setView(formLayout)
                .setPositiveButton("Save", (dialog, which) -> {

                    String newName =
                            nameInput.getText().toString().trim();

                    String quantityText =
                            quantityInput.getText().toString().trim();

                    String newCategory =
                            categoryInput.getText().toString().trim();

                    String newExpiry =
                            expiryInput.getText().toString().trim();

                    if (newName.isEmpty()
                            || quantityText.isEmpty()
                            || newCategory.isEmpty()
                            || newExpiry.isEmpty()) {

                        Toast.makeText(this,
                                "Please complete all fields.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int newQuantity;

                    try {
                        newQuantity = Integer.parseInt(quantityText);

                        if (newQuantity < 0) {
                            Toast.makeText(this,
                                    "Quantity cannot be negative.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                    } catch (NumberFormatException e) {
                        Toast.makeText(this,
                                "Enter a valid quantity.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean updated = databaseHelper.updatePantryItem(
                            id,
                            newName,
                            newQuantity,
                            newCategory,
                            newExpiry
                    );

                    if (updated) {
                        Toast.makeText(this,
                                "Item updated successfully.",
                                Toast.LENGTH_SHORT).show();

                        displayPantryItems();
                    } else {
                        Toast.makeText(this,
                                "Could not update item.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDelete(int id, String name) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Pantry Item")
                .setMessage("Are you sure you want to delete "
                        + name + "?")
                .setPositiveButton("Delete", (dialog, which) -> {

                    boolean deleted =
                            databaseHelper.deletePantryItem(id);

                    if (deleted) {
                        Toast.makeText(this,
                                "Item deleted successfully.",
                                Toast.LENGTH_SHORT).show();

                        displayPantryItems();
                    } else {
                        Toast.makeText(this,
                                "Could not delete item.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }

        super.onDestroy();
    }
}