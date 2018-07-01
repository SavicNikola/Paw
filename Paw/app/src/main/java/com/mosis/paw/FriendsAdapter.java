package com.mosis.paw;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.MyViewHolder> implements Filterable {

    private Context mContext;
    private List<Friend> friendsList;
    private List<Friend> friendListFiltered;

    // za select
    private FriendsAdapterListener listener;

    // View holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, place;
        public ImageView thumbnail;

        public MyViewHolder(View v) {
            super(v);

            name = v.findViewById(R.id.friend_name);
            place = v.findViewById(R.id.friend_place);
            thumbnail = v.findViewById(R.id.friend_thumbnail);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // selektovan prijatelj
                    listener.onFriendSelected(friendListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public FriendsAdapter(Context mContext, List<Friend> friendsList, FriendsAdapterListener listener) {
        this.mContext = mContext;
        this.friendsList = friendsList;
        this.friendListFiltered = friendsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Friend friend = friendListFiltered.get(position);

        holder.name.setText(friend.getName());
        holder.place.setText(friend.getPlace());

        Glide.with(mContext)
                .load(friend.getImage())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return friendListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String sequence = constraint.toString();

                if (sequence.isEmpty()) {
                    friendListFiltered = friendsList;
                } else {

                    List<Friend> filteredList = new ArrayList<>();
                    for (Friend friend : friendsList) {
                        if (friend.getName().toLowerCase().contains(sequence.toLowerCase()))
                            filteredList.add(friend);
                    }

                    friendListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = friendListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                friendListFiltered = (ArrayList<Friend>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface FriendsAdapterListener {
        void onFriendSelected(Friend friend);
    }
}
