package com.example.medmate;

public class ChestMedicine {
    private String medID;
    private String name;
    private String expiry_date;
    private String reminder_days;
    private String key; // Add this field

    public ChestMedicine() {}

    public ChestMedicine(String medID, String name, String expiry_date, String reminder_days) {
        this.medID = medID;
        this.name = name;
        this.expiry_date = expiry_date;
        this.reminder_days = reminder_days;
    }

    public String getMedID() { return medID; }
    public String getName() { return name; }
    public String getExpiry_date() { return expiry_date; }
    public String getReminder_days() { return reminder_days; }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
