package com.yahoo.apps.thirty.fragments;

import android.os.Bundle;
import android.util.Log;

import com.yahoo.apps.thirty.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by hsuanlee on 8/19/15.
 */
public class UserTimelineFragment extends TopicsListFragment {
    public static UserTimelineFragment newInstance(long uid) {
        UserTimelineFragment fUserTimeline = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putLong("uid", uid);
        fUserTimeline.setArguments(args);
        return fUserTimeline;
    }

    @Override
    void loadTweetsList() {
        long uid = 0;
        try {
            uid = getArguments().getLong("uid", 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        long max_id = Long.MAX_VALUE;

        if (tweets.size() > 0) {
            max_id = tweets.get(tweets.size() - 1).getUid() - 1;
        }
        tumblrClient.getUserTimeline(uid, max_id, new JsonHttpResponseHandler() {
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