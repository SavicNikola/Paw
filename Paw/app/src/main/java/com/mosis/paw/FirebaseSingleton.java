package com.mosis.paw;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseSingleton {

    private static final FirebaseSingleton instance = new FirebaseSingleton();

    public DatabaseReference databaseReference;

    private FirebaseSingleton() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public static FirebaseSingleton getInstance() { return instance; }
}
