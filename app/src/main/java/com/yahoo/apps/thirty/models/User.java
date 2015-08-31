package com.yahoo.apps.thirty.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by jackylee on 8/10/15.
 */
public class User implements Serializable {
    private String name;
    private long uid;
    private String screenName;
    private String profileImgUrl;
    private Integer countFollowers;
    private Integer countFriends;

    public User(JSONObject json) {
        super();

        try {
            this.name = json.getString("name");
            this.uid = json.getLong("id");
            this.screenName = json.getString("screen_name");
            this.profileImgUrl = json.getString("profile_image_url");
            this.countFollowers = json.getInt("followers_count");
            this.countFriends = json.getInt("friends_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public Integer getCountFollowers() {
        return countFollowers;
    }

    public Integer getCountFriends() {
        return countFriends;
    }
}
