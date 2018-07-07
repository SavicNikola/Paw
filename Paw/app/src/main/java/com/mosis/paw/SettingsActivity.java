package com.mosis.paw;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class SettingsActivity extends AppCompatActivity {

    private static final int MENU_ITEM_SAVE = 1;
    private static final int RC_GALLERY = 1;

    private ImageView imgProfile;
    private Uri imageUri;
    private EditText etEmail;
    private EditText etName;
    private EditText etPassword;
    private EditText etPhone;
    private EditText etCity;
    private ImageView imgAvatar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditToolbar();
        initViews();
        fillViewsWithData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_ITEM_SAVE, 0, "Save");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_ITEM_SAVE) {
            saveChanges();
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_GALLERY) {
            if (data != null) {
                imageUri = data.getData();
                Glide.with(this).load(imageUri).into(imgProfile);
            }
        }
    }

    private void EditToolbar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void initViews() {
        imgProfile = findViewById(R.id.settings_image);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent()
                        .setType("image/*")
                        .setAction(Intent.ACTION_GET_CONTENT), RC_GALLERY);
            }
        });
        etEmail = findViewById(R.id.settings_email);
        etName = findViewById(R.id.settings_name);
        etPassword = findViewById(R.id.settings_password);
        etPhone = findViewById(R.id.settings_phone);
        etCity = findViewById(R.id.settings_location);
        imgAvatar = findViewById(R.id.settings_avatar);
    }

    private void fillViewsWithData() {
        etEmail.setText(Pawer.getInstance().getEmail());
        etName.setText(Pawer.getInstance().getName());
        etPassword.setText(Pawer.getInstance().getPassword());
        etPhone.setText(Pawer.getInstance().getPhone());
        etCity.setText(Pawer.getInstance().getCity());
        final int avatarId = getResources().getIdentifier("avatar" + Pawer.getInstance().getAvatar(), "drawable", getPackageName());
        Glide.with(this).load(avatarId).into(imgAvatar);
    }

    private void saveChanges() {
        Toast.makeText(this, "Saving", Toast.LENGTH_SHORT).show();
    }
}
