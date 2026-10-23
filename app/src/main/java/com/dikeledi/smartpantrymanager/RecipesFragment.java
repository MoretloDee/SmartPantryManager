package com.dikeledi.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecipesFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private TextView tvNoRecipes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);

        dbHelper = new DatabaseHelper(getContext());
        recyclerView = view.findViewById(R.id.recyclerViewRecipes);
        tvNoRecipes = view.findViewById(R.id.tvNoRecipes); // We need to add this to the XML
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadSuggestions();
        return view;
    }

    private void loadSuggestions() {
        List<String> suggestions = dbHelper.getSuggestedRecipes();

        if (suggestions.isEmpty()) {
            tvNoRecipes.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoRecipes.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        RecipeAdapter adapter = new RecipeAdapter(suggestions, recipeName -> {
            Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
            intent.putExtra("RECIPE_NAME", recipeName);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dbHelper != null) {
            loadSuggestions();
        }
    }
}