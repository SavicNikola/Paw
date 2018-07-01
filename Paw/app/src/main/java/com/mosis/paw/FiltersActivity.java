package com.mosis.paw;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class FiltersActivity extends AppCompatActivity {

    private Spinner locationSpinner;
    private Spinner typeSpinner;
    private Spinner sizeSpinner;
    private Spinner colorSpinner;

    private List<String> spinnerList;
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        LocationSpinnerInit();
        TypeSpinnerInit();
        ColorSpinnerInit();
        SizeSpinnerInit();

        EditToolbar();
    }

    private void LocationSpinnerInit() {
        locationSpinner = findViewById(R.id.location_spinner);

        spinnerList = new ArrayList<String>();
        spinnerList.add("Nis, Serbia");

        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerList);

        locationSpinner.setAdapter(spinnerAdapter);
    }

    private void TypeSpinnerInit() {
        typeSpinner = findViewById(R.id.type_spinner);

        spinnerList = new ArrayList<String>();
        spinnerList.add("All");
        spinnerList.add("Dog");
        spinnerList.add("Cat");
        spinnerList.add("Tiger");

        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerList);

        typeSpinner.setAdapter(spinnerAdapter);
    }

    private void ColorSpinnerInit() {
        colorSpinner = findViewById(R.id.color_spinner);

        spinnerList = new ArrayList<String>();
        spinnerList.add("All");
        spinnerList.add("White");
        spinnerList.add("Blue");
        spinnerList.add("Black");
        spinnerList.add("Yellow");
        spinnerList.add("Brown");

        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerList);

        colorSpinner.setAdapter(spinnerAdapter);
    }

    private void SizeSpinnerInit() {
        sizeSpinner = findViewById(R.id.size_spinner);

        spinnerList = new ArrayList<String>();
        spinnerList.add("All");
        spinnerList.add("Small");
        spinnerList.add("Normal");
        spinnerList.add("Big");

        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerList);

        sizeSpinner.setAdapter(spinnerAdapter);
    }

    public void ShowLocationSpinner(View v) {
        locationSpinner.performClick();
    }

    public void ShowTypeSpinner(View v) {
        typeSpinner.performClick();
    }

    public void ShowColorSpinner(View v) {
        colorSpinner.performClick();
    }

    public void ShowSizeSpinner(View v) {
        sizeSpinner.performClick();
    }

    private void EditToolbar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
        getSupportActionBar().setTitle("Filters");
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
