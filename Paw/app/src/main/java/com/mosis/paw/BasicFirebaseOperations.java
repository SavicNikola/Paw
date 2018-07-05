package com.mosis.paw;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BasicFirebaseOperations extends AppCompatActivity {
    public static final String FIREBASE_CHILD_USERS = "users";
    public static final String FIREBASE_CHILD_POSTS = "posts";

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public String escapeSpecialCharacters(String email) {
        return email.replaceAll("%", "%25")
                .replaceAll("\\.", "%2E")
                .replaceAll("#", "%23")
                .replaceAll("\\$", "%24")
                .replaceAll("/", "%2F")
                .replaceAll("\\[", "%5B")
                .replaceAll("]", "%5D");
    }

}
