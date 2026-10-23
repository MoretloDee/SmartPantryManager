package com.dikeledi.smartpantrymanager;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private Cursor cursor;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int id, String name, String qty, String unit);
        void onItemLongClick(int id);
    }

    public PantryAdapter(Cursor cursor, OnItemClickListener listener) {
        this.cursor = cursor;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) return;

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
        String qty = String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_QUANTITY)));
        String unit = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_UNIT));

        holder.tvName.setText(name);
        holder.tvQty.setText(qty + " " + unit);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(id, name, qty, unit));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(id);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    public static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQty;
        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvIngredientName);
            tvQty = itemView.findViewById(R.id.tvIngredientQty);
        }
    }
}