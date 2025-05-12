package com.example.medmate;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {
    private final List<Medicine> medicineList;
    private final DatabaseReference databaseReference;
    private final Context context;
    private static final String CHANNEL_ID = "LOW_STOCK_CHANNEL";

    public MedicineAdapter(List<Medicine> medicineList, Context context) {
        this.medicineList = medicineList;
        this.context = context;
        this.databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("medicines");
        createNotificationChannel();
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
        if (position < 0 || position >= medicineList.size()) {
            return;
        }

        Medicine medicine = medicineList.get(position);
        medicine.setId(medicineList.get(position).getId());

        holder.nameTextView.setText(medicine.getName());
        holder.countTextView.setText("Count: " + medicine.getCount());
        holder.timeTextView.setText("Time: " + medicine.getTimeSelection());
        holder.typeTextView.setText("Type: " + medicine.getType());
        holder.dosageTextView.setText("Dosage: " + medicine.getDosage());


        int lowStockThreshold = Integer.parseInt(medicine.getLowStockReminder());
        if (medicine.getCount() <= lowStockThreshold) {
            holder.lowStockWarningTextView.setVisibility(View.VISIBLE);
        } else {
            holder.lowStockWarningTextView.setVisibility(View.GONE);
        }

        holder.checkbox.setOnCheckedChangeListener(null);
        holder.checkbox.setChecked(false);

        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                try {
                    int dosage = Integer.parseInt(medicine.getDosage().replaceAll("[^0-9]", ""));
                    int newCount = medicine.getCount() - dosage;

                    if (newCount >= 0) {
                        databaseReference.child(medicine.getId()).child("count")
                                .setValue(newCount)
                                .addOnSuccessListener(aVoid -> {
                                    medicine.setCount(newCount);
                                    notifyItemChanged(position);

                                    if (newCount <= lowStockThreshold) {
                                        showLowStockNotification(medicine.getName());
                                        holder.lowStockWarningTextView.setVisibility(View.VISIBLE);
                                    }
                                });
                    } else {
                        Toast.makeText(context, "Not enough medicine available!", Toast.LENGTH_SHORT).show();
                        holder.checkbox.setChecked(false);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(context, "Error parsing dosage amount", Toast.LENGTH_SHORT).show();
                    holder.checkbox.setChecked(false);
                }
            }
        });
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Medicine")
                    .setMessage("Are you sure you want to delete " + medicine.getName() + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        deleteMedicineFromFirebase(medicine.getId(), position);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    private void deleteMedicineFromFirebase(String medicineId, int position) {
        databaseReference.child(medicineId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    if (position >= 0 && position < medicineList.size()) {
                        medicineList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, medicineList.size());
                        Toast.makeText(context, "Medicine deleted!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Low Stock Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Alerts when medicine stock is low");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    private void showLowStockNotification(String medicineName) {
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_pill)
                .setContentTitle("Low Stock Alert!")
                .setContentText("Time to reorder " + medicineName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(medicineName.hashCode(), notification);
    }

    public static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, countTextView, timeTextView,
                typeTextView, dosageTextView, lowStockWarningTextView;
        CheckBox checkbox;
        ImageButton btnDelete;

        public MedicineViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.medicine_name);
            countTextView = itemView.findViewById(R.id.medicine_count);
            timeTextView = itemView.findViewById(R.id.medicine_time);
            typeTextView = itemView.findViewById(R.id.medicine_type);
            dosageTextView = itemView.findViewById(R.id.medicine_dosage);
            lowStockWarningTextView = itemView.findViewById(R.id.medicine_low_stock_warning);
            checkbox = itemView.findViewById(R.id.medicine_checkbox);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}