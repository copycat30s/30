package com.yahoo.apps.thirty.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jackylee on 8/10/15.
 */
public class Post {
    private String body;
    private String uid;
    private String title;
    private String author;
    private long timestamp;

    public Post(JSONObject json) {
        super();

        try {
            this.body = json.getString("body");
            this.uid = json.getString("id");
            this.title = json.getString("title");
            this.author = json.getString("post_author");
            this.timestamp = json.getLong("timestamp");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Post> fromJSONArray(JSONArray jsonArray){
        ArrayList<Post> posts = new ArrayList<>();

        for (int i = 0, n = jsonArray.length(); i < n; i++) {
            try {
                Post post = new Post(jsonArray.getJSONObject(i));
                posts.add(post);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        return posts;
    }

    public String getBody() {
        return body;
    }

    public String getUid() {
        return uid;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
