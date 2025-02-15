package com.example.medmate;

import java.util.UUID;

public class Medicine {
    private String id;
    private String name;
    private String frequency;
    private int stock;
    private String time;
    private String days;
    private String type;
    private boolean isTaken;

    public Medicine() {
        this.id = UUID.randomUUID().toString(); // Generate unique ID
    }

    public Medicine(String name, String frequency, int stock, String time, String days, String type, boolean isTaken) {
        this.id = UUID.randomUUID().toString(); // Generate unique ID
        this.name = name;
        this.frequency = frequency;
        this.stock = stock;
        this.time = time;
        this.days = days;
        this.type = type;
        this.isTaken = isTaken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }
}
