package com.mosis.paw;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mosis.paw.Model.PawNotification;

public class PawService extends IntentService {

    private static final String NOTIFICATIONS = "notifications";
    private static final String NOTIFICATION_DATA = "notification_data";
    private static final String CHANNEL_ID = "paw_channel";
    private static final int STANDARD_NOTIFICATION_ID = 1;

    public PawService() {
        super("PawService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setNotificationListener();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setNotificationListener() {
        FirebaseSingleton.getInstance().databaseReference
                .child(NOTIFICATIONS)
                .child(Pawer.getInstance().getEscapedEmail())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Integer notificationId = (Integer) dataSnapshot.getValue();

                        if (notificationId == null)
                            return;

                        FirebaseSingleton.getInstance().databaseReference
                                .child(NOTIFICATION_DATA)
                                .child(notificationId.toString())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        PawNotification notification = dataSnapshot.getValue(PawNotification.class);
                                        displayNotification(notification);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void displayNotification(PawNotification notification) {
        if (notification == null) return;

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(PawService.this, CHANNEL_ID)
                .setContentTitle(notification.getType())
                .setContentText(notification.getDescription())
                .setColor(getResources().getColor(R.color.colorAccent))
                .setSmallIcon(R.drawable.ic_paw_accent);

        Glide.with(this)
                .asBitmap()
                .load(notification.getPicture())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(resource));
                    }
                });

        NotificationManagerCompat nmc = NotificationManagerCompat.from(this);
        nmc.notify(STANDARD_NOTIFICATION_ID, builder.build());
    }
}
