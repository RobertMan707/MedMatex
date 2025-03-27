package com.example.medmate;

public class Medicine {
    private String id;  // Added Firebase key field
    private String name;
    private int count;
    private String timeSelection;
    private String type;
    private boolean taken;
    private String daysFrequency;
    private String dosage;
    private String lowStockReminder;
    private String medicineStock;

    // Empty constructor required for Firebase
    public Medicine() {}

    public Medicine(String name, int count, String timeSelection, String type,
                    boolean taken, String daysFrequency, String dosage,
                    String lowStockReminder, String medicineStock) {
        this.name = name;
        this.count = count;
        this.timeSelection = timeSelection;
        this.type = type;
        this.taken = taken;
        this.daysFrequency = daysFrequency;
        this.dosage = dosage;
        this.lowStockReminder = lowStockReminder;
        this.medicineStock = medicineStock;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name != null ? name : ""; }
    public int getCount() { return count; }
    public String getTimeSelection() { return timeSelection != null ? timeSelection : ""; }
    public String getType() { return type != null ? type : ""; }
    public boolean isTaken() { return taken; }
    public String getDaysFrequency() { return daysFrequency != null ? daysFrequency : ""; }
    public String getDosage() { return dosage != null ? dosage : "1"; }  // Default dosage
    public String getLowStockReminder() { return lowStockReminder != null ? lowStockReminder : "5"; } // Default threshold
    public String getMedicineStock() { return medicineStock != null ? medicineStock : "0"; }

    // Setters with null checks
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name != null ? name : ""; }
    public void setCount(int count) { this.count = Math.max(count, 0); } // Prevent negative counts
    public void setTimeSelection(String timeSelection) { this.timeSelection = timeSelection != null ? timeSelection : ""; }
    public void setType(String type) { this.type = type != null ? type : ""; }
    public void setTaken(boolean taken) { this.taken = taken; }
    public void setDaysFrequency(String daysFrequency) { this.daysFrequency = daysFrequency != null ? daysFrequency : ""; }
    public void setDosage(String dosage) { this.dosage = dosage != null ? dosage : "1"; }
    public void setLowStockReminder(String lowStockReminder) { this.lowStockReminder = lowStockReminder != null ? lowStockReminder : "5"; }
    public void setMedicineStock(String medicineStock) { this.medicineStock = medicineStock != null ? medicineStock : "0"; }

    // Helper methods
    public int getParsedDosage() {
        try {
            return Integer.parseInt(getDosage().replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 1; // Default fallback
        }
    }

    public int getParsedLowStockReminder() {
        try {
            return Integer.parseInt(getLowStockReminder());
        } catch (NumberFormatException e) {
            return 5; // Default fallback
        }
    }

    public boolean isLowStock() {
        return count <= getParsedLowStockReminder();
    }
}