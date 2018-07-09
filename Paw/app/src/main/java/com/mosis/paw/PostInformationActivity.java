package com.mosis.paw;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mosis.paw.Model.Post;

import java.util.ArrayList;
import java.util.List;

public class PostInformationActivity extends AppCompatActivity {

    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    LinearLayout dotsPanel;
    int dotsCounter;
    List<ImageView> dotsList;
    Post post;

    String postId;

    Button btnMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_information);

        postId = getIntent().getStringExtra("postId");

        getPost();

        EditToolbar();

        btnMain = findViewById(R.id.post_info_main_btn);

        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), FoundActivity.class);
                intent.putExtra("TYPE", post.getType());
                intent.putExtra("PostUserId", post.getUserId());
                v.getContext().startActivity(intent);
            }
        });

//        Button postOnMap = findViewById(R.id.post_info_map_btn);
//        postOnMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), MapActivity.class);
//                //type of map (friends, post, feed)
//                intent.putExtra("TYPE", "post");
//                intent.putExtra("POSTID", postId);
//                v.getContext().startActivity(intent);
//            }
//        });
    }

    private void getPost() {

        FirebaseSingleton.getInstance().databaseReference
                .child("posts")
                .child(postId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        post = dataSnapshot.getValue(Post.class);

                        initData();
                        InitViewPager();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void initData() {
        ((TextView) findViewById(R.id.post_user_name)).setText(getIntent().getStringExtra("postCreator"));
        ((TextView) findViewById(R.id.post_layout_desc)).setText(post.getDescription());
        btnMain.setText(post.getType().toUpperCase());
    }

    private void InitViewPager() {
        viewPager = findViewById(R.id.post_info_view_pager);
        final List<Uri> picturesList = new ArrayList<>();

        viewPagerAdapter = new ViewPagerAdapter(this, picturesList);

        viewPager.setAdapter(viewPagerAdapter);

        for (int i = 0; i < 3; i++) {
            FirebaseSingleton.getInstance().storageReference
                    .child(post.getId() + "/img"+i)
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            picturesList.add(uri);
                            viewPagerAdapter.notifyDataSetChanged();
                            InitDots();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
            });
        }

    }

    private void InitDots() {
        if (viewPagerAdapter.getCount() == 0) return;   //todo: hide image view or show no image

        dotsPanel = findViewById(R.id.post_dots);
        dotsPanel.removeAllViews();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.post_map_menu, menu);
        return true;
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
            case R.id.post_map:
                Intent intent = new Intent(this, MapActivity.class);
                //type of map (friends, post, feed)
                intent.putExtra("TYPE", "post");
                intent.putExtra("POSTID", postId);
                this.startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
