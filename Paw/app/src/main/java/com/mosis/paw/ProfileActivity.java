package com.mosis.paw;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mosis.paw.Model.User;

public class ProfileActivity extends AppCompatActivity {

    Button btnProfile;

    public static final String PROFILE_TYPE = "profileType";
    public static final int PROFILE_FRIEND = 1;
    public static final int PROFILE_NOT_FRIEND = 2;
    public static final int PROFILE_CURRENT_USER = 3;

    int profileType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        User user = getIntent().getParcelableExtra("user");
        profileType = getIntent().getIntExtra(PROFILE_TYPE, 0);
        displayUserInfo(user);
    }

    private void initViews() {
        btnProfile = findViewById(R.id.btn_profile);
    }

    private void displayUserInfo(User user) {
        Glide.with(this)
                .load(user.getImageUrl())
                .into(((ImageView) findViewById(R.id.image_profile)));

        ((TextView) findViewById(R.id.text_user_name)).setText(user.getName());
        ((TextView) findViewById(R.id.text_user_location)).setText(user.getCity());

        switch (profileType) {
            case PROFILE_FRIEND:
                setUpButtonFriend();
                break;
            case PROFILE_NOT_FRIEND:
                setUpButtonNotFriend();
                break;
            case PROFILE_CURRENT_USER:
                setUpButtonMyProfile();
                break;
        }
    }

    private void setUpButtonMyProfile() {
        btnProfile.setText(R.string.remove);

        //todo: logika za editovanje profila
    }

    private void setUpButtonFriend() {
        btnProfile.setText(R.string.remove);

        //todo: logika za brisanje iz prijatelja
    }

    private void setUpButtonNotFriend() {
        btnProfile.setText(R.string.add);

        //todo: logika za dodavanje prijatelja
    }


}
