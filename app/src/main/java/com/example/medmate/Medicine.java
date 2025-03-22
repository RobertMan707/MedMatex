package com.example.medmate;

public class Medicine {
    private String name;
    private int count;
    private String time;
    private String type;
    private boolean taken;
    private String daysFrequency;
    private String dosage;
    private String lowStockReminder;
    private String medicineStock;

    public Medicine(String name, int count, String time, String type, boolean taken,
                    String daysFrequency, String dosage, String lowStockReminder, String medicineStock) {
        this.name = name;
        this.count = count;
        this.time = time;
        this.type = type;
        this.taken = taken;
        this.daysFrequency = daysFrequency;
        this.dosage = dosage;
        this.lowStockReminder = lowStockReminder;
        this.medicineStock = medicineStock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public String getDaysFrequency() {
        return daysFrequency;
    }

    public void setDaysFrequency(String daysFrequency) {
        this.daysFrequency = daysFrequency;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getLowStockReminder() {
        return lowStockReminder;
    }

    public void setLowStockReminder(String lowStockReminder) {
        this.lowStockReminder = lowStockReminder;
    }

    public String getMedicineStock() {
        return medicineStock;
    }

    public void setMedicineStock(String medicineStock) {
        this.medicineStock = medicineStock;
    }
}
