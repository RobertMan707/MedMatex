package com.example.medmate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {
    private final List<Medicine> medicineList;

    public MedicineAdapter(List<Medicine> medicineList) {
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);

        holder.nameTextView.setText(medicine.getName());
        holder.countTextView.setText("Count: " + medicine.getCount());
        holder.timeTextView.setText("Time: " + medicine.getTimeSelection());
        holder.typeTextView.setText("Type: " + medicine.getType());
        holder.dosageTextView.setText("Dosage: " + medicine.getDosage());

        if (medicine.getCount() <= Integer.parseInt(medicine.getLowStockReminder())) {
            holder.lowStockWarningTextView.setVisibility(View.VISIBLE);
        } else {
            holder.lowStockWarningTextView.setVisibility(View.GONE);
        }

        holder.checkbox.setChecked(medicine.isTaken());
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, countTextView, timeTextView,
                typeTextView, dosageTextView, lowStockWarningTextView;
        CheckBox checkbox;

        public MedicineViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.medicine_name);
            countTextView = itemView.findViewById(R.id.medicine_count);
            timeTextView = itemView.findViewById(R.id.medicine_time);
            typeTextView = itemView.findViewById(R.id.medicine_type);
            dosageTextView = itemView.findViewById(R.id.medicine_dosage);
            lowStockWarningTextView = itemView.findViewById(R.id.medicine_low_stock_warning);
            checkbox = itemView.findViewById(R.id.medicine_checkbox);
        }
    }
}