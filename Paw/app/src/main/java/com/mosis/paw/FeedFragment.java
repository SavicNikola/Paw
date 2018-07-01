package com.mosis.paw;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    private RecyclerView recyclerView;
    private FeedAdapter adapter;
    private List<FeedItem> feedList;

    private FloatingActionButton fab;

    private String feedName;

    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mView = view;

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            feedName = bundle.getString("feed", "feed");
        }

        switch (feedName) {
            case "lost":
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Lost feed");
                break;

            case "found":
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Found feed");
                break;

            case "adopt":
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Adopt feed");
                break;
        }

        InitRecycleView();
        InitFabButton();
    }

    private void InitRecycleView() {
        recyclerView = (RecyclerView) mView.findViewById(R.id.feed_recycler_view);

        //proba glupi podaci
        feedList = new ArrayList<>();
        FeedItem item = new FeedItem("Marko Markovic", R.drawable.avatar1, "2h ago", "Korisnikov opis posta, na primer kratke informacije o izgubljenom ljubimcu.. Klikom na post dobice vise informacija..", R.drawable.picture1, FeedTypeEnum.FOUND, true);
        FeedItem item2 = new FeedItem("Nikola Niki", R.drawable.avatar2, "1h ago", "Korisnikov opis posta, na primer kratke informacije o izgubljenom ljubimcu..", R.drawable.picture2, FeedTypeEnum.LOST, true);
        FeedItem item3 = new FeedItem("Stefan Steki", R.drawable.avatar3, "2h ago", "Klikom na post dobice vise informacija..", R.drawable.picture3, FeedTypeEnum.ADOPT, true);
        feedList.add(item);
        feedList.add(item2);
        feedList.add(item3);

        adapter = new FeedAdapter(getActivity(), feedList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }

    private void InitFabButton() {
        // fab button
        fab = (FloatingActionButton) mView.findViewById(R.id.fab);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy < 0 && !fab.isShown())
                    fab.show();
                else if (dy > 0 && fab.isShown())
                    fab.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AddActivity.class);
                getContext().startActivity(i);
            }
        });
    }
}
