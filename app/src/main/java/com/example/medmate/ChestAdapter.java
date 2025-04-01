package com.example.medmate;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChestAdapter extends RecyclerView.Adapter<ChestAdapter.ChestViewHolder> {

    private List<MedicineChest> chestList;

    // Constructor
    public ChestAdapter(List<MedicineChest> chestList) {
        this.chestList = chestList;
    }

    @NonNull
    @Override
    public ChestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chest, parent, false);
        return new ChestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChestViewHolder holder, int position) {
        MedicineChest chest = chestList.get(position);
        holder.tvChestName.setText(chest.getName());
        holder.tvChestSize.setText("Capacity: " + chest.getSize());

        // Set the chestId as a tag for the "Add" button
        holder.itemView.setTag(chest.getChestId());
    }

    @Override
    public int getItemCount() {
        return chestList.size();
    }

    // ViewHolder class
    public static class ChestViewHolder extends RecyclerView.ViewHolder {
        TextView tvChestName, tvChestSize;
        Button btnAddMedicine, btnShowMedicines;

        public ChestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChestName = itemView.findViewById(R.id.tv_chest_name);
            tvChestSize = itemView.findViewById(R.id.tv_chest_size);
            btnAddMedicine = itemView.findViewById(R.id.btn_add_medicine);
            btnShowMedicines = itemView.findViewById(R.id.btn_show_medicines);

            btnAddMedicine.setOnClickListener(v -> {
                String chestId = (String) itemView.getTag();
                Intent intent = new Intent(itemView.getContext(), AddMedicineActivity.class);
                intent.putExtra("CHEST_ID", chestId);
                itemView.getContext().startActivity(intent);
            });


        }
    }

    // Update data method (for refreshing the list)
    public void updateData(List<MedicineChest> newChestList) {
        chestList = newChestList;
        notifyDataSetChanged();
    }
}