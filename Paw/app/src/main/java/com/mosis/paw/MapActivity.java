package com.mosis.paw;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private List<LatLng> markersList;

    private String typeOfMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_friends);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.friends_map_fragment);
        mapFragment.getMapAsync(this);

        markersList = new ArrayList<LatLng>();

        Bundle extras = getIntent().getExtras();
        typeOfMap = extras == null ? null : extras.getString("TYPE", null);

        if (typeOfMap != null)
            switch (typeOfMap) {
                case "friends":
                    EditToolbar("Friends on map");
                    initFriendsMarkers();
                    break;
                case "post":
                    EditToolbar("Post on map");
                    String postId = extras.getString("POSTID", null);
                    if (postId != null)
                        initSinglePostMarker(postId);
                    break;
                case "feed":
                    EditToolbar("Posts on map");
                    initPostsMarkers();
                    break;
            }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void EditToolbar(String title) {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.map_only_friends:
                getSupportActionBar().setTitle("Friends on map");
                initFriendsMarkers();
                break;
            case R.id.map_all_users:
                getSupportActionBar().setTitle("Users on map");
                initUsersMarkers();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void initFriendsMarkers() {
        FirebaseSingleton.getInstance().databaseReference
                .child("friends")
                .child(Pawer.getInstance().getEmail())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        markersList.clear();

                        for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                            String friendEmail = friendSnapshot.getValue(String.class);

                            FirebaseSingleton.getInstance().databaseReference
                                    .child("users")
                                    .child(friendEmail)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Pawer friend = dataSnapshot.getValue(Pawer.class);

                                            if (friend.getLatitude() != null && friend.getLongitude() != null) {
                                                markersList.add(new LatLng(Double.valueOf(friend.getLatitude()), Double.valueOf(friend.getLongitude())));
                                                showMarkers();
                                            }
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

    void initUsersMarkers() {
        FirebaseSingleton.getInstance().databaseReference
                .child("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        markersList.clear();

                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            Pawer user = userSnapshot.getValue(Pawer.class);

                            if (user.getLatitude() != null && user.getLongitude() != null) {
                                markersList.add(new LatLng(Double.valueOf(user.getLatitude()), Double.valueOf(user.getLongitude())));
                                showMarkers();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    void initSinglePostMarker(String postId) {

    }

    void initPostsMarkers() {

    }

    void showMarkers() {

        mMap.clear();

        for (LatLng marker : markersList) {
            mMap.addMarker(new MarkerOptions().position(marker));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (typeOfMap.equals("friends"))
            getMenuInflater().inflate(R.menu.users_map_menu, menu);

        return true;
    }
}
