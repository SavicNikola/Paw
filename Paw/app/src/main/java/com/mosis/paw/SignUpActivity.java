package com.mosis.paw;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mosis.paw.Model.User;

public class SignUpActivity extends BasicFirebaseOperations {

    private EditText editName;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editPhone;
    private EditText editCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        EditToolbar();
        initializeViews();
    }

    private void EditToolbar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeViews() {
        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        editPhone = findViewById(R.id.edit_phone);
        editCity = findViewById(R.id.edit_city);


        Button btnSignUp = findViewById(R.id.btn_signUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs())
                    persistUser();
            }
        });
    }

    private void persistUser() {
        Pawer pawer = new Pawer(readUser());
        getDatabaseReference().child(FIREBASE_CHILD_USERS)
                .child(escapeSpecialCharacters(pawer.getEmail()))
                .setValue(pawer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SignUpActivity.this, "Sign up successfull!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, "Sign up failed!", Toast.LENGTH_LONG).show();
                    }
                });


    }

    private User readUser() {
        String name = editName.getText().toString();
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        String phone = editPhone.getText().toString();
        String city = editCity.getText().toString();

        return new User(null, name, email, password, phone, city);
    }

    private boolean validateInputs() {
        return isValid(editName) && isValid(editEmail) && isValid(editPassword)
                && isValid(editPhone) && isValid(editCity);
    }

    private boolean isValid(EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            editText.setError(getString(R.string.error_empty_input_field));
            return false;
        }
        return true;
    }
}
