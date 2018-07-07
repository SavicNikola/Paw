package com.mosis.paw;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends BasicFirebaseOperations {

    private static final int MENU_ITEM_SAVE = 1;
    private static final int RC_GALLERY = 1;

    private ImageView imgProfile;
    private Uri imageUri;

    private ImageView imgAvatar;
    private AlertDialog dialog;
    private int avatarChosen = Pawer.getInstance().getAvatar();
    private EditText etEmail;
    private EditText etName;
    private EditText etPassword;
    private EditText etPhone;
    private EditText etCity;

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
        menu.add(0, MENU_ITEM_SAVE, 0, "Save").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
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

        FirebaseSingleton.getInstance().storageReference
                .child("profile_images")
                .child(escapeSpecialCharacters(Pawer.getInstance().getEmail()))
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(SettingsActivity.this).load(uri).into(imgProfile);
                    }
                });
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent()
                        .setType("image/*")
                        .setAction(Intent.ACTION_GET_CONTENT), RC_GALLERY);
            }
        });
        imgAvatar = findViewById(R.id.settings_avatar);
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooserDialog();
            }
        });
        etEmail = findViewById(R.id.settings_email);
        etName = findViewById(R.id.settings_name);
        etPassword = findViewById(R.id.settings_password);
        etPhone = findViewById(R.id.settings_phone);
        etCity = findViewById(R.id.settings_location);
    }

    private void showChooserDialog() {
        GridView gridView = new GridView(this);

        List<Integer> list = new ArrayList<Integer>();
        for (int i = 1; i < 4; i++) {
            list.add(getAvatarDrawableId(i));
        }

        AvatarChooserAdapter adapter = new AvatarChooserAdapter(this, R.layout.avatar_image_layout, list);
        gridView.setAdapter(adapter);
        gridView.setNumColumns(3);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                avatarChosen = position + 1;
                Glide.with(SettingsActivity.this).load(getAvatarDrawableId(avatarChosen)).into(imgAvatar);
                dialog.dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(gridView);
        builder.setTitle("Select avatar");
        dialog = builder.show();
    }

    private void fillViewsWithData() {
        etEmail.setText(Pawer.getInstance().getEmail());
        etName.setText(Pawer.getInstance().getName());
        etPassword.setText(Pawer.getInstance().getPassword());
        etPhone.setText(Pawer.getInstance().getPhone());
        etCity.setText(Pawer.getInstance().getCity());
        Glide.with(this).load(getAvatarDrawableId(Pawer.getInstance().getAvatar())).into(imgAvatar);
    }

    private void saveChanges() {
        FirebaseSingleton.getInstance().databaseReference
                .child("users")
                .child(escapeSpecialCharacters(Pawer.getInstance().getEmail()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.child("avatar").getRef().setValue(avatarChosen);
                        dataSnapshot.child("city").getRef().setValue(etCity.getText().toString());
                        dataSnapshot.child("password").getRef().setValue(etPassword.getText().toString());
                        dataSnapshot.child("phone").getRef().setValue(etPhone.getText().toString());
                        dataSnapshot.child("name").getRef().setValue(etName.getText().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        if (imageUri != null) {
            FirebaseSingleton.getInstance().storageReference
                    .child("profile_images")
                    .child(escapeSpecialCharacters(Pawer.getInstance().getEmail()))
                    .putFile(imageUri);
        }

        updatePawerSingleton();
    }

    private void updatePawerSingleton() {   //todo: ne update-uje odma, mora da se ponovo uloguje
        Pawer.getInstance().setAvatar(avatarChosen);
        Pawer.getInstance().setCity(etCity.getText().toString());
        Pawer.getInstance().setPassword(etPassword.getText().toString());
        Pawer.getInstance().setPhone(etPhone.getText().toString());
        Pawer.getInstance().setName(etName.getText().toString());
    }

    private int getAvatarDrawableId(int avatarNumber) {
        return getResources().getIdentifier("avatar" + avatarNumber, "drawable", getPackageName());
    }
}
