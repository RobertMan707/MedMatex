package com.example.medmate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChestMedicineAdapter extends RecyclerView.Adapter<ChestMedicineAdapter.MedicineViewHolder> {
    private List<ChestMedicine> medicineList;

    public ChestMedicineAdapter(List<ChestMedicine> medicineList) {
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.medicine_item, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        ChestMedicine medicine = medicineList.get(position);
        holder.medicineName.setText(medicine.getName());
        holder.expiryDate.setText("Expires: " + medicine.getExpiry_date());
        holder.reminderDays.setText("Remind " + medicine.getReminder_days() + " days before expiry");
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView medicineName, expiryDate, reminderDays;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            medicineName = itemView.findViewById(R.id.medicine_name);
            expiryDate = itemView.findViewById(R.id.expiry_date);
            reminderDays = itemView.findViewById(R.id.reminder_days);
        }
    }
}