package com.mosis.paw;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mosis.paw.Model.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    TextView myRank, myPoints, numOfUsers, numOfAdopt, numOfFound, numOfLost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        EditToolbar();

        initViews();
        initStatisticsFromDatabase();
    }

    private void initViews() {
        myRank = findViewById(R.id.statistics_myrank);
        myPoints = findViewById(R.id.statistics_mypoints);
        numOfUsers = findViewById(R.id.statistics_numofusers);
        numOfAdopt = findViewById(R.id.statistics_numofadopt);
        numOfFound = findViewById(R.id.statistics_numoffound);
        numOfLost = findViewById(R.id.statistics_numoflost);

        // points from singleton
        myPoints.setText(Pawer.getInstance().getPoints());
    }

    private void initStatisticsFromDatabase() {

        // number of users and rank
        FirebaseSingleton.getInstance().databaseReference
                .child("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        List<Pawer> usersList = new ArrayList<>();

                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            Pawer user = userSnapshot.getValue(Pawer.class);
                            usersList.add(user);
                        }

                        // number of users
                        numOfUsers.setText(String.valueOf(usersList.size()));

                        Collections.sort(usersList, new Comparator<Pawer>() {
                            @Override
                            public int compare(Pawer o1, Pawer o2) {
                                return (Integer.valueOf(o2.getPoints()).compareTo(Integer.valueOf(o1.getPoints())));
                            }
                        });

                        for (int i = 0; i < usersList.size(); ++i) {
                            if (usersList.get(i).getEmail().equals(Pawer.getInstance().getEmail())) {

                                // my rank
                                myRank.setText(String.valueOf(i + 1));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        // number adopted and founded
        FirebaseSingleton.getInstance().databaseReference
                .child("ex_posts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        int adoptNum = 0;
                        int lostNum = 0;

                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            Post post = userSnapshot.getValue(Post.class);

                            if (post.getType() != null) {
                                if (post.getType().equals("adopt"))
                                    adoptNum++;
                                else if (post.getType().equals("lost"))
                                    lostNum++;
                            }
                        }

                        // adopt num
                        numOfAdopt.setText(String.valueOf(adoptNum));

                        // found num or ex lost num
                        numOfFound.setText(String.valueOf(lostNum));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        // number of currently lost
        FirebaseSingleton.getInstance().databaseReference
                .child("posts")
                .orderByChild("type")
                .equalTo("lost") // filter po feed
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // num of lost
                        numOfLost.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void EditToolbar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
        getSupportActionBar().setTitle("Statistics");
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

    public void showLeaderboard(View view) {
        Intent intent = new Intent(this, LeaderBoardActivity.class);
        startActivity(intent);
    }
}
