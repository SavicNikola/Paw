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
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.util.DataUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mosis.paw.Model.Post;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeedFragment extends Fragment {

    private RecyclerView recyclerView;
    private FeedAdapter adapter;
    private List<FeedItem> feedList;

    private FloatingActionButton fab;
    private Button filtersButton;

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
            case "favourites":
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Your favourites posts");
                break;
        }

        filtersButton = mView.findViewById(R.id.feed_filters_btn);
        initFiltersButton();
        InitRecycleView();
        InitFabButton();
    }

    private void initFiltersButton() {

        String text = "FILTERS:     ";
        if (Pawer.getInstance().getFilter().getColor().equals("All") &&
            Pawer.getInstance().getFilter().getSize().equals("All") &&
            Pawer.getInstance().getFilter().getType().equals("All")) {
            text += "ALL";
        } else {
            if (!Pawer.getInstance().getFilter().getType().equals("All")) {
                text += Pawer.getInstance().getFilter().getType();
                text += "  |  ";
            }

            if (!Pawer.getInstance().getFilter().getColor().equals("All")) {
                text += Pawer.getInstance().getFilter().getColor();
                text += "  |  ";
            }

            if (!Pawer.getInstance().getFilter().getSize().equals("All")) {
                text += Pawer.getInstance().getFilter().getSize();
                text += "  |  ";
            }

            text = text.substring(0, text.length() - 5);
        }

        filtersButton.setText(text);

        filtersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FiltersActivity.class);
                startActivity(intent);
            }
        });
    }

    private void InitRecycleView() {
        recyclerView = (RecyclerView) mView.findViewById(R.id.feed_recycler_view);

        feedList = new ArrayList<FeedItem>();

        if (feedName.equals("favourites"))
            initFavouriteFeed();
        else
            initFeedFromDatabase();

        adapter = new FeedAdapter(getActivity(), feedList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }

    private void initFeedFromDatabase() {

        FirebaseSingleton.getInstance().databaseReference
                .child("posts")
                .orderByChild("type")
                .equalTo(feedName) // filter po feed
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Post post;
                feedList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // TODO: handle the post
                    post = postSnapshot.getValue(Post.class);

                    if (enterToFeed(post)) {

                        final Post finalPost = post;
                        FirebaseSingleton.getInstance().databaseReference
                                .child("users")
                                .child(post.getUserId())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Pawer user = dataSnapshot.getValue(Pawer.class);
                                        Post inerPost = finalPost;

                                        int avatar = SwitchAvatar(user.getAvatar());

                                        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(Long.valueOf(inerPost.getTime()));

                                        FeedTypeEnum feedType = SwitchType(inerPost.getType());

                                        Boolean favourite = false;

                                        if (Pawer.getInstance().getFavourites() != null) {
                                            favourite = Pawer.getInstance().getFavourites().contains(inerPost.getId());
                                        }

                                        // TODO: da se refaktorise, post slika da se ubaci
                                        feedList.add(new FeedItem(inerPost.getId(), user.getName(), avatar, timeAgo.toString(), inerPost.getDescription(), R.drawable.picture3, feedType, favourite));
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

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

    void initFavouriteFeed() {

        FirebaseSingleton.getInstance().databaseReference
                .child("users")
                .child(Pawer.getInstance().getEmail())
                .child("favourites")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String favourite;
                        feedList.clear();

                        for (DataSnapshot favouritesSnapshot : dataSnapshot.getChildren()) {

                            favourite = favouritesSnapshot.getValue(String.class);

                            FirebaseSingleton.getInstance().databaseReference
                                    .child("posts")
                                    .child(favourite)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                        final Post post = dataSnapshot.getValue(Post.class);

                                            FirebaseSingleton.getInstance().databaseReference
                                                    .child("users")
                                                    .child(post.getUserId())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                                            Post inerPost = post;
                                                            Pawer user = dataSnapshot.getValue(Pawer.class);

                                                            int avatar = SwitchAvatar(user.getAvatar());

                                                            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(Long.valueOf(inerPost.getTime()));

                                                            FeedTypeEnum feedType = SwitchType(inerPost.getType());

                                                            // TODO: da se refaktorise, post slika da se ubaci
                                                            feedList.add(new FeedItem(post.getId(), user.getName(), avatar, timeAgo.toString(), inerPost.getDescription(), R.drawable.picture3, feedType, true));
                                                            adapter.notifyDataSetChanged();
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
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

    private boolean enterToFeed(Post post) {

        Boolean type, size, color;

        type = Pawer.getInstance().getFilter().getType().equals("All") ||
                (post.getAnimalType() != null && Pawer.getInstance().getFilter().getType().equals(post.getAnimalType()));

        size = Pawer.getInstance().getFilter().getSize().equals("All") ||
                (post.getAnimalSize() != null && Pawer.getInstance().getFilter().getSize().equals(post.getAnimalSize()));

        color = Pawer.getInstance().getFilter().getColor().equals("All") ||
                (post.getAnimalColor() != null && Pawer.getInstance().getFilter().getColor().equals(post.getAnimalColor()));

        return type && size && color;
    }

    private FeedTypeEnum SwitchType(String type) {

        FeedTypeEnum feedType = FeedTypeEnum.LOST; //default
        switch (type) {
            case "lost":
                feedType = FeedTypeEnum.LOST;
                break;
            case "found":
                feedType = FeedTypeEnum.FOUND;
                break;
            case "adopt":
                feedType = FeedTypeEnum.ADOPT;
                break;
        }

        return feedType;
    }

    private int SwitchAvatar(Integer input) {

        int avatar = R.drawable.avatar1; //default
        switch (input) {
            case 1:
                avatar = R.drawable.avatar1;
                break;
            case 2:
                avatar = R.drawable.avatar2;
                break;
            case 3:
                avatar = R.drawable.avatar3;
                break;
        }

        return avatar;
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
                // type of post
                i.putExtra("TYPE", feedName);
                getContext().startActivity(i);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        initFiltersButton();
        initFeedFromDatabase();
    }
}
