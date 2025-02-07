package com.example.medmate;

public class Medicine {
    private String name;
    private String type;
    private String time;
    private int stock;

    public Medicine() {
        // Empty constructor required for Firebase
    }

    public Medicine(String name, String type, String time, int stock) {
        this.name = name;
        this.type = type;
        this.time = time;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getTime() {
        return time;
    }

    public int getStock() {
        return stock;
    }
}
