package com.yahoo.apps.thirty.activities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yahoo.apps.thirty.R;
import com.yahoo.apps.thirty.backbone.TumblrApplication;
import com.yahoo.apps.thirty.backbone.TumblrClient;
import com.yahoo.apps.thirty.fragments.post.HotPostsFragment;
import com.yahoo.apps.thirty.fragments.post.NewPostsFragment;

import org.apache.http.Header;
import org.json.JSONObject;

public class PostsActivity extends ActionBarActivity implements StatusDialog.StatusDialogListener {
    private HotPostsFragment fHotPosts;
    private NewPostsFragment fNewPosts;
    private TumblrClient tumblrClient;
    private String topicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        tumblrClient = TumblrApplication.getTumblrClient();

        if (savedInstanceState == null) {
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
            PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
            tabStrip.setViewPager(viewPager);

            topicId = getIntent().getStringExtra("topicId");
            Log.i("ERROR", topicId);
        }
    }

    public void postStatus(String targetId, String content) {
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
        getMenuInflater().inflate(R.menu.menu_posts, menu);
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
            showPostDialog(topicId);
            return true;
//        } else if (id == R.id.action_profile) {
//            Intent i = new Intent(this, ProfileActivity.class);
//            i.putExtra("user", user);
//            startActivity(i);
//            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showPostDialog(String targetId) {
        FragmentManager fm = getFragmentManager();
        StatusDialog filterDialog = StatusDialog.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("targetId", targetId);
        filterDialog.setArguments(bundle);
        filterDialog.show(fm, "fragment_filter");
    }

    @Override
    public void onFinishStatusDialog(String targetId, String status) {
        postStatus(targetId, status);
    }

    public String getTopicId() {
        return topicId;
    }

    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        private String tabItems[] = {"Hot", "New"};

        public TweetsPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);

            fHotPosts = new HotPostsFragment();
            fNewPosts = new NewPostsFragment();
        }

        @Override
        public int getCount() {
            return tabItems.length;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return fHotPosts;
            } else if (position == 1) {
                return fNewPosts;
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
