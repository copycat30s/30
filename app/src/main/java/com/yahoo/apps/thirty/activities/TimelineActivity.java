package com.yahoo.apps.thirty.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.yahoo.apps.thirty.R;
import com.yahoo.apps.thirty.backbone.TumblrApplication;
import com.yahoo.apps.thirty.backbone.TumblrClient;
import com.yahoo.apps.thirty.fragments.HomeTimelineFragment;
import com.yahoo.apps.thirty.fragments.MentionsTimelineFragment;
import com.yahoo.apps.thirty.models.Tweet;
import com.yahoo.apps.thirty.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class TimelineActivity extends ActionBarActivity implements StatusDialog.StatusDialogListener {
    private HomeTimelineFragment fHomeTimeline;
    private MentionsTimelineFragment fMentionsTimeline;
    private TumblrClient tumblrClient;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        tumblrClient = TumblrApplication.getTumblrClient();

        if (savedInstanceState == null) {
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
            PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
            tabStrip.setViewPager(viewPager);
            loadUserInfo();
        }
    }

    private void postStatus(String status) {
        tumblrClient.postStatus(status, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Tweet tweet = new Tweet(json);
                fHomeTimeline.add(0, tweet);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Log.i("ERROR", response.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_post) {
            showPostDialog();
            return true;
        } else if (id == R.id.action_profile) {
            Intent i = new Intent(this, ProfileActivity.class);
            i.putExtra("user", user);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showPostDialog() {
        FragmentManager fm = getFragmentManager();
        StatusDialog filterDialog = StatusDialog.newInstance();
        filterDialog.show(fm, "fragment_filter");
    }

    private void loadUserInfo() {
        tumblrClient.getUserInfo(0, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                user = new User(json);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Log.i("ERROR", response.toString());
            }
        });
    }

    @Override
    public void onFinishStatusDialog(String status) {
        postStatus(status);
    }

    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        private String tabItems[] = {"Home", "Mentions"};

        public TweetsPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);

            fHomeTimeline = new HomeTimelineFragment();
            fMentionsTimeline = new MentionsTimelineFragment();
        }

        @Override
        public int getCount() {
            return tabItems.length;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return fHomeTimeline;
            } else if (position == 1) {
                return fMentionsTimeline;
            } else {
                return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabItems[position];
        }
    }
}
