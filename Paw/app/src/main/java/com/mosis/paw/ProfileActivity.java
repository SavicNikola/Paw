package com.mosis.paw;

import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mosis.paw.Model.Post;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProfileHelpsAdapter adapter;
    private List<FeedItem> feedList;

    private String userId;

    TextView name;
    TextView place;
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
        userId = extras == null ? null : extras.getString("UserID");

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
        place = findViewById(R.id.profile_place);
        profileBtn = findViewById(R.id.profile_btn);
        points = findViewById(R.id.profile_points);
        helps = findViewById(R.id.profile_helps);
        friends = findViewById(R.id.profile_friends);
        profileImage = findViewById(R.id.image_profile);
    }

    private void InitRecycleView() {
        recyclerView = (RecyclerView) findViewById(R.id.profile_helps_recycler_view);

        feedList = new ArrayList<>();
        adapter = new ProfileHelpsAdapter(this, feedList);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

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
                        place.setText(pawer.getCity());
                        points.setText(pawer.getPoints() == null || pawer.getPoints().isEmpty() ? "0" : pawer.getPoints());
                        helps.setText(pawer.getHelps() == null || pawer.getHelps().isEmpty() ? "0" : pawer.getHelps());
                        friends.setText(pawer.getFriends() == null || pawer.getFriends().isEmpty() ? "0" : pawer.getFriends());


                        // TODO: u bazi da se doda helps za svakog i ovde da se ubaci
                        // TODO: da se prodje kroz tu listu i povuku postovi
                        FirebaseSingleton.getInstance().databaseReference
                                .child("helps")
                                .child(userId)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        ArrayList<String> helpsList = (ArrayList<String>) dataSnapshot.getValue();

                                        feedList.clear();

                                        if (helpsList != null) {
                                            for (String help : helpsList) {

                                                FirebaseSingleton.getInstance().databaseReference
                                                        .child("ex_posts")
                                                        .child(help)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                                final Post post = dataSnapshot.getValue(Post.class);

                                                                FirebaseSingleton.getInstance().databaseReference
                                                                        .child("users")
                                                                        .child(post.getUserId())
                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                Pawer user = dataSnapshot.getValue(Pawer.class);
                                                                                Post inerPost = post;

                                                                                int avatar = SwitchAvatar(user.getAvatar());

                                                                                CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(Long.valueOf(inerPost.getTime()));

                                                                                FeedTypeEnum feedType = SwitchType(inerPost.getType());

                                                                                // TODO: da se refaktorise, post slika da se ubaci
                                                                                feedList.add(new FeedItem(post.getId(), user.getName(), avatar, timeAgo.toString(), inerPost.getDescription(), R.drawable.picture3, feedType, true));
                                                                                adapter.notifyDataSetChanged();
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private FeedTypeEnum SwitchType(String type) {

        FeedTypeEnum feedType = FeedTypeEnum.LOST; //default
        switch (type) {
            case "lost":
                feedType = FeedTypeEnum.LOST;
                break;
            case "found":
                feedType = FeedTypeEnum.FOUND;
                break;
            case "adopt":
                feedType = FeedTypeEnum.ADOPT;
                break;
        }

        return feedType;
    }

    private int SwitchAvatar(Integer input) {

        int avatar = R.drawable.avatar1; //default
        switch (input) {
            case 1:
                avatar = R.drawable.avatar1;
                break;
            case 2:
                avatar = R.drawable.avatar2;
                break;
            case 3:
                avatar = R.drawable.avatar3;
                break;
        }

        return avatar;
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
