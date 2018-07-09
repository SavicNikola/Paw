package com.mosis.paw;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.mosis.paw.Model.Post;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AddActivity extends BasicFirebaseOperations implements OnMapReadyCallback {

    private static final int RC_CAMERA = 1;
    private static final int RC_GALLERY = 2;

    private Spinner typeSpinner;
    private Spinner sizeSpinner;
    private Spinner colorSpinner;
    private List<String> spinnerList;
    private ArrayAdapter<String> spinnerAdapter;
    private Uri capturedImageUri;
    private ArrayList<Uri> imageUris = new ArrayList<>();

    private EditText descText;
    private LinearLayout imagesContainer;

    private String typeOfPost;
    private int numOfSelectedImages;
    private File photoFile;

    GoogleMap mMap;
    Marker mMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Bundle extras = getIntent().getExtras();
        typeOfPost = extras == null ? null : extras.getString("TYPE");

        EditToolbar();

        TypeSpinnerInit();
        SizeSpinnerInit();
        ColorSpinnerInit();

        descText = findViewById(R.id.add_post_desc);
        imagesContainer = findViewById(R.id.linear_images);

        initAddButton();
        initBtnTakePicture();
        initBtnSelectPhotos();
        initDiscardButton();

        InitMap();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_CAMERA) {
            if (photoFile.length() != 0) {
                addImageUri(capturedImageUri);
            }
        } else if (requestCode == RC_GALLERY) {
            if (data != null) {
                if (data.getClipData() != null) {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        addImageUri(data.getClipData().getItemAt(i).getUri());
                    }
                } else {
                    addImageUri(data.getData());
                }
            }
        }
        refreshImages();
        Toast.makeText(this, imageUris.size() + " ", Toast.LENGTH_SHORT).show();
    }

    private void addImageUri(Uri imageUri) {
        if (imageUris.size() < 3) {
            imageUris.add(imageUri);
            numOfSelectedImages ++;
        } else if (imageUris.size() == 3) {
            for (int i = 0; i < 3; i++) {
                if (imageUris.get(i) == null) {
                    imageUris.set(i, imageUri);
                    numOfSelectedImages ++;
                    return;
                }
            }
        }
    }

    public void removeImageUri(View view) {
        String[] id = getResources().getResourceName(view.getId()).split("_");
        int index = Integer.parseInt(id[1]) - 1;
        imageUris.remove(index);
        imageUris.add(null);
        numOfSelectedImages--;
        refreshImages();
    }

    private void refreshImages() {
        if (numOfSelectedImages == 0) {
            imagesContainer.setVisibility(View.GONE);
            return;
        }
        imagesContainer.setVisibility(View.VISIBLE);

        ImageView imageView;
        LinearLayout linearLayout = (LinearLayout) imagesContainer.getChildAt(0);
        for (int i = 0; i < imageUris.size(); i++) {
            imageView = (ImageView) linearLayout.getChildAt(i);
            Glide.with(this)
                    .load(imageUris.get(i))
                    .into(imageView);
        }
    }

    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String filename = "IMG_" + timestamp;
        File imagesFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Paw");
        imagesFolder.mkdir();
        return File.createTempFile(filename, ".jpg", imagesFolder);
    }

    private void initBtnTakePicture() {
        findViewById(R.id.btn_take_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        Log.e("AddActivity", "Failed to create image file " + e.getMessage());
                    }
                    if (photoFile != null) {
                        capturedImageUri = FileProvider.getUriForFile(AddActivity.this, getString(R.string.file_provider_authority), photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
                        startActivityForResult(takePictureIntent, RC_CAMERA);
                    }
                }
            }
        });
    }

    private void initBtnSelectPhotos() {
        findViewById(R.id.btn_select_photos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent()
                        .setType("image/*")
                        .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                        .setAction(Intent.ACTION_GET_CONTENT), RC_GALLERY);
            }
        });
    }

    private void initAddButton() {
        Button addButton = findViewById(R.id.add_post_btn);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), Pawer.getInstance().getEmail(), Toast.LENGTH_SHORT).show();
                String desc = descText.getText().toString();
                String color = colorSpinner.getSelectedItem().toString();
                String size = sizeSpinner.getSelectedItem().toString();
                String type = typeSpinner.getSelectedItem().toString();

                Post post = new Post(
                        UUID.randomUUID().toString(), //id
                        Pawer.getInstance().getEmail(), //user id
                        String.valueOf(System.currentTimeMillis()), //time
                        "Insert images", //images
                        "City", //city
                        typeOfPost, // type
                        desc.isEmpty() ? null : desc, // desc
                        color.equals("Color") ? null : color, //color
                        size.equals("Size") ? null : size, //size
                        type.equals("Type") ? null : type, //type of animal
                        String.valueOf(mMarker.getPosition().latitude),
                        String.valueOf(mMarker.getPosition().longitude));

                addPostToDatabase(post);
                addImagesToStorage(post);
            }
        });
    }

    private void initDiscardButton() {
        findViewById(R.id.image_discard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUris.clear();
                imagesContainer.setVisibility(View.GONE);
            }
        });
    }

    private void addPostToDatabase(Post post) {
        getDatabaseReference().child(FIREBASE_CHILD_POSTS)
                .child(post.getId())
                .setValue(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddActivity.this, "Posted successfully!", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddActivity.this, "Posted failed!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void addImagesToStorage(Post post) {
        StorageReference ref = FirebaseSingleton.getInstance().storageReference.child(post.getId());
        for (int i = 0; i < imageUris.size(); i++) {
            if (imageUris.get(i) != null)
                ref.child("img"+i).putFile(imageUris.get(i));
        }
    }

    private void InitMap() {
        ScrollableMapFragment mapFragment = (ScrollableMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
        ((ScrollableMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment)).setListener(new ScrollableMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng myLocation = new LatLng(Double.valueOf(Pawer.getInstance().getLatitude()), Double.valueOf(Pawer.getInstance().getLongitude())); // TODO: real location
        mMarker = mMap.addMarker(new MarkerOptions().position(myLocation).title("Your location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMarker.setPosition(latLng);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
            }
        });
    }

    private void TypeSpinnerInit() {
        typeSpinner = findViewById(R.id.type_spinner);

        spinnerList = new ArrayList<String>();
        spinnerList.add("Type");
        spinnerList.add("Dog");
        spinnerList.add("Cat");
        spinnerList.add("Tiger");

        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerList);

        typeSpinner.setAdapter(spinnerAdapter);
    }

    private void SizeSpinnerInit() {
        sizeSpinner = findViewById(R.id.size_spinner);

        spinnerList = new ArrayList<String>();
        spinnerList.add("Size");
        spinnerList.add("Small");
        spinnerList.add("Normal");
        spinnerList.add("Big");

        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerList);

        sizeSpinner.setAdapter(spinnerAdapter);
    }

    private void ColorSpinnerInit() {
        colorSpinner = findViewById(R.id.color_spinner);

        spinnerList = new ArrayList<String>();
        spinnerList.add("Color");
        spinnerList.add("White");
        spinnerList.add("Blue");
        spinnerList.add("Black");
        spinnerList.add("Yellow");
        spinnerList.add("Brown");

        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerList);

        colorSpinner.setAdapter(spinnerAdapter);
    }

    public void showTypeSpinner(View v) {
        typeSpinner.performClick();
    }

    public void showSizeSpinner(View v) {
        sizeSpinner.performClick();
    }

    public void showColorSpinner(View v) {
        colorSpinner.performClick();
    }

    private void EditToolbar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
        getSupportActionBar().setTitle("Add to " + typeOfPost + " feed");
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
