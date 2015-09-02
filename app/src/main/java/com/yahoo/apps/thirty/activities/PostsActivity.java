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
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.yahoo.apps.thirty.R;
import com.yahoo.apps.thirty.fragments.post.HotPostsFragment;
import com.yahoo.apps.thirty.fragments.post.NewPostsFragment;

import java.io.File;

public class PostsActivity extends ActionBarActivity {
    private HotPostsFragment fHotPosts;
    private NewPostsFragment fNewPosts;
    private String topicId;
    private String title;

    private String parentId = null;

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        if (savedInstanceState == null) {
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.setAdapter(new PostsPagerAdapter(getSupportFragmentManager()));
            PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
            tabStrip.setViewPager(viewPager);

            topicId = getIntent().getStringExtra("topicId");
            title = getIntent().getStringExtra("title");
            setTitle(Html.fromHtml(title).toString());
        }
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
            Intent i = new Intent(PostsActivity.this, ComposeActivity.class);
            i.putExtra("targetId", topicId);
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
        startRecordingVideo();
    }

    public String getTopicId() {
        return topicId;
    }

    public class PostsPagerAdapter extends FragmentPagerAdapter {
        private String tabItems[] = {"Hot Posts", "New Posts"};

        public PostsPagerAdapter(android.support.v4.app.FragmentManager fm) {
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
                if (parentId != null) {
                    intent.putExtra("targetId", parentId);
                    parentId = null;
                } else {
                    intent.putExtra("targetId", topicId);
                }
                startActivityForResult(intent, POSTING_CONFIRM);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.",  Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video",  Toast.LENGTH_LONG).show();
            }
        }
    }
}
