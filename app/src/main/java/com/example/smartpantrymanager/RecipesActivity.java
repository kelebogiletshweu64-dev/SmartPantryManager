package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RecipesActivity extends AppCompatActivity {

    private PantryDatabaseHelper databaseHelper;
    private LinearLayout recipesContainer;

    private final int PINK = Color.parseColor("#E88BAF");
    private final int DARK_PINK = Color.parseColor("#B94F79");
    private final int LAVENDER = Color.parseColor("#8064A2");
    private final int CREAM = Color.parseColor("#FFF8F0");
    private final int DARK_TEXT = Color.parseColor("#493744");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(CREAM);

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(24, 24, 24, 24);

        TextView title = new TextView(this);
        title.setText("🍓 My Lovely Recipes");
        title.setTextSize(28);
        title.setTextColor(DARK_PINK);
        title.setPadding(0, 0, 0, 12);
        mainLayout.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Simple meals made with love ♡");
        subtitle.setTextSize(16);
        subtitle.setTextColor(DARK_TEXT);
        subtitle.setPadding(0, 0, 0, 20);
        mainLayout.addView(subtitle);

        recipesContainer = new LinearLayout(this);
        recipesContainer.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(recipesContainer);

        scrollView.addView(mainLayout);
        setContentView(scrollView);

        databaseHelper = new PantryDatabaseHelper(this);
        displayRecipes();
    }

    private void displayRecipes() {
        recipesContainer.removeAllViews();

        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(
                "SELECT name, ingredients, instructions "
                        + "FROM recipes ORDER BY id ASC",
                null
        );

        try {
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                String ingredients = cursor.getString(1);
                String instructions = cursor.getString(2);

                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(20, 20, 20, 20);
                card.setBackgroundColor(Color.WHITE);

                LinearLayout.LayoutParams cardParams =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                cardParams.setMargins(0, 0, 0, 24);
                card.setLayoutParams(cardParams);

                TextView recipeName = new TextView(this);
                recipeName.setText("♡ " + name);
                recipeName.setTextSize(21);
                recipeName.setTextColor(DARK_PINK);
                recipeName.setPadding(0, 0, 0, 12);
                card.addView(recipeName);

                TextView recipeIngredients = new TextView(this);
                recipeIngredients.setText(
                        "Ingredients:\n• "
                                + ingredients.replace(",", "\n• ")
                );
                recipeIngredients.setTextSize(16);
                recipeIngredients.setTextColor(DARK_TEXT);
                recipeIngredients.setPadding(0, 0, 0, 12);
                card.addView(recipeIngredients);

                TextView recipeInstructions = new TextView(this);
                recipeInstructions.setText(
                        "How to make it:\n" + instructions
                );
                recipeInstructions.setTextSize(16);
                recipeInstructions.setTextColor(DARK_TEXT);
                recipeInstructions.setPadding(0, 0, 0, 16);
                card.addView(recipeInstructions);

                // Create the pink ingredient-check button
                Button checkButton = new Button(this);
                checkButton.setText("♡ Check My Ingredients");
                checkButton.setAllCaps(false);
                checkButton.setTextSize(15);
                checkButton.setTextColor(Color.WHITE);
                checkButton.setBackgroundTintList(
                        ColorStateList.valueOf(PINK)
                );

                checkButton.setOnClickListener(v ->
                        checkIngredients(name, ingredients)
                );

                // Add button to the card BEFORE adding the card to the screen
                card.addView(checkButton);
                recipesContainer.addView(card);
            }

            if (cursor.getCount() == 0) {
                TextView empty = new TextView(this);
                empty.setText("No recipes found yet. 🌷");
                empty.setTextSize(18);
                empty.setTextColor(DARK_PINK);
                recipesContainer.addView(empty);
            }

        } finally {
            cursor.close();
        }
    }

    private void checkIngredients(String recipeName, String ingredients) {
        String[] requiredIngredients = ingredients.toLowerCase().split(",");
        boolean allAvailable = true;

        for (String required : requiredIngredients) {
            boolean found = false;

            Cursor pantryCursor = databaseHelper.getAllPantryItems();

            try {
                while (pantryCursor.moveToNext()) {
                    String pantryName = pantryCursor.getString(
                            pantryCursor.getColumnIndexOrThrow(
                                    PantryDatabaseHelper.COL_NAME
                            )
                    ).toLowerCase().trim();

                    int quantity = pantryCursor.getInt(
                            pantryCursor.getColumnIndexOrThrow(
                                    PantryDatabaseHelper.COL_QUANTITY
                            )
                    );

                    if (pantryName.equals(required.trim())
                            && quantity > 0) {
                        found = true;
                        break;
                    }
                }
            } finally {
                pantryCursor.close();
            }

            if (!found) {
                allAvailable = false;
                break;
            }
        }

        String message;

        if (allAvailable) {
            message = "You have all the listed ingredients! Let's cook. ♡";
        } else {
            message = "You're missing one or more ingredients. "
                    + "Add them to your pantry first. 🌷";
        }

        new AlertDialog.Builder(this)
                .setTitle(recipeName)
                .setMessage(message)
                .setPositiveButton("OK", null)
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