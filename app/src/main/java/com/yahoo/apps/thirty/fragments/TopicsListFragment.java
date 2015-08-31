package com.yahoo.apps.thirty.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yahoo.apps.thirty.R;
import com.yahoo.apps.thirty.activities.ProfileActivity;
import com.yahoo.apps.thirty.adapters.TweetsArrayAdapter;
import com.yahoo.apps.thirty.backbone.TumblrClient;
import com.yahoo.apps.thirty.backbone.TumblrApplication;
import com.yahoo.apps.thirty.common.EndlessScrollListener;
import com.yahoo.apps.thirty.models.Tweet;
import com.yahoo.apps.thirty.models.User;

import java.util.ArrayList;

public abstract class TopicsListFragment extends Fragment {
    protected TumblrClient tumblrClient;
    protected ArrayList<Tweet> tweets;
    protected TweetsArrayAdapter tweetsArrayAdapter;
    protected SwipeRefreshLayout swipeContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweets = new ArrayList<>();
        tweetsArrayAdapter = new TweetsArrayAdapter(getActivity(), tweets);
        tumblrClient = TumblrApplication.getTumblrClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);

        ListView lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        lvTweets.setAdapter(tweetsArrayAdapter);
        // setup swipe container
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tweets.clear();
                loadTweetsList();
            }
        });
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadTweetsList();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_purple,
                android.R.color.holo_blue_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(view.getContext(), ProfileActivity.class);
                User user = tweets.get(position).getUser();
                i.putExtra("user", user);
                startActivity(i);
            }
        });

        loadTweetsList();
        return v;
    }

    public void add(Tweet tweet) {
        tweetsArrayAdapter.add(tweet);
    }

    public void add(Integer position, Tweet tweet) {
        tweets.add(position, tweet);
        tweetsArrayAdapter.notifyDataSetChanged();
    }

    abstract void loadTweetsList();
}
