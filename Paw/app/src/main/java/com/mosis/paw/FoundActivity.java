package com.mosis.paw;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.mosis.paw.Model.PawNotification;
import com.mosis.paw.Model.Post;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class FoundActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    Marker mMarker;

    Switch numberSwitch;
    TextView desc;
    Button takePicture, send;

    Uri capturedImageUri;

    String typeOfPost, postUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found);

        Bundle extras = getIntent().getExtras();
        typeOfPost = extras == null ? null : extras.getString("TYPE");
        postUserId = extras == null ? null : extras.getString("PostUserId");

        InitMap();

        EditToolbar();

        initViews();
        //initBtnTakePicture();
        initSendButton();
    }

    private void initViews() {
        numberSwitch = findViewById(R.id.found_send_number_switch);
        desc = findViewById(R.id.found_desc);
        takePicture = findViewById(R.id.found_btn_take_photo);
        send = findViewById(R.id.found_btn_send);
    }

    //region PICTURE SENTIC DA POGLEDA

    private void initBtnTakePicture() {
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        Log.e("AddActivity", "Failed to create image file " + e.getMessage());
                    }
                    if (photoFile != null) {
                        capturedImageUri = FileProvider.getUriForFile(FoundActivity.this, getString(R.string.file_provider_authority), photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
                        startActivityForResult(takePictureIntent, 1);
                    }
                }
            }
        });
    }

    private void addImagesToStorage(Post post) {
        StorageReference ref = FirebaseSingleton.getInstance().storageReference.child(post.getId());
        ref.child("img").putFile(capturedImageUri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1)
            takePicture.setText("Take again");
    }

    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String filename = "IMG_" + timestamp;
        File imagesFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Paw");
        imagesFolder.mkdir();
        return File.createTempFile(filename, ".jpg", imagesFolder);
    }

    //endregion

    private void initSendButton() {

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PawNotification notification = new PawNotification();

                //desc
                notification.setDescription(desc.getText().toString());

                //id
                notification.setId(UUID.randomUUID().toString());

                //position
                notification.setLatitude(String.valueOf(mMarker.getPosition().latitude));
                notification.setLongitude(String.valueOf(mMarker.getPosition().longitude));

                //phone
                if (numberSwitch.isChecked())
                    notification.setNumber(Pawer.getInstance().getPhone());

                //picture
                // TODO: SET PICTURE

                //read
                notification.setRead(false);

                //time
                notification.setTime(String.valueOf(System.currentTimeMillis()));

                //type
                notification.setType(typeOfPost);

                //user
                notification.setUser(Pawer.getInstance().getEmail());

                //ADD NOTIFICATION IN NOTIFICATION DATA
                FirebaseSingleton.getInstance().databaseReference
                        .child("notification_data")
                        .child(notification.getId())
                        .setValue(notification);

                //ADD IN NOTIFICATIONS
                FirebaseSingleton.getInstance().databaseReference
                        .child("notifications")
                        .child(postUserId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                ArrayList<String> list = (ArrayList) dataSnapshot.getValue();

                                if (list == null)
                                    list = new ArrayList<>();

                                list.add(notification.getId());

                                FirebaseSingleton.getInstance().databaseReference
                                        .child("notifications")
                                        .child(postUserId)
                                        .setValue(list);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        });
    }

    private void InitMap() {
        ScrollableMapFragment mapFragment = (ScrollableMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.found_mapFragment);
        mapFragment.getMapAsync(this);

        final ScrollView scrollView = (ScrollView) findViewById(R.id.found_scroll_view);
        ((ScrollableMapFragment) getSupportFragmentManager().findFragmentById(R.id.found_mapFragment)).setListener(new ScrollableMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // TODO: REAL LOCATION
        LatLng sydney = new LatLng(-34, 151);
        mMarker = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void EditToolbar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
        getSupportActionBar().setTitle("Found");
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
}
