package com.yahoo.apps.thirty.fragments;

import android.util.Log;

import com.yahoo.apps.thirty.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by hsuanlee on 8/18/15.
 */
public class MentionsTimelineFragment extends TopicsListFragment {
    @Override
    void loadTweetsList() {
        long max_id = Long.MAX_VALUE;

        if (tweets.size() > 0) {
            max_id = tweets.get(tweets.size() - 1).getUid() - 1;
        }
        tumblrClient.getMentionsTimeline(max_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                tweetsArrayAdapter.addAll(Tweet.fromJSONArray(jsonArray));
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Log.i("ERROR", response.toString());
            }
        });
    }
}
