package com.example.medmate;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {

    private DatabaseReference mDatabase;

    public FirebaseHelper() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void addMedicine(Medicine medicine) {
        String key = mDatabase.child("medicines").push().getKey();
        mDatabase.child("medicines").child(key).setValue(medicine);
    }

    public void updateMedicine(String key, Medicine medicine) {
        mDatabase.child("medicines").child(key).setValue(medicine);
    }

    public void deleteMedicine(String key) {
        mDatabase.child("medicines").child(key).removeValue();
    }

    public DatabaseReference getMedicinesDatabase() {
        return mDatabase.child("medicines");
    }
}

