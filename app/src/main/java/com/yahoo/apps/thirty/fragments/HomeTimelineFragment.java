package com.yahoo.apps.thirty.fragments;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeTimelineFragment extends TopicsListFragment {
    @Override
    void loadTweetsList() {
        String url = "http://192.168.33.36:8080/30/topics?sort_by=-heat";
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                try {
                    JSONArray results = jsonObject.getJSONObject("_embedded").getJSONArray("rh:doc");
                    Log.i("ERROR", results.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Log.i("ERROR", response.toString());
            }
        });
    }
}
