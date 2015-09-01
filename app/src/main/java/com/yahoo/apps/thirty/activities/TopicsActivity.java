package com.yahoo.apps.thirty.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yahoo.apps.thirty.R;
import com.yahoo.apps.thirty.backbone.TumblrApplication;
import com.yahoo.apps.thirty.backbone.TumblrClient;
import com.yahoo.apps.thirty.fragments.topic.HotTopicsFragment;
import com.yahoo.apps.thirty.fragments.topic.NewTopicsFragment;

import org.apache.http.Header;
import org.json.JSONObject;

public class TopicsActivity extends ActionBarActivity {
    private HotTopicsFragment fHotTopics;
    private NewTopicsFragment fNewTopics;
    private TumblrClient tumblrClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics);
        getSupportActionBar().hide();
        tumblrClient = TumblrApplication.getTumblrClient();

        if (savedInstanceState == null) {
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.setAdapter(new TopicsPagerAdapter(getSupportFragmentManager()));
            PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
            tabStrip.setViewPager(viewPager);
        }
    }

    private void postStatus(String targetId, String content) {
        tumblrClient.postStatus(content, targetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.i("ERROR", json.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Log.i("ERROR", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.i("ERROR", response);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_topics, menu);
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
            Intent i = new Intent(TopicsActivity.this, ComposeActivity.class);
            i.putExtra("targetId", "");
            startActivity(i);
            return true;
//        } else if (id == R.id.action_profile) {
//            Intent i = new Intent(this, ProfileActivity.class);
//            i.putExtra("user", user);
//            startActivity(i);
//            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showCompose(View view) {
        Intent i = new Intent(TopicsActivity.this, ComposeActivity.class);
        i.putExtra("targetId", "");
        startActivity(i);
    }

    public class TopicsPagerAdapter extends FragmentPagerAdapter {
        private String tabItems[] = {"Hot Topics", "New Topics"};

        public TopicsPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);

            fHotTopics = new HotTopicsFragment();
            fNewTopics = new NewTopicsFragment();
        }

        @Override
        public int getCount() {
            return tabItems.length;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return fHotTopics;
            } else if (position == 1) {
                return fNewTopics;
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
