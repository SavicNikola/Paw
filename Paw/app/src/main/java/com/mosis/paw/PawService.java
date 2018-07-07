package com.mosis.paw;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mosis.paw.Model.PawNotification;

public class PawService extends Service {

    private static final String TAG = "PawServiceTAG";
    private static final String NOTIFICATIONS = "notifications";
    private static final String NOTIFICATION_DATA = "notification_data";
    private static final String CHANNEL_ID = "paw_channel";
    private static int STANDARD_NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        setNotificationListener();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void setNotificationListener() {
        FirebaseSingleton.getInstance().databaseReference
                .child(NOTIFICATIONS)
                .child(Pawer.getInstance().getEscapedEmail())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String notificationId = (String) dataSnapshot.getValue();

                        if (notificationId == null)
                            return;

                        FirebaseSingleton.getInstance().databaseReference
                                .child(NOTIFICATION_DATA)
                                .child(notificationId)
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

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(PawService.this, CHANNEL_ID)
                .setContentTitle(notification.getType())
                .setContentText(notification.getDescription())
                .setColor(getResources().getColor(R.color.colorAccent))
                .setSmallIcon(R.drawable.ic_paw_accent)
                .setSound(sound)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

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
        nmc.notify(STANDARD_NOTIFICATION_ID++, builder.build());
    }


}
