package com.yahoo.apps.thirty.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yahoo.apps.thirty.R;
import com.yahoo.apps.thirty.backbone.TumblrApplication;
import com.yahoo.apps.thirty.backbone.TumblrClient;
import com.yahoo.apps.thirty.fragments.topic.HotTopicsFragment;
import com.yahoo.apps.thirty.fragments.topic.NewTopicsFragment;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;

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

    public void showCompose(View view) {
        startRecordingVideo();
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

    private static final int VIDEO_CAPTURE = 30;
    private static final int POSTING_CONFIRM = 12;

    Uri videoUri;

    public void startRecordingVideo() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            File mediaFile = new File(
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.mp4");
            videoUri = Uri.fromFile(mediaFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
            startActivityForResult(intent, VIDEO_CAPTURE);
        } else {
            Toast.makeText(this, "No camera on device", Toast.LENGTH_LONG).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Video has been saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this, PostingActivity.class);
                intent.putExtra("blog_name", "holicy");
                intent.putExtra("targetId", "");
                startActivityForResult(intent, POSTING_CONFIRM);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.",  Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video",  Toast.LENGTH_LONG).show();
            }
        }
    }
}
