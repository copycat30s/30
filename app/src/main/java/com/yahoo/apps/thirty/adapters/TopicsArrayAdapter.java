package com.yahoo.apps.thirty.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.koushikdutta.ion.Ion;
import com.yahoo.apps.thirty.R;
import com.yahoo.apps.thirty.activities.PostsActivity;
import com.yahoo.apps.thirty.models.Post;

import java.util.ArrayList;

/**
 * Created by jackylee on 8/10/15.
 */
public class TopicsArrayAdapter extends RecyclerView.Adapter<TopicsArrayAdapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public View view;
        public TextView tvTitle;
        public TextView tvAuthor;
        public ImageView ivImage;
//        public TextView tvBody;
        public RelativeTimeTextView tvRelativeTime;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            view = itemView;
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvAuthor = (TextView) itemView.findViewById(R.id.tvAuthor);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
//            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvRelativeTime = (RelativeTimeTextView) itemView.findViewById(R.id.tvRelativeTime);
        }
    }

    private ArrayList<Post> posts;

    public TopicsArrayAdapter(ArrayList<Post> posts) {
        this.posts = posts;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public TopicsArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.topic_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(TopicsArrayAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        Post post = posts.get(position);

        // Set item views based on the data model
        viewHolder.tvTitle.setText(post.getTitle());
        viewHolder.tvAuthor.setText(post.getAuthor());
//        viewHolder.tvBody.setText(Html.fromHtml(post.getBody()));
        viewHolder.tvRelativeTime.setReferenceTime(post.getTimestamp() * 1000);

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), PostsActivity.class);
                String topicId = posts.get(position).getUid();
                i.putExtra("topicId", topicId);
                view.getContext().startActivity(i);
            }
        });

        viewHolder.ivImage.setImageResource(0);
        Ion.with(viewHolder.view.getContext())
                .load(post.getImage_url())
                .withBitmap()
                .placeholder(R.drawable.placeholder_img)
                .intoImageView(viewHolder.ivImage);
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return posts.size();
    }
}
