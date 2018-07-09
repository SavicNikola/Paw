package com.mosis.paw;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mosis.paw.Model.PawNotification;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity implements NotificationAdapter.NotificationAdapterListener {

    private RecyclerView recyclerView;
    private List<PawNotification> notificationList;
    private NotificationAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        EditToolbar();

        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.notification_recycle_view);
        notificationList = new ArrayList<>();

        mAdapter = new NotificationAdapter(this, notificationList,this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        initListFromDatabase();
    }

    private void initListFromDatabase() {
        FirebaseSingleton.getInstance().databaseReference
                .child("notifications")
                .child(Pawer.getInstance().getEmail())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        notificationList.clear();

                        for (DataSnapshot nDataSnapshot : dataSnapshot.getChildren()) {
                            String notificationID = nDataSnapshot.getValue(String.class);

                            FirebaseSingleton.getInstance().databaseReference
                                    .child("notification_data")
                                    .child(notificationID)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            PawNotification notification = dataSnapshot.getValue(PawNotification.class);

                                            notificationList.add(notification);
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

    @Override
    public void onNotificationSelected(PawNotification notification, int position) {
        Intent i = new Intent(this, NotificationInfoActivity.class);
        i.putExtra("NotID", notification.getId());
        this.startActivity(i);
    }

    private void EditToolbar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
        getSupportActionBar().setTitle("Notifications");
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
