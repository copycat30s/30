package com.yahoo.apps.thirty.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jackylee on 8/10/15.
 */
public class Post {
    private String uid;
    private String title = "";
    private String author;
    private String image_url;
    private long timestamp;

    public Post(JSONObject json) {
        super();

        try {
            this.uid = json.getString("id");
            this.title = json.getString("caption");
            this.author = json.getString("post_author");
            this.timestamp = json.getLong("timestamp");
            this.image_url = json.getJSONArray("photos").getJSONObject(0).getJSONObject("original_size").getString("url");
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

    public String getImage_url() { return image_url; }
}
