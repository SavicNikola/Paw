package com.mosis.paw;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mosis.paw.Model.GeofireLocationObject;

import java.util.ArrayList;

public class LocationTrackingService extends Service {

    private static final int NOTIF_TYPE_PAWER_NERBY = 1;
    private static final String NOTIF_CHANNEL_ID = "channel_id";
    private static final int NOTIFICATION_ID = 5;
    private static final int RC_NOTIFICATION = 3;

    private FusedLocationProviderClient client;
    private LocationCallback locationCallback;
    private GeoFire geoFire;
    private ArrayList<GeoQuery> geoQueries = new ArrayList<>();
    private ArrayList<GeoLocation> geoLocations = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference("/geofire"));
        startGettingLocationUpdates();
        getUserLocationsFromServer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        client.removeLocationUpdates(locationCallback);
        for (GeoQuery q : geoQueries) {
            q.removeAllListeners();
        }
    }

    private void getUserLocationsFromServer() {
        FirebaseSingleton.getInstance().databaseReference
                .child("geofire")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            GeofireLocationObject o = ds.getValue(GeofireLocationObject.class);
                            o.setKey(ds.getKey());
                            if (!o.getKey().equals(Pawer.getInstance().getEscapedEmail())) {
                                geoLocations.add(o.getLocation());
                            }
                        }
                        setupGeofences();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setupGeofences() {
        for (GeoLocation location : geoLocations) {
            GeoQuery geoQuery = geoFire.queryAtLocation(location, 0.1);
            geoQueries.add(geoQuery);
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    if (key.equals(Pawer.getInstance().getEscapedEmail())) {        //bug je bio sto se na pocetku uvek poziva za sve usere enter
                        displayNotification(location, NOTIF_TYPE_PAWER_NERBY);                //key je onaj koji ulazi u oblast
                    }                                                               //znaci, nama treba key trenutnog user-a.
                }

                @Override
                public void onKeyExited(String key) {
                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {
                }

                @Override
                public void onGeoQueryReady() {
                }

                @Override
                public void onGeoQueryError(DatabaseError error) {
                }
            });
        }
    }

    private void displayNotification(GeoLocation location, int notificaitonType) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, RC_NOTIFICATION,
                new Intent(this, MapFriendsActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        .putExtra("longitude", location.longitude)
                        .putExtra("latitude", location.latitude),
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_paw_accent)
                .setSound(sound)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (notificaitonType == NOTIF_TYPE_PAWER_NERBY) {
            builder.setContentTitle("Ljubitelj zivotinja u blizini!")
                    .setContentText("Klikni ovde da saznas vise.");
        }

        NotificationManagerCompat nmc = NotificationManagerCompat.from(this);
        nmc.notify(NOTIFICATION_ID, builder.build());
    }


    public void startGettingLocationUpdates() {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: PERMISIJEEEEE

            return;
        }
        this.locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(final LocationResult locationResult) {
                FirebaseSingleton.getInstance().databaseReference
                        .child("users")
                        .child(Pawer.getInstance().getEscapedEmail())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                dataSnapshot.child("latitude").getRef().setValue(String.valueOf(locationResult.getLastLocation().getLatitude()));
                                dataSnapshot.child("longitude").getRef().setValue(String.valueOf(locationResult.getLastLocation().getLongitude()));
                                geoFire.setLocation(Pawer.getInstance().getEscapedEmail(), new GeoLocation(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        };
        client.requestLocationUpdates(locationRequest, locationCallback, null);
    }
}
