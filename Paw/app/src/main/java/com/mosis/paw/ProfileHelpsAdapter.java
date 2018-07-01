package com.mosis.paw;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProfileHelpsAdapter extends RecyclerView.Adapter<ProfileHelpsAdapter.MyViewHolder>{

    private Context mContext;
    private List<FeedItem> helpsItemsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView type, timeAgo, postDesc;
        private ImageView postPicture;

        public MyViewHolder(View itemView) {
            super(itemView);

            type = (TextView) itemView.findViewById(R.id.profile_help_item_type);
            timeAgo = (TextView) itemView.findViewById(R.id.profile_help_item_time_ago);
            postDesc = (TextView) itemView.findViewById(R.id.profile_help_item_help_desc);

            postPicture = (ImageView) itemView.findViewById(R.id.profile_help_item_post_help_picture);
        }
    }

    public ProfileHelpsAdapter(Context mContext, List<FeedItem> helpsItemsList) {
        this.mContext = mContext;
        this.helpsItemsList = helpsItemsList;
    }

    @NonNull
    @Override
    public ProfileHelpsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_help_item, parent, false);
        return new ProfileHelpsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FeedItem post = helpsItemsList.get(position);

        // load
        holder.type.setText(post.getFeedType().toString());
        holder.timeAgo.setText(post.getTimeAgo());
        holder.postDesc.setText(post.getPostDesc());

        // load picture
        Glide.with(mContext).load(post.getPostPicture()).into(holder.postPicture);
    }


    @Override
    public int getItemCount() {
        return helpsItemsList.size();
    }
}
