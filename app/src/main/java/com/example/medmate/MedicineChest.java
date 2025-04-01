package com.example.medmate;

public class MedicineChest {
    private String chestId;
    private String name;
    private String size;

    public MedicineChest() {}

    public MedicineChest(String chestId, String name, String size) {
        this.chestId = chestId;
        this.name = name;
        this.size = size;
    }

    public String getChestId() { return chestId; }
    public String getName() { return name; }
    public String getSize() { return size; }
}