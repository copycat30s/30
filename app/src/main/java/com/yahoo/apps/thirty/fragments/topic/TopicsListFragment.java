package com.yahoo.apps.thirty.fragments.topic;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yahoo.apps.thirty.R;
import com.yahoo.apps.thirty.adapters.TopicsArrayAdapter;
import com.yahoo.apps.thirty.backbone.TumblrApplication;
import com.yahoo.apps.thirty.backbone.TumblrClient;
import com.yahoo.apps.thirty.models.Post;

import java.util.ArrayList;

public abstract class TopicsListFragment extends Fragment {
    protected TumblrClient tumblrClient;
    protected ArrayList<Post> posts;
    protected TopicsArrayAdapter topicsArrayAdapter;
    protected SwipeRefreshLayout swipeContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        posts = new ArrayList<>();
        topicsArrayAdapter = new TopicsArrayAdapter(posts);
        tumblrClient = TumblrApplication.getTumblrClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_posts_list, container, false);

        RecyclerView rvPosts = (RecyclerView) v.findViewById(R.id.rvPosts);
        rvPosts.setLayoutManager(new LinearLayoutManager(rvPosts.getContext()));
        rvPosts.setAdapter(topicsArrayAdapter);
        // setup swipe container
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                posts.clear();
                loadTopicsList();
            }
        });
//        lvTweets.setOnScrollListener(new EndlessScrollListener() {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount) {
//                loadTopicsList();
//            }
//        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_purple,
                android.R.color.holo_blue_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        loadTopicsList();
        return v;
    }

//    public void add(Post post) {
//        topicsArrayAdapter.add(post);
//    }
//
//    public void add(Integer position, Post post) {
//        posts.add(position, post);
//        topicsArrayAdapter.notifyDataSetChanged();
//    }

    abstract void loadTopicsList();
}
