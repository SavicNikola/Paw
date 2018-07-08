package com.mosis.paw;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mosis.paw.Model.PawNotification;

public class NotificationInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    TextView type, time, desc, num;
    ImageView image;
    Button dismiss, thankyou;
    LinearLayout numLayout;

    String notificationID, notificationIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_info);

        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            notificationID = extras.getString("NotID");
            notificationIndex = extras.getString("NotIndex");
        }

        EditToolbar();

        initMap();
        initViews();

        initData();

        //TODO: CHANGE READ ON TRUE
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void initMap() {
        ScrollableMapFragment mapFragment = (ScrollableMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.not_info_map);
        mapFragment.getMapAsync(this);

        final ScrollView scrollView = (ScrollView) findViewById(R.id.not_info_scroll);
        ((ScrollableMapFragment) getSupportFragmentManager().findFragmentById(R.id.not_info_map)).setListener(new ScrollableMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });
    }

    private void initViews() {
        type = findViewById(R.id.not_info_type);
        time = findViewById(R.id.not_info_time);
        desc = findViewById(R.id.not_info_desc);
        num = findViewById(R.id.not_info_number);

        image = findViewById(R.id.not_info_picture);

        dismiss = findViewById(R.id.not_info_dismiss);
        thankyou = findViewById(R.id.not_info_thankyou);

        numLayout = findViewById(R.id.not_info_num_layout);
    }

    private void initData() {
        FirebaseSingleton.getInstance().databaseReference
                .child("notification_data")
                .child(notificationID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final PawNotification notification = dataSnapshot.getValue(PawNotification.class);

                        if (notification != null) {

                            // type
                            type.setText(notification.getType().toUpperCase());

                            // time
                            CharSequence date = DateUtils.getRelativeTimeSpanString(Long.valueOf(notification.getTime()));
                            time.setText(date.toString());

                            //desc
                            if (notification.getDescription() != null && !notification.getDescription().equals(""))
                                desc.setText(notification.getDescription());

                            //num
                            if (notification.getNumber() != null) {
                                numLayout.setVisibility(View.VISIBLE);
                                num.setText(notification.getNumber());
                            }

                            //image
                            if (notification.getPicture() != null)
                                Glide.with(NotificationInfoActivity.this)
                                        .load(notification.getPicture())
                                        .into(image);

                            //marker on map
                            LatLng location = new LatLng(Double.valueOf(notification.getLatitude()), Double.valueOf(notification.getLongitude()));
                            mMap.addMarker(new MarkerOptions().position(location).title("Your paw"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14.0f));

                            //dismiss
                            dismiss.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    // DELETE NOTIFICATION DATA
                                    FirebaseSingleton.getInstance().databaseReference
                                            .child("notification_data")
                                            .child(notificationID)
                                            .removeValue();

                                    // DELETE NOTIFICATION
                                    FirebaseSingleton.getInstance().databaseReference
                                            .child("notifications")
                                            .child(Pawer.getInstance().getEmail())
                                            .child(notificationIndex)
                                            .removeValue();

                                    Toast.makeText(v.getContext(), "We're sorry!", Toast.LENGTH_SHORT).show();

                                    onBackPressed();
                                }
                            });

                            //thankyou
                            thankyou.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    // ADD POINTS
                                    FirebaseSingleton.getInstance().databaseReference
                                            .child("users")
                                            .child(notification.getUser())
                                            .child("points")
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    String points = dataSnapshot.getValue(String.class);
                                                    Integer pts = Integer.valueOf(points);

                                                    //add 10 pts for adopt, 5 for lost (found), -5 for found (lost)
                                                    switch (notification.getType()) {
                                                        case "adopt":
                                                            pts += 10;
                                                            break;
                                                        case "lost":
                                                            pts += 5;
                                                            break;
                                                        case "found":
                                                            pts -= 5;
                                                            break;
                                                    }

                                                    FirebaseSingleton.getInstance().databaseReference
                                                            .child("users")
                                                            .child(notification.getUser())
                                                            .child("points")
                                                            .setValue(pts.toString());
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                    // DELETE NOTIFICATION DATA
                                    FirebaseSingleton.getInstance().databaseReference
                                            .child("notification_data")
                                            .child(notificationID)
                                            .removeValue();

                                    // DELETE NOTIFICATION
                                    FirebaseSingleton.getInstance().databaseReference
                                            .child("notifications")
                                            .child(Pawer.getInstance().getEmail())
                                            .child(notificationIndex)
                                            .removeValue();

                                    Toast.makeText(v.getContext(), "You're welcome!", Toast.LENGTH_SHORT).show();

                                    onBackPressed();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void EditToolbar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
        getSupportActionBar().setTitle("Notification info");
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
