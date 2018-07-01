package com.mosis.paw;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class PostInformationActivity extends AppCompatActivity {

    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    LinearLayout dotsPanel;
    int dotsCounter;
    List<ImageView> dotsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_information);

        InitViewPager();
        InitDots();

        EditToolbar();

        Button btn = findViewById(R.id.post_info_main_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), FoundActivity.class);
                v.getContext().startActivity(intent);
            }
        });
    }

    private void InitViewPager() {
        viewPager = findViewById(R.id.post_info_view_pager);

        List<Integer> picturesList = new ArrayList<>();
        picturesList.add(R.drawable.picture1);
        picturesList.add(R.drawable.picture2);
        picturesList.add(R.drawable.picture3);

        viewPagerAdapter = new ViewPagerAdapter(this, picturesList);

        viewPager.setAdapter(viewPagerAdapter);
    }

    private void InitDots() {
        dotsPanel = findViewById(R.id.post_dots);
        dotsCounter = viewPagerAdapter.getCount();

        dotsList = new ArrayList<>();

        for (int i = 0; i < dotsCounter; ++i) {

            ImageView pom = new ImageView(this);
            pom.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.dot_non_active));

            dotsList.add(pom);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            dotsPanel.addView(pom, params);
        }

        dotsList.get(0).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.dot_active));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (ImageView imageView : dotsList) {
                    imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.dot_non_active));
                }

                dotsList.get(position).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.dot_active));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void EditToolbar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
        getSupportActionBar().setTitle("Post Information");
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
