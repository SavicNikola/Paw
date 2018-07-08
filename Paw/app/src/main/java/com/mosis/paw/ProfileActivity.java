package com.mosis.paw;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
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
        initProfileBtn();

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

        FirebaseSingleton.getInstance().storageReference
                .child("profile_images")
                .child(Pawer.getInstance().getEscapedEmail())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(ProfileActivity.this).load(uri).into(profileImage);
                    }
                });
    }

    private void initProfileBtn() {
        if (userId.equals(Pawer.getInstance().getEmail())) {
            profileBtn.setText("Edit");
            profileBtn.setVisibility(View.VISIBLE);

            profileBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            FirebaseSingleton.getInstance().databaseReference
                    .child("friends")
                    .child(Pawer.getInstance().getEmail())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            final ArrayList<String> list = (ArrayList) dataSnapshot.getValue();

                            if (list != null && list.contains(userId)) {
                                profileBtn.setText("Remove");
                                profileBtn.setVisibility(View.VISIBLE);

                                profileBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);

                                        builder.setTitle("Delete friend?")
                                                .setMessage("Do you want to delete your friendship with " + userId + "?")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        list.remove(userId);

                                                        FirebaseSingleton.getInstance().databaseReference
                                                                .child("friends")
                                                                .child(Pawer.getInstance().getEmail())
                                                                .setValue(list);

                                                        FirebaseSingleton.getInstance().databaseReference
                                                                .child("friends")
                                                                .child(userId)
                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        ArrayList<String> list2 = (ArrayList) dataSnapshot.getValue();

                                                                        if (list2 != null && list2.contains(Pawer.getInstance().getEmail())) {
                                                                            list2.remove(Pawer.getInstance().getEmail());

                                                                            FirebaseSingleton.getInstance().databaseReference
                                                                                    .child("friends")
                                                                                    .child(userId)
                                                                                    .setValue(list2);
                                                                        }

                                                                        Toast.makeText(ProfileActivity.this, ":(", Toast.LENGTH_SHORT).show();
                                                                        onBackPressed();
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                    }
                                                                });
                                                    }
                                                })
                                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Toast.makeText(ProfileActivity.this, "Good :)", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .setIcon(R.drawable.ic_face_sad)
                                                .show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
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
