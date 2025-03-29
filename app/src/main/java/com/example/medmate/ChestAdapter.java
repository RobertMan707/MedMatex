package com.example.medmate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChestAdapter extends RecyclerView.Adapter<ChestAdapter.ChestViewHolder> {

    private List<MedicineChest> chestList;

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
        holder.tvChestSize.setText("Capacity: " + chest.getSize() + " medicines");
    }

    @Override
    public int getItemCount() {
        return chestList.size();
    }

    public static class ChestViewHolder extends RecyclerView.ViewHolder {
        TextView tvChestName, tvChestSize;

        public ChestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChestName = itemView.findViewById(R.id.tv_chest_name);
            tvChestSize = itemView.findViewById(R.id.tv_chest_size);
        }
    }
}