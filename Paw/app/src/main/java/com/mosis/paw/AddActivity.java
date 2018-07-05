package com.mosis.paw;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mosis.paw.Model.Post;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.mosis.paw.BasicFirebaseOperations.FIREBASE_CHILD_USERS;

public class AddActivity extends BasicFirebaseOperations implements OnMapReadyCallback {

    private Spinner typeSpinner;
    private Spinner sizeSpinner;
    private Spinner colorSpinner;
    private List<String> spinnerList;
    private ArrayAdapter<String> spinnerAdapter;

    private EditText descText;

    private Button addButton;

    private String typeOfPost;

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

        initAddButton();

        InitMap();
    }

    private void initAddButton() {
        addButton = findViewById(R.id.add_post_btn);

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
        LatLng sydney = new LatLng(-34, 151); // TODO: real location
        mMarker = mMap.addMarker(new MarkerOptions().position(sydney).title("Your location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

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
