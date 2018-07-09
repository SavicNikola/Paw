package com.mosis.paw;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.Exclude;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MyViewHolder> {

    private Context mContext;
    private List<FeedItem> feedItemsList;
    private StorageReference storage = FirebaseSingleton.getInstance().storageReference;

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
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final FeedItem post = feedItemsList.get(position);

        // load thumbnail
        Glide.with(mContext).load(post.getThumbnail()).into(holder.thumbnail);

        // load ime prezime vreme
        holder.imePrezime.setText(post.getImePrezime());
        holder.timeAgo.setText(post.getTimeAgo());
        holder.postDesc.setText(post.getPostDesc());

        // load picture
        try {
            storage.child(post.getPostId() + "/img0").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(mContext).load(uri).into(holder.postPicture);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Glide.with(mContext).load(R.drawable.no_image_available).into(holder.postPicture);
                }
            });
        } catch (Exception e) {
            Log.e("STORAGE ERROR UHVACEN", "onBindViewHolder: " );
        }

        if (post.getFavoruite())
            holder.favouriteButton.setImageResource(R.drawable.ic_post_favourite_fill);

        holder.favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> favourites = Pawer.getInstance().getFavourites();

                if (favourites.contains(post.getPostId())) {
                    // delete favourite
                    favourites.remove(post.getPostId());
                    holder.favouriteButton.setImageResource(R.drawable.ic_post_favourite);
                } else {
                    // add fovourite
                    favourites.add(post.getPostId());
                    holder.favouriteButton.setImageResource(R.drawable.ic_post_favourite_fill);
                }

                FirebaseSingleton.getInstance().databaseReference
                        .child("users")
                        .child(Pawer.getInstance().getEmail())
                        .child("favourites")
                        .setValue(favourites)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(mContext, "Favourite update", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
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
                Intent intent = new Intent(mContext, PostInformationActivity.class)
                        .putExtra("postId", post.getPostId())
                        .putExtra("postCreator", post.getImePrezime());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedItemsList.size();
    }
}
