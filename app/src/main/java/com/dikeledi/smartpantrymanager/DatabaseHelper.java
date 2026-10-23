package com.dikeledi.smartpantrymanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 1;

    // Pantry Table
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";

    // Recipe Table
    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "recipe_id";
    public static final String COL_RECIPE_NAME = "recipe_name";
    public static final String COL_RECIPE_INGREDIENTS = "ingredients";
    public static final String COL_RECIPE_METHOD = "method";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPantryTable = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_QUANTITY + " REAL, " +
                COL_UNIT + " TEXT)";
        db.execSQL(createPantryTable);

        String createRecipesTable = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_NAME + " TEXT, " +
                COL_RECIPE_INGREDIENTS + " TEXT, " +
                COL_RECIPE_METHOD + " TEXT)";
        db.execSQL(createRecipesTable);

        // Pre-load Recipes
        insertRecipe(db, "Cheese Omelette", "egg,cheese,butter", "1. Whisk eggs. 2. Melt butter in pan. 3. Cook eggs. 4. Add cheese.");
        insertRecipe(db, "Tomato Pasta", "pasta,tomato,garlic,olive oil", "1. Boil pasta. 2. Fry garlic in oil. 3. Add tomatoes. 4. Mix with pasta.");
        insertRecipe(db, "French Toast", "bread,egg,milk,sugar", "1. Mix egg and milk. 2. Dip bread. 3. Fry until golden.");
        insertRecipe(db, "Scrambled Eggs", "egg,butter,salt", "1. Whisk eggs with salt. 2. Melt butter. 3. Scramble until cooked.");
        insertRecipe(db, "Garlic Bread", "bread,garlic,butter", "1. Mix crushed garlic and butter. 2. Spread on bread. 3. Bake until crispy.");
        insertRecipe(db, "Pancakes", "flour,milk,egg,sugar", "1. Mix dry ingredients. 2. Add wet ingredients. 3. Fry on pan.");
        insertRecipe(db, "Grilled Cheese", "bread,cheese,butter", "1. Butter bread. 2. Add cheese. 3. Grill until melted.");
        insertRecipe(db, "Tomato Soup", "tomato,onion,garlic,stock", "1. Fry onions and garlic. 2. Add tomatoes and stock. 3. Blend smooth.");
        insertRecipe(db, "Fried Rice", "rice,egg,soy sauce,oil", "1. Cook rice. 2. Scramble egg. 3. Fry rice with egg and soy sauce.");
        insertRecipe(db, "Mashed Potatoes", "potato,butter,milk,salt", "1. Boil potatoes. 2. Mash with butter and milk.");
        insertRecipe(db, "Banana Smoothie", "banana,milk,honey", "1. Blend all ingredients until smooth.");
        insertRecipe(db, "Greek Salad", "tomato,cucumber,feta,olive oil", "1. Chop veg. 2. Mix with feta and oil.");
        insertRecipe(db, "Tuna Sandwich", "bread,tuna,mayo", "1. Mix tuna and mayo. 2. Put on bread.");
        insertRecipe(db, "Bean Wrap", "tortilla,beans,cheese", "1. Heat beans. 2. Put beans and cheese on tortilla. 3. Roll up.");
        insertRecipe(db, "Chicken Stir-fry", "chicken,rice,soy sauce,oil", "1. Cook chicken. 2. Fry with rice and sauce.");
    }

    private void insertRecipe(SQLiteDatabase db, String name, String ingredients, String method) {
        String sql = "INSERT INTO " + TABLE_RECIPES + " (" + COL_RECIPE_NAME + ", " + COL_RECIPE_INGREDIENTS + ", " + COL_RECIPE_METHOD + ") VALUES ('" + name + "', '" + ingredients + "', '" + method + "')";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    // --- THE STRICT MATCHING CORE LOGIC ---
    public List<String> getSuggestedRecipes() {
        List<String> suggested = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // 1. Get all pantry items into a list
        List<String> pantryItems = new ArrayList<>();
        Cursor pantryCursor = db.rawQuery("SELECT " + COL_NAME + " FROM " + TABLE_PANTRY, null);
        while (pantryCursor.moveToNext()) {
            pantryItems.add(pantryCursor.getString(0).toLowerCase().trim());
        }
        pantryCursor.close();

        // 2. Get all recipes
        Cursor recipeCursor = db.rawQuery("SELECT " + COL_RECIPE_NAME + ", " + COL_RECIPE_INGREDIENTS + " FROM " + TABLE_RECIPES, null);
        while (recipeCursor.moveToNext()) {
            String recipeName = recipeCursor.getString(0);
            String[] requiredIngredients = recipeCursor.getString(1).split(",");

            boolean canMakeIt = true;
            // 3. Strict check: Are ALL ingredients in the pantry?
            for (String req : requiredIngredients) {
                String cleanReq = req.trim().toLowerCase();
                boolean hasIngredient = false;
                for (String pantryItem : pantryItems) {
                    // Simple plural/case matching
                    if (pantryItem.contains(cleanReq) || cleanReq.contains(pantryItem) ||
                            pantryItem.equals(cleanReq + "s") || cleanReq.equals(pantryItem + "s")) {
                        hasIngredient = true;
                        break;
                    }
                }
                if (!hasIngredient) {
                    canMakeIt = false;
                    break;
                }
            }

            if (canMakeIt) {
                suggested.add(recipeName);
            }
        }
        recipeCursor.close();
        return suggested;
    }
}