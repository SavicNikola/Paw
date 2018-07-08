package com.mosis.paw;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends BasicFirebaseOperations {

    private EditText editEmail;
    private  EditText editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditToolbar();
        initializeViews();
        checkIfLogOutIntent();
    }

    private void checkIfLogOutIntent() {
        if (getIntent().getAction() != null && getIntent().getAction().equals("logout")) {
            editEmail.setText("");
            editPassword.setText("");
//            Pawer.getInstance().destroy(); //todo: treba li da se unisti singleton instanca???
        }
    }

    private void EditToolbar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
        getSupportActionBar().setTitle("Log in");
    }

    private void initializeViews() {
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);

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
        String email = editEmail.getText().toString();
        final String password = editPassword.getText().toString();

        getDatabaseReference().child(FIREBASE_CHILD_USERS).child(escapeSpecialCharacters(email))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Pawer user = dataSnapshot.getValue(Pawer.class);

                        if (user == null) {
                            editEmail.setError(getString(R.string.error_wrong_email));
                        } else if (user.getPassword()!=null && !user.getPassword().equals(password)) {
                            editPassword.setError(getString(R.string.error_password_incorect));
                        } else {

                            Pawer.getInstance().setEmail(user.getEmail());

                            if (user.getFavourites() == null)
                                Pawer.getInstance().setFavourites(new ArrayList<String>());
                            else
                                Pawer.getInstance().setFavourites(user.getFavourites());

                            Pawer.getInstance().setName(user.getName());
                            Pawer.getInstance().setCity(user.getCity());
                            Pawer.getInstance().setPoints(user.getPoints());
                            Pawer.getInstance().setPhone(user.getPhone());
                            Pawer.getInstance().setAvatar(user.getAvatar());    //todo: dodati i ostale po potrebi

                            startActivity(new Intent(LoginActivity.this, MainSideNavActivity.class));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }
}
