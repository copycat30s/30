package com.yahoo.apps.thirty.fragments.post;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.yahoo.apps.thirty.models.Post;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class HotPostsFragment extends PostsListFragment {
    @Override
    void loadTopicsList() {
        tumblrClient.getHotPosts(topicId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                posts.addAll(Post.fromJSONArray(jsonArray));
                postsArrayAdapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Log.i("ERROR", response.toString());
            }
        });
    }
}
