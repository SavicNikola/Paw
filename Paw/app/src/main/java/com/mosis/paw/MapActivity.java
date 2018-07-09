package com.mosis.paw;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mosis.paw.Model.Post;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private List<MarkerOptions> markersList;

    private String typeOfMap, typeOfFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_friends);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.friends_map_fragment);
        mapFragment.getMapAsync(this);

        markersList = new ArrayList<MarkerOptions>();

        Bundle extras = getIntent().getExtras();
        typeOfMap = extras == null ? null : extras.getString("TYPE", null);
        typeOfFeed = extras == null ? null : extras.getString("FEED", null);

        if (typeOfMap != null)
            switch (typeOfMap) {
                case "friends":
                    EditToolbar("Friends on map");
                    initFriendsMarkers();
                    break;
                case "users":
                    EditToolbar("Users on map");
                    initUsersMarkers();
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
            case R.id.map_filters:
                Intent intent = new Intent(this, FiltersActivity.class);
                startActivity(intent);
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
                                            final Pawer friend = dataSnapshot.getValue(Pawer.class);

                                            if (friend.getLatitude() != null && friend.getLongitude() != null) {
                                                FirebaseSingleton.getInstance().storageReference
                                                        .child("profile_images")
                                                        .child(friend.getEscapedEmail())
                                                        .getDownloadUrl()
                                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                Glide.with(MapActivity.this)
                                                                        .asBitmap()
                                                                        .load(uri)
                                                                        .apply(RequestOptions.circleCropTransform())
                                                                        .into(new SimpleTarget<Bitmap>() {
                                                                            @Override
                                                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                                                MarkerOptions markerOptions = new MarkerOptions()
                                                                                        .icon(BitmapDescriptorFactory.fromBitmap(makeStackedBitmap(resource)))
                                                                                        .position(new LatLng(Double.valueOf(friend.getLatitude()), Double.valueOf(friend.getLongitude())));

                                                                                markersList.add(markerOptions);
                                                                                showMarkers();
                                                                            }
                                                                        });
                                                            }

                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Glide.with(MapActivity.this)
                                                                        .asBitmap()
                                                                        .load(R.drawable.no_image_available)
                                                                        .apply(RequestOptions.circleCropTransform())
                                                                        .into(new SimpleTarget<Bitmap>() {
                                                                            @Override
                                                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                                                MarkerOptions markerOptions = new MarkerOptions()
                                                                                        .icon(BitmapDescriptorFactory.fromBitmap(makeStackedBitmap(resource)))
                                                                                        .position(new LatLng(Double.valueOf(friend.getLatitude()), Double.valueOf(friend.getLongitude())));

                                                                                markersList.add(markerOptions);
                                                                                showMarkers();
                                                                            }
                                                                        });
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
                            final Pawer user = userSnapshot.getValue(Pawer.class);

                            if (user.getLatitude() != null && user.getLongitude() != null) {
                                FirebaseSingleton.getInstance().storageReference
                                        .child("profile_images")
                                        .child(user.getEscapedEmail())
                                        .getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Glide.with(MapActivity.this)
                                                        .asBitmap()
                                                        .load(uri)
                                                        .apply(RequestOptions.circleCropTransform())
                                                        .into(new SimpleTarget<Bitmap>() {
                                                            @Override
                                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                                MarkerOptions markerOptions = new MarkerOptions()
                                                                        .icon(BitmapDescriptorFactory.fromBitmap(makeStackedBitmap(resource)))
                                                                        .position(new LatLng(Double.valueOf(user.getLatitude()), Double.valueOf(user.getLongitude())));

                                                                markersList.add(markerOptions);
                                                                showMarkers();
                                                            }
                                                        });
                                            }

                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Glide.with(MapActivity.this)
                                                        .asBitmap()
                                                        .load(R.drawable.no_image_available)
                                                        .apply(RequestOptions.circleCropTransform())
                                                        .into(new SimpleTarget<Bitmap>() {
                                                            @Override
                                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                                MarkerOptions markerOptions = new MarkerOptions()
                                                                        .icon(BitmapDescriptorFactory.fromBitmap(makeStackedBitmap(resource)))
                                                                        .position(new LatLng(Double.valueOf(user.getLatitude()), Double.valueOf(user.getLongitude())));

                                                                markersList.add(markerOptions);
                                                                showMarkers();
                                                            }
                                                        });
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

    void initSinglePostMarker(String postId) {
        FirebaseSingleton.getInstance().databaseReference
                .child("posts")
                .child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Post post = dataSnapshot.getValue(Post.class);

                        markersList.clear();

                        if (post.getMapLatitude() != null && post.getMapLongitude() != null) {
                            markersList.add(new MarkerOptions()
                                    .position(new LatLng(Double.valueOf(post.getMapLatitude()), Double.valueOf(post.getMapLongitude()))));
                            showMarkers();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    void initPostsMarkers() {
        FirebaseSingleton.getInstance().databaseReference
                .child("posts")
                .orderByChild("type")
                .equalTo(typeOfFeed) // filter po feed
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Post post;
                        markersList.clear();

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            post = postSnapshot.getValue(Post.class);

                            if (enterToFeed(post))
                                if (post.getMapLatitude() != null && post.getMapLongitude() != null) {
                                    markersList.add(new MarkerOptions()
                                            .position(new LatLng(Double.valueOf(post.getMapLatitude()), Double.valueOf(post.getMapLongitude()))));
                                    showMarkers();
                                }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private boolean enterToFeed(Post post) {

        Boolean type, size, color, radius;

        Location postLocation = new Location("");
        postLocation.setLatitude(Double.valueOf(post.getMapLatitude()));
        postLocation.setLongitude(Double.valueOf(post.getMapLongitude()));

        Location myLocation = new Location("");
        myLocation.setLatitude(Double.valueOf(Pawer.getInstance().getLatitude()));
        myLocation.setLongitude(Double.valueOf(Pawer.getInstance().getLongitude()));

        type = Pawer.getInstance().getFilter().getType().equals("All") ||
                (post.getAnimalType() != null && Pawer.getInstance().getFilter().getType().equals(post.getAnimalType()));

        size = Pawer.getInstance().getFilter().getSize().equals("All") ||
                (post.getAnimalSize() != null && Pawer.getInstance().getFilter().getSize().equals(post.getAnimalSize()));

        color = Pawer.getInstance().getFilter().getColor().equals("All") ||
                (post.getAnimalColor() != null && Pawer.getInstance().getFilter().getColor().equals(post.getAnimalColor()));

        radius = Pawer.getInstance().getFilter().getRadius().equals("All") || (Double.valueOf(Pawer.getInstance().getFilter().getRadius()) > postLocation.distanceTo(myLocation));

        return type && size && color && radius;
    }

    void showMarkers() {

        mMap.clear();

        for (MarkerOptions marker : markersList) {
            mMap.addMarker(marker);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        switch (typeOfMap) {
            case "friends":
                getMenuInflater().inflate(R.menu.users_map_menu, menu);
                break;

            case "feed":
                getMenuInflater().inflate(R.menu.feed_map_menu, menu);
                break;
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        initPostsMarkers();
    }

    private Bitmap makeStackedBitmap(final Bitmap foreground) {
        Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.map_marker);
        Bitmap result = Bitmap.createBitmap(background.getWidth(), background.getHeight(), background.getConfig());   //Initialize the result image
        Canvas canvas = new Canvas(result);   //Create a canvas so we can draw onto the result image
        canvas.drawBitmap(background, 0, 0, null);   //Draw the background
        canvas.drawBitmap(foreground, 230, 35, null);   //Draw the foreground. Change (0, 0) if you want.
        return result.createScaledBitmap(result, 160, 160, false);   //Returns single image with the background and foreground
    }
}
