package com.mosis.paw;

import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProfileHelpsAdapter adapter;
    private List<FeedItem> feedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        EditToolbar();

        InitRecycleView();

        // da se ne bi fokusirao recycleview, posto se iz nekog razloga fokusira
        findViewById(R.id.profile_helps_recycler_view).setFocusable(false);
        findViewById(R.id.profile_start_layout).requestFocus();

        // profile
        ((ImageView) findViewById(R.id.image_profile)).setImageResource(R.drawable.profile_full);
    }

    private void InitRecycleView() {
        recyclerView = (RecyclerView) findViewById(R.id.profile_helps_recycler_view);

        //proba glupi podaci
        feedList = new ArrayList<>();
        FeedItem item = new FeedItem("Marko Markovic", R.drawable.avatar1, "2h ago", "Korisnikov opis posta, na primer kratke informacije o izgubljenom ljubimcu.. Klikom na post dobice vise informacija..", R.drawable.picture1, FeedTypeEnum.FOUND, true);
        FeedItem item2 = new FeedItem("Nikola Niki", R.drawable.avatar2, "1h ago", "Korisnikov opis posta, na primer kratke informacije o izgubljenom ljubimcu..", R.drawable.picture2, FeedTypeEnum.LOST, true);
        FeedItem item3 = new FeedItem("Stefan Steki", R.drawable.avatar3, "2h ago", "Klikom na post dobice vise informacija..", R.drawable.picture3, FeedTypeEnum.ADOPT, true);
        feedList.add(item);
        feedList.add(item2);
        feedList.add(item3);

        adapter = new ProfileHelpsAdapter(this, feedList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }

    private void EditToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
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
