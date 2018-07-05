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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProfileHelpsAdapter adapter;
    private List<FeedItem> feedList;

    private String userId;

    TextView name;
    Button profileBtn;
    TextView points;
    TextView helps;
    TextView friends;
    ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle extras = getIntent().getExtras();
        userId = extras == null ? null : extras.getString("email");

        EditToolbar();

        getViews();

        InitRecycleView();

        // da se ne bi fokusirao recycleview, posto se iz nekog razloga fokusira
        findViewById(R.id.profile_helps_recycler_view).setFocusable(false);
        findViewById(R.id.profile_start_layout).requestFocus();

        // profile
        profileImage.setImageResource(R.drawable.profile_full);
    }

    private void getViews() {
        name = findViewById(R.id.profile_name);
        profileBtn = findViewById(R.id.profile_btn);
        points = findViewById(R.id.profile_points);
        helps = findViewById(R.id.profile_helps);
        friends = findViewById(R.id.profile_friends);
        profileImage = findViewById(R.id.image_profile);
    }

    private void InitRecycleView() {
        recyclerView = (RecyclerView) findViewById(R.id.profile_helps_recycler_view);

//        //proba glupi podaci
//        feedList = new ArrayList<>();
//        FeedItem item = new FeedItem("Marko Markovic", R.drawable.avatar1, "2h ago", "Korisnikov opis posta, na primer kratke informacije o izgubljenom ljubimcu.. Klikom na post dobice vise informacija..", R.drawable.picture1, FeedTypeEnum.FOUND, true);
//        FeedItem item2 = new FeedItem("Nikola Niki", R.drawable.avatar2, "1h ago", "Korisnikov opis posta, na primer kratke informacije o izgubljenom ljubimcu..", R.drawable.picture2, FeedTypeEnum.LOST, true);
//        FeedItem item3 = new FeedItem("Stefan Steki", R.drawable.avatar3, "2h ago", "Klikom na post dobice vise informacija..", R.drawable.picture3, FeedTypeEnum.ADOPT, true);
//        feedList.add(item);
//        feedList.add(item2);
//        feedList.add(item3);

        adapter = new ProfileHelpsAdapter(this, feedList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

        InitFromDatabase();
    }

    void InitFromDatabase() {
        FirebaseSingleton.getInstance().databaseReference
                .child("users")
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Pawer pawer = dataSnapshot.getValue(Pawer.class);

                        // TODO: profile image and profile btn
                        name.setText(pawer.getName());
                        points.setText(pawer.getPoints());
                        helps.setText(pawer.getHelps());
                        friends.setText(pawer.getFriends());

                        // TODO: u bazi da se doda favourites za svakog i ovde da se ubaci
                        // TODO: da se prodje kroz tu listu i povuku postovi
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
