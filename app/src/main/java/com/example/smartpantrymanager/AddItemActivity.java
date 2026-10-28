package com.example.smartpantrymanager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddItemActivity extends Activity {

    private EditText itemName;
    private EditText itemQuantity;
    private EditText itemCategory;
    private EditText itemExpiry;
    private Button saveButton;

    private PantryDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        databaseHelper = new PantryDatabaseHelper(this);

        itemName = findViewById(R.id.itemName);
        itemQuantity = findViewById(R.id.itemQuantity);
        itemCategory = findViewById(R.id.itemCategory);
        itemExpiry = findViewById(R.id.itemExpiry);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveItem();
            }
        });
    }

    private void saveItem() {
        String name = itemName.getText().toString().trim();
        String quantityText = itemQuantity.getText().toString().trim();
        String category = itemCategory.getText().toString().trim();
        String expiry = itemExpiry.getText().toString().trim();

        if (name.isEmpty() || quantityText.isEmpty()
                || category.isEmpty() || expiry.isEmpty()) {
            Toast.makeText(this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;

        try {
            quantity = Integer.parseInt(quantityText);

            if (quantity <= 0) {
                Toast.makeText(this,
                        "Quantity must be greater than zero",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this,
                    "Please enter a valid quantity",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        boolean saved = databaseHelper.insertPantryItem(
                name, quantity, category, expiry
        );

        if (saved) {
            Toast.makeText(this,
                    "Pantry item saved successfully!",
                    Toast.LENGTH_LONG).show();

            itemName.setText("");
            itemQuantity.setText("");
            itemCategory.setText("");
            itemExpiry.setText("");

            finish();
        } else {
            Toast.makeText(this,
                    "Failed to save pantry item",
                    Toast.LENGTH_LONG).show();
        }
    }
}
