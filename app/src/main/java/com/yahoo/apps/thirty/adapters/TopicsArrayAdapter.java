package com.yahoo.apps.thirty.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.yahoo.apps.thirty.R;
import com.yahoo.apps.thirty.models.Post;

import java.util.ArrayList;

/**
 * Created by jackylee on 8/10/15.
 */
public class TopicsArrayAdapter extends ArrayAdapter<Post> {
    public TopicsArrayAdapter(Context context, ArrayList<Post> posts) {
        super(context, 0, posts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Post post = getItem(position);

        // check if we're using recycled view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.topic_item, parent, false);
        }

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        TextView tvAuthor = (TextView) convertView.findViewById(R.id.tvAuthor);
        TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
        RelativeTimeTextView tvRelativeTime = (RelativeTimeTextView) convertView.findViewById(R.id.tvRelativeTime);


        tvTitle.setText(post.getTitle());
        tvAuthor.setText(post.getAuthor());
        tvBody.setText(Html.fromHtml(post.getBody()));
        tvRelativeTime.setReferenceTime(post.getTimestamp() * 1000);

        return convertView;
    }
}
