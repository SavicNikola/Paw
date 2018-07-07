package com.mosis.paw;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AvatarChooserAdapter extends ArrayAdapter {

    private List<Integer> images;

    public AvatarChooserAdapter(@NonNull Context context, int resource, List<Integer> images) {
        super(context, resource);
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.avatar_image_layout, parent, false);
        ImageView image = view.findViewById(R.id.image_layout);
        Glide.with(parent.getContext()).load(images.get(position)).into(image);
        return view;
    }
}
