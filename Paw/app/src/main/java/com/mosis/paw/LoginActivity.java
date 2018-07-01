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

//        User user = new User(null, "Pera Peric", "pera@gmail.com", "pera123",
//                "0641234153", "Nis, Serbia",
//                "http://cdn1.thr.com/sites/default/files/2017/08/gettyimages-630421358_-_h_2017.jpg");
//        startActivity(new Intent(this, ProfileActivity2.class)
//                .putExtra("user", user)
//                .putExtra(ProfileActivity2.PROFILE_TYPE, ProfileActivity2.PROFILE_NOT_FRIEND)); //TODO: testiranje, obrisati
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
        final EditText editEmail = findViewById(R.id.edit_email);
        final EditText editPassword = findViewById(R.id.edit_password);
        String email = editEmail.getText().toString();
        final String password = editPassword.getText().toString();

        getDatabaseReference().child(FIREBASE_CHILD_USERS).child(escapeSpecialCharacters(email))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user == null) {
                            editEmail.setError(getString(R.string.error_wrong_email));
                        } else if (!user.getPassword().equals(password)) {
                            editPassword.setError(getString(R.string.error_password_incorect));
                        } else {
                            //ulogovan
                            User user2 = new User(null, "Pera Peric", "pera@gmail.com", "pera123",
                                    "0641234153", "Nis, Serbia",
                                    "http://cdn1.thr.com/sites/default/files/2017/08/gettyimages-630421358_-_h_2017.jpg");
                            startActivity(new Intent(LoginActivity.this, ProfileActivity2.class)
                                    .putExtra("user", user2)
                                    .putExtra(ProfileActivity2.PROFILE_TYPE, ProfileActivity2.PROFILE_NOT_FRIEND)); //TODO: testiranje, obrisati

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }
}
