package com.yahoo.apps.thirty.fragments.topic;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.yahoo.apps.thirty.models.Post;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class HotTopicsFragment extends TopicsListFragment {
    @Override
    void loadTopicsList() {
        tumblrClient.getHotTopics(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                posts.addAll(Post.fromJSONArray(jsonArray));
                topicsArrayAdapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Log.i("ERROR", response.toString());
            }
        });
    }
}
