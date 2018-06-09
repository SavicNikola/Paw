package com.mosis.paw;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mosis.paw.Model.User;

public class SignUpActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private static final String FIREBASE_CHILD_USERS = "users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button btnSignUp = findViewById(R.id.btn_signUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persistUser();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void persistUser() {
        User user = readUser();
        String userKey = databaseReference.push().getKey();
        databaseReference.child(FIREBASE_CHILD_USERS).child(userKey).setValue(user);
    }

    private User readUser() {
        String name = ((EditText) findViewById(R.id.edit_name)).getText().toString();
        String email = ((EditText) findViewById(R.id.edit_email)).getText().toString();
        String password = ((EditText) findViewById(R.id.edit_password)).getText().toString();
        String phone = ((EditText) findViewById(R.id.edit_phone)).getText().toString();
        String city = ((EditText) findViewById(R.id.edit_city)).getText().toString();

        return new User("", name, email, password, phone, city);
    }
}
