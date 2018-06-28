package com.mosis.paw;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mosis.paw.Model.User;

public class LoginActivity extends BasicFirebaseOperations {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
    }

    private void initializeViews() {
        Button btnSignUp = findViewById(R.id.btn_signUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        Button btnLogin = findViewById(R.id.btn_logIn);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });


        TextView textForgotPass = findViewById(R.id.text_forgotPassword);
        textForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }

    private void logIn() {
        String email = ((EditText) findViewById(R.id.edit_email)).getText().toString();
        String password = ((EditText) findViewById(R.id.edit_password)).getText().toString();

        getDatabaseReference().child(FIREBASE_CHILD_USERS).child(escapeSpecialCharacters(email))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }
}
