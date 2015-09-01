package com.yahoo.apps.thirty.fragments.post;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yahoo.apps.thirty.R;
import com.yahoo.apps.thirty.activities.PostsActivity;
import com.yahoo.apps.thirty.adapters.TopicsArrayAdapter;
import com.yahoo.apps.thirty.backbone.TumblrApplication;
import com.yahoo.apps.thirty.backbone.TumblrClient;
import com.yahoo.apps.thirty.models.Post;

import java.util.ArrayList;

public abstract class PostsListFragment extends Fragment {
    protected TumblrClient tumblrClient;
    protected ArrayList<Post> posts;
    protected TopicsArrayAdapter topicsArrayAdapter;
    protected SwipeRefreshLayout swipeContainer;
    protected String topicId;
    private PostsActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        posts = new ArrayList<>();
        topicsArrayAdapter = new TopicsArrayAdapter(getActivity(), posts);
        tumblrClient = TumblrApplication.getTumblrClient();

        activity = (PostsActivity) getActivity();
        topicId = activity.getTopicId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_posts_list, container, false);

        ListView lvPosts = (ListView) v.findViewById(R.id.lvPosts);
        lvPosts.setAdapter(topicsArrayAdapter);
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

        lvPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String targetId = posts.get(position).getUid();
                activity.showPostDialog(targetId);
            }
        });

        loadTopicsList();
        return v;
    }

    public void add(Post post) {
        topicsArrayAdapter.add(post);
    }

    public void add(Integer position, Post post) {
        posts.add(position, post);
        topicsArrayAdapter.notifyDataSetChanged();
    }

    abstract void loadTopicsList();
}
