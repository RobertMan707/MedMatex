package com.example.medmate;

public class Medicine {
    private String name;
    private int count;
    private String time;
    private String type;
    private boolean taken;


    public Medicine(String name, int count, String time, String type, boolean taken) {
        this.name = name;
        this.count = count;
        this.time = time;
        this.type = type;
        this.taken = taken;
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
}
