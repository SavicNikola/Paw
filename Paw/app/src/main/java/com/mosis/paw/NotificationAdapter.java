package com.mosis.paw;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mosis.paw.Model.PawNotification;

import java.text.DateFormat;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private Context mContext;
    private List<PawNotification> notificationList;

    private NotificationAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView type, time;

        public MyViewHolder(View itemView) {
            super(itemView);

            type = itemView.findViewById(R.id.notification_type);
            time = itemView.findViewById(R.id.notification_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onNotificationSelected(notificationList.get(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }

    public NotificationAdapter(Context mContext, List<PawNotification> notificationList, NotificationAdapterListener listener) {
        this.mContext = mContext;
        this.notificationList = notificationList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PawNotification notification = notificationList.get(position);

        holder.type.setText(notification.getType().toUpperCase());

        // TODO: format
        CharSequence date = DateUtils.getRelativeTimeSpanString(Long.valueOf(notification.getTime()));
        holder.time.setText(date.toString());
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public interface NotificationAdapterListener {
        void onNotificationSelected(PawNotification notification, int position);
    }
}
