package com.mosis.paw;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.MyViewHolder>{

    private Context mContext;
    private List<Pawer> usersItemsList;

    private LeaderboardAdapterListener listener;

    String sortBy;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView rank, name, city, value;

        public MyViewHolder(View itemView) {
            super(itemView);

            rank = itemView.findViewById(R.id.leaderboard_rank);
            name = itemView.findViewById(R.id.leaderboard_name);
            city = itemView.findViewById(R.id.leaderboard_city);
            value = itemView.findViewById(R.id.leaderboard_value);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onLeaderboardSelected(usersItemsList.get(getAdapterPosition()));
                }
            });
        }
    }

    public LeaderboardAdapter(Context context, List<Pawer> list, String sortBy, LeaderboardAdapterListener listener) {
        this.mContext = context;
        this.usersItemsList = list;

        this.sortBy = sortBy;

        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_item, parent, false);
        return new LeaderboardAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Pawer pawer = usersItemsList.get(position);

        Integer rank = position + 1;

        holder.rank.setText(rank.toString());
        holder.name.setText(pawer.getName());
        holder.city.setText(pawer.getCity());

        switch (sortBy) {
            case "points":
                holder.value.setText(pawer.getPoints());
                break;
            case "helps":
                holder.value.setText(pawer.getHelps());
                break;
            case "friends":
                holder.value.setText(pawer.getFriends());
                break;
            default:
                holder.value.setText("X");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return usersItemsList.size();
    }

    public interface LeaderboardAdapterListener {
        void onLeaderboardSelected(Pawer user);
    }
}
