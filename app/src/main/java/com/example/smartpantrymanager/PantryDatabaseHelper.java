
package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PantryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_PANTRY = "pantry_items";

    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_CATEGORY = "category";
    public static final String COL_EXPIRY = "expiry_date";

    public PantryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_PANTRY + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_QUANTITY + " INTEGER NOT NULL, "
                + COL_CATEGORY + " TEXT NOT NULL, "
                + COL_EXPIRY + " TEXT NOT NULL"
                + ")";

        db.execSQL(createTable);


        String createRecipesTable = "CREATE TABLE recipes ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "ingredients TEXT NOT NULL, "
                + "instructions TEXT NOT NULL"
                + ")";

        db.execSQL(createRecipesTable);

        ContentValues recipe1 = new ContentValues();
        recipe1.put("name", "Tomato & Egg Scramble");
        recipe1.put("ingredients", "eggs,tomatoes,cooking oil");
        recipe1.put("instructions",
                "1. Heat a little cooking oil in a pan. "
                        + "2. Add chopped tomatoes. "
                        + "3. Add beaten eggs and stir until cooked.");
        db.insert("recipes", null, recipe1);

        ContentValues recipe2 = new ContentValues();
        recipe2.put("name", "Simple Rice & Beans");
        recipe2.put("ingredients", "rice,beans,cooking oil");
        recipe2.put("instructions",
                "1. Cook the rice. "
                        + "2. Heat the beans separately. "
                        + "3. Mix or serve together with a little cooking oil.");
        db.insert("recipes", null, recipe2);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {

        if (oldVersion < 2) {
            String createRecipesTable =
                    "CREATE TABLE IF NOT EXISTS recipes ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "name TEXT NOT NULL, "
                            + "ingredients TEXT NOT NULL, "
                            + "instructions TEXT NOT NULL"
                            + ")";

            db.execSQL(createRecipesTable);

            ContentValues recipe1 = new ContentValues();
            recipe1.put("name", "Tomato & Egg Scramble");
            recipe1.put("ingredients", "eggs,tomatoes,cooking oil");
            recipe1.put("instructions",
                    "1. Heat cooking oil in a pan. "
                            + "2. Add chopped tomatoes. "
                            + "3. Add beaten eggs and stir until cooked.");

            db.insert("recipes", null, recipe1);

            ContentValues recipe2 = new ContentValues();
            recipe2.put("name", "Simple Rice & Beans");
            recipe2.put("ingredients", "rice,beans,cooking oil");
            recipe2.put("instructions",
                    "1. Cook the rice. "
                            + "2. Heat the beans separately. "
                            + "3. Serve together with a little cooking oil.");

            db.insert("recipes", null, recipe2);
        }
    }

    public boolean insertPantryItem(
            String name,
            int quantity,
            String category,
            String expiry) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_QUANTITY, quantity);
        values.put(COL_CATEGORY, category);
        values.put(COL_EXPIRY, expiry);

        long result = db.insert(TABLE_PANTRY, null, values);

        return result != -1;
    }

    public Cursor getAllPantryItems() {

        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,
                COL_NAME + " ASC"
        );
    }

    public boolean deletePantryItem(int itemId) {
        SQLiteDatabase db = getWritableDatabase();

        int rowsDeleted = db.delete(
                TABLE_PANTRY,
                COL_ID + " = ?",
                new String[]{String.valueOf(itemId)}
        );

        return rowsDeleted > 0;
    }

    public boolean updatePantryItem(
            int itemId,
            String name,
            int quantity,
            String category,
            String expiry) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_QUANTITY, quantity);
        values.put(COL_CATEGORY, category);
        values.put(COL_EXPIRY, expiry);

        int rowsUpdated = db.update(
                TABLE_PANTRY,
                values,
                COL_ID + " = ?",
                new String[]{String.valueOf(itemId)}
        );

        return rowsUpdated > 0;
    }

}
