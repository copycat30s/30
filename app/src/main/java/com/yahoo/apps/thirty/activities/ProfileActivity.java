package com.yahoo.apps.thirty.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.yahoo.apps.thirty.R;
import com.yahoo.apps.thirty.fragments.UserTimelineFragment;
import com.yahoo.apps.thirty.models.User;
import com.squareup.picasso.Picasso;

public class  ProfileActivity extends ActionBarActivity {
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = (User) getIntent().getSerializableExtra("user");
        populateViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    private void populateViews() {
        setTitle("@" + user.getScreenName());

        ImageView ivProfile = (ImageView) findViewById(R.id.ivProfile);
        Picasso.with(ProfileActivity.this)
                .load(user.getProfileImgUrl())
                .placeholder(R.drawable.placeholder_img)
                .into(ivProfile);

        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFriends = (TextView) findViewById(R.id.tvFriends);

        tvName.setText(user.getName());
        tvScreenName.setText("@" + user.getScreenName());
        tvFollowers.setText(user.getCountFollowers() + " Followers");
        tvFriends.setText(user.getCountFriends() + " Following");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        UserTimelineFragment fUserTimeline = UserTimelineFragment.newInstance(user.getUid());
        ft.replace(R.id.fUserTimeline, fUserTimeline);
        ft.commit();
    }
}
