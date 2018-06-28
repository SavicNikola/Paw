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
import android.widget.EditText;

import android.widget.Toast;

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

        DummyItems();
    }

    void DummyItems() {
        Friend f = new Friend("Marko Markovic", "https://api.androidhive.info/json/images/tom_hardy.jpg", "Nis, Serbia");
        Friend f1 = new Friend("Nikola Nikolic", "https://api.androidhive.info/json/images/tom_hardy.jpg", "Belgrade, Serbia");
        Friend f2 = new Friend("Marko Stefanovic", "https://api.androidhive.info/json/images/tom_hardy.jpg", "Novi Sad, Serbia");

        friendsList.add(f);
        friendsList.add(f1);
        friendsList.add(f2);
    }

    private void EditToolbar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
        getSupportActionBar().setTitle("FRIENDS");
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFriendSelected(Friend friend) {
        Toast.makeText(getApplicationContext(), "Selected:" + friend.getName(), Toast.LENGTH_SHORT).show();
    }
}
