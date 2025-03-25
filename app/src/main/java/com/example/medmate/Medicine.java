package com.example.medmate;

public class Medicine {
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
    public String getName() { return name; }
    public int getCount() { return count; }
    public String getTimeSelection() { return timeSelection; }
    public String getType() { return type; }
    public boolean isTaken() { return taken; }
    public String getDaysFrequency() { return daysFrequency; }
    public String getDosage() { return dosage; }
    public String getLowStockReminder() { return lowStockReminder; }
    public String getMedicineStock() { return medicineStock; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setCount(int count) { this.count = count; }
    public void setTimeSelection(String timeSelection) { this.timeSelection = timeSelection; }
    public void setType(String type) { this.type = type; }
    public void setTaken(boolean taken) { this.taken = taken; }
    public void setDaysFrequency(String daysFrequency) { this.daysFrequency = daysFrequency; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public void setLowStockReminder(String lowStockReminder) { this.lowStockReminder = lowStockReminder; }
    public void setMedicineStock(String medicineStock) { this.medicineStock = medicineStock; }
}