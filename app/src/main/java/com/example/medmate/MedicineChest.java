package com.example.medmate;

public class MedicineChest {
    private String chestId;
    private String name;
    private String size;

    // Empty constructor required for Firebase
    public MedicineChest() {}

    public MedicineChest(String chestId, String name, String size) {
        this.chestId = chestId;
        this.name = name;
        this.size = size;
    }

    // Getters and setters
    public String getChestId() { return chestId; }
    public String getName() { return name; }
    public String getSize() { return size; }
}