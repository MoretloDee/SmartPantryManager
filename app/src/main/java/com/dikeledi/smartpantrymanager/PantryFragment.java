package com.dikeledi.smartpantrymanager;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PantryFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private PantryAdapter adapter;
    private Cursor currentCursor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pantry, container, false);

        dbHelper = new DatabaseHelper(getContext());
        recyclerView = view.findViewById(R.id.recyclerViewPantry);
        tvEmpty = view.findViewById(R.id.tvEmptyPantry);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        view.findViewById(R.id.fabAddIngredient).setOnClickListener(v -> showItemDialog(-1, "", "", ""));

        loadPantryData();
        return view;
    }

    private void loadPantryData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        currentCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PANTRY, null);

        if (currentCursor.getCount() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        adapter = new PantryAdapter(currentCursor, new PantryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int id, String name, String qty, String unit) {
                // Edit mode
                showItemDialog(id, name, qty, unit);
            }

            @Override
            public void onItemLongClick(int id) {
                // Delete mode
                confirmDelete(id);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void showItemDialog(int id, String name, String qty, String unit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(id == -1 ? "Add Pantry Item" : "Edit Pantry Item");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_ingredient, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.etIngredientName);
        EditText etQty = dialogView.findViewById(R.id.etIngredientQty);
        EditText etUnit = dialogView.findViewById(R.id.etIngredientUnit);

        if (id != -1) {
            etName.setText(name);
            etQty.setText(qty);
            etUnit.setText(unit);
        }

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = etName.getText().toString().trim();
            String newQtyStr = etQty.getText().toString().trim();
            String newUnit = etUnit.getText().toString().trim();

            // INPUT VALIDATION
            if (newName.isEmpty() || newQtyStr.isEmpty() || newUnit.isEmpty()) {
                Toast.makeText(getContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            double newQty;
            try {
                newQty = Double.parseDouble(newQtyStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Quantity must be a number!", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (id == -1) {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_PANTRY + " (" + DatabaseHelper.COL_NAME + ", " + DatabaseHelper.COL_QUANTITY + ", " + DatabaseHelper.COL_UNIT + ") VALUES ('" + newName + "', " + newQty + ", '" + newUnit + "')");
            } else {
                db.execSQL("UPDATE " + DatabaseHelper.TABLE_PANTRY + " SET " + DatabaseHelper.COL_NAME + "='" + newName + "', " + DatabaseHelper.COL_QUANTITY + "=" + newQty + ", " + DatabaseHelper.COL_UNIT + "='" + newUnit + "' WHERE " + DatabaseHelper.COL_ID + "=" + id);
            }
            loadPantryData();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void confirmDelete(int id) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to remove this from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.execSQL("DELETE FROM " + DatabaseHelper.TABLE_PANTRY + " WHERE " + DatabaseHelper.COL_ID + "=" + id);
                    loadPantryData();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dbHelper != null) {
            loadPantryData();
        }
    }
}