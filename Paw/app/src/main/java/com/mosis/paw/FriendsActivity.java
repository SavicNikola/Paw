package com.mosis.paw;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends AppCompatActivity implements FriendsAdapter.FriendsAdapterListener {

    private RecyclerView recyclerView;
    private List<Friend> friendsList;
    private FriendsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        EditToolbar();

        recyclerView = findViewById(R.id.friends_recycle_view);
        friendsList = new ArrayList<>();
        mAdapter = new FriendsAdapter(this, friendsList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        initShowUsersOnMapButton();

        //DummyItems();
        InitListFromDatabase();
    }

    void InitListFromDatabase() {
        FirebaseSingleton.getInstance().databaseReference
                .child("friends")
                .child(Pawer.getInstance().getEmail())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        friendsList.clear();

                        for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                            String friendEmail = friendSnapshot.getValue(String.class);

                            FirebaseSingleton.getInstance().databaseReference
                                    .child("users")
                                    .child(friendEmail)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Friend friend = dataSnapshot.getValue(Friend.class);

                                            friend.setAvatar(SwitchAvatar(friend.getAvatar()));

                                            friendsList.add(friend);
                                            mAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    //TODO: da se refaktorise
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
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
        getSupportActionBar().setTitle("Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.friends_search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.friends_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.friends_search:
                return true;
            case R.id.friends_add:
                Intent intent = new Intent(this, AddFriendActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFriendSelected(Friend friend) {
        //Toast.makeText(getApplicationContext(), "Selected:" + friend.getName(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("UserID", friend.getEmail());
        startActivity(intent);
    }

    void initShowUsersOnMapButton() {
        Button mapBtn = findViewById(R.id.friends_show_map);

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapActivity.class);
                //type of map (friends, post, feed)
                intent.putExtra("TYPE", "friends");
                v.getContext().startActivity(intent);
            }
        });
    }
}
