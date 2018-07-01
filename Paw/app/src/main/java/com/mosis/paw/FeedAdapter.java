package com.mosis.paw;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MyViewHolder> {

    private Context mContext;
    private List<FeedItem> feedItemsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView imePrezime, timeAgo, postDesc;
        private ImageView thumbnail, postPicture;
        private ImageButton favouriteButton;
        private Button shareButton;

        CardView card;

        public MyViewHolder(View itemView) {
            super(itemView);

            imePrezime = (TextView) itemView.findViewById(R.id.ime_prezime);
            timeAgo = (TextView) itemView.findViewById(R.id.time_ago);
            postDesc = (TextView) itemView.findViewById(R.id.post_desc);

            thumbnail = (ImageView) itemView.findViewById(R.id.post_thumbnail);
            postPicture = (ImageView) itemView.findViewById(R.id.post_picture);

            favouriteButton = (ImageButton) itemView.findViewById(R.id.post_favourite_button);
            shareButton = (Button) itemView.findViewById(R.id.post_share_button);

            card = itemView.findViewById(R.id.feed_card);
        }
    }

    public FeedAdapter(Context mContext, List<FeedItem> feedItemsList) {
        this.mContext = mContext;
        this.feedItemsList = feedItemsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FeedItem post = feedItemsList.get(position);

        // load thumbnail
        Glide.with(mContext).load(post.getThumbnail()).into(holder.thumbnail);

        // load ime prezime vreme
        holder.imePrezime.setText(post.getImePrezime());
        holder.timeAgo.setText(post.getTimeAgo());
        holder.postDesc.setText(post.getPostDesc());

        // load picture
        Glide.with(mContext).load(post.getPostPicture()).into(holder.postPicture);

        holder.favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Favourite", Toast.LENGTH_SHORT).show();
            }
        });

        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Share", Toast.LENGTH_SHORT).show();
            }
        });

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PostInformationActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedItemsList.size();
    }
}
