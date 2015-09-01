package com.yahoo.apps.thirty.fragments.post;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.yahoo.apps.thirty.models.Post;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewPostsFragment extends PostsListFragment {
    @Override
    void loadTopicsList() {
        tumblrClient.getNewPosts(topicId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                ArrayList<Post> posts = Post.fromJSONArray(jsonArray);
                Log.i("ERROR", posts.toString());
                topicsArrayAdapter.addAll(Post.fromJSONArray(jsonArray));
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Log.i("ERROR", response.toString());
            }
        });
    }
}
