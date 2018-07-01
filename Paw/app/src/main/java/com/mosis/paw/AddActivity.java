package com.mosis.paw;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class AddActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Spinner typeSpinner;
    private Spinner sizeSpinner;
    private Spinner colorSpinner;
    private List<String> spinnerList;
    private ArrayAdapter<String> spinnerAdapter;

    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        EditToolbar();

        TypeSpinnerInit();
        SizeSpinnerInit();
        ColorSpinnerInit();

        InitMap();
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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
        getSupportActionBar().setTitle("Add");
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
