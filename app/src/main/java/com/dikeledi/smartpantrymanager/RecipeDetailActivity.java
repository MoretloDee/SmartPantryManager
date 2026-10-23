package com.dikeledi.smartpantrymanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        String recipeName = getIntent().getStringExtra("RECIPE_NAME");

        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvIngredients = findViewById(R.id.tvDetailIngredients);
        TextView tvMethod = findViewById(R.id.tvDetailMethod);

        tvTitle.setText(recipeName);

        // Fetch details from DB
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + DatabaseHelper.COL_RECIPE_INGREDIENTS + ", " + DatabaseHelper.COL_RECIPE_METHOD + " FROM " + DatabaseHelper.TABLE_RECIPES + " WHERE " + DatabaseHelper.COL_RECIPE_NAME + " = ?", new String[]{recipeName});

        if (cursor.moveToFirst()) {
            tvIngredients.setText("Ingredients:\n" + cursor.getString(0).replace(",", ", "));
            tvMethod.setText("Method:\n" + cursor.getString(1));
        }
        cursor.close();
    }
}