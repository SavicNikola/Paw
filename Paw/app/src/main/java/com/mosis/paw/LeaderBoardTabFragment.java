package com.mosis.paw;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderBoardTabFragment extends Fragment implements LeaderboardAdapter.LeaderboardAdapterListener {

    String sortBy;

    View mView;

    private RecyclerView recyclerView;
    private List<Pawer> usersList;
    private LeaderboardAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaderboard_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.mView = view;

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sortBy = bundle.getString("SORTBY", "points");
        }

        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView = mView.findViewById(R.id.leaderboard_recycle_view);
        usersList = new ArrayList<>();

        mAdapter = new LeaderboardAdapter(getActivity(), usersList, sortBy, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        initListFromDatabase();
    }

    private void initListFromDatabase() {
        FirebaseSingleton.getInstance().databaseReference
                .child("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        usersList.clear();

                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                            Pawer user = userSnapshot.getValue(Pawer.class);

                            usersList.add(user);

                            switch (sortBy) {
                                case "points":
                                    Collections.sort(usersList, new Comparator<Pawer>() {
                                        @Override
                                        public int compare(Pawer o1, Pawer o2) {
                                            return (Integer.valueOf(o2.getPoints()).compareTo(Integer.valueOf(o1.getPoints())));
                                        }
                                    });
                                    break;

                                case "helps":
                                    Collections.sort(usersList, new Comparator<Pawer>() {
                                        @Override
                                        public int compare(Pawer o1, Pawer o2) {
                                            return (Integer.valueOf(o2.getHelps()).compareTo(Integer.valueOf(o1.getHelps())));
                                        }
                                    });
                                    break;

                                case "friends":
                                    Collections.sort(usersList, new Comparator<Pawer>() {
                                        @Override
                                        public int compare(Pawer o1, Pawer o2) {
                                            return (Integer.valueOf(o2.getFriends()).compareTo(Integer.valueOf(o1.getFriends())));
                                        }
                                    });
                                    break;
                            }

                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onLeaderboardSelected(Pawer user) {
        Toast.makeText(getContext(), user.getName(), Toast.LENGTH_SHORT).show();
    }
}
