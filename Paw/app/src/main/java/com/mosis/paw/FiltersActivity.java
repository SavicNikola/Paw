package com.mosis.paw;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.mosis.paw.Model.Filter;

import java.util.ArrayList;
import java.util.List;

public class FiltersActivity extends AppCompatActivity {

    private Spinner locationSpinner;
    private Spinner typeSpinner;
    private Spinner sizeSpinner;
    private Spinner colorSpinner;

    private List<String> locationList, typeList, sizeList, colorList;
    private ArrayAdapter<String> spinnerAdapter;

    private Button setFilters, resetFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        LocationSpinnerInit();
        TypeSpinnerInit();
        ColorSpinnerInit();
        SizeSpinnerInit();

        initButtons();

        EditToolbar();
    }

    private void LocationSpinnerInit() {
        locationSpinner = findViewById(R.id.location_spinner);

        locationList = new ArrayList<String>();
        locationList.add("Nis, Serbia");

        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, locationList);

        locationSpinner.setAdapter(spinnerAdapter);
    }

    private void TypeSpinnerInit() {
        typeSpinner = findViewById(R.id.type_spinner);

        typeList = new ArrayList<String>();
        typeList.add("All");
        typeList.add("Dog");
        typeList.add("Cat");
        typeList.add("Tiger");

        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, typeList);

        typeSpinner.setAdapter(spinnerAdapter);

        typeSpinner.setSelection(typeList.indexOf(Pawer.getInstance().getFilter().getType()));
    }

    private void ColorSpinnerInit() {
        colorSpinner = findViewById(R.id.color_spinner);

        colorList = new ArrayList<String>();
        colorList.add("All");
        colorList.add("White");
        colorList.add("Blue");
        colorList.add("Black");
        colorList.add("Yellow");
        colorList.add("Brown");

        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, colorList);

        colorSpinner.setAdapter(spinnerAdapter);

        colorSpinner.setSelection(colorList.indexOf(Pawer.getInstance().getFilter().getColor()));
    }

    private void SizeSpinnerInit() {
        sizeSpinner = findViewById(R.id.size_spinner);

        sizeList = new ArrayList<String>();
        sizeList.add("All");
        sizeList.add("Small");
        sizeList.add("Normal");
        sizeList.add("Big");

        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sizeList);

        sizeSpinner.setAdapter(spinnerAdapter);

        sizeSpinner.setSelection(sizeList.indexOf(Pawer.getInstance().getFilter().getSize()));
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

    private void initButtons() {

        setFilters = findViewById(R.id.btnSetFilters);
        resetFilters = findViewById(R.id.btnResetFilters);

        setFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pawer.getInstance().setFilter(new Filter(
                        typeList.get(typeSpinner.getSelectedItemPosition()),
                        colorList.get(colorSpinner.getSelectedItemPosition()),
                        sizeList.get(sizeSpinner.getSelectedItemPosition())
                ));

                Toast.makeText(v.getContext(), "Filters are set!", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });

        resetFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pawer.getInstance().setFilter(new Filter("All", "All", "All"));

                Toast.makeText(v.getContext(), "Filters are reset!", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
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
