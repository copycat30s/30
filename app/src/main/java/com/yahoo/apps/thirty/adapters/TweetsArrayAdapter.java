package com.yahoo.apps.thirty.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yahoo.apps.thirty.R;
import com.yahoo.apps.thirty.common.TwitterHelper;
import com.yahoo.apps.thirty.models.Tweet;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jackylee on 8/10/15.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {
    public TweetsArrayAdapter(Context context, ArrayList<Tweet> tweets) {
        super(context, 0, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Tweet tweet = getItem(position);

        // check if we're using recycled view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tweet_item, parent, false);
        }

        ImageView ivProfile = (ImageView) convertView.findViewById(R.id.ivProfile);
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
        TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
        RelativeTimeTextView tvRelativeTime = (RelativeTimeTextView) convertView.findViewById(R.id.tvRelativeTime);


        tvName.setText(tweet.getUser().getName());
        tvScreenName.setText("@" + tweet.getUser().getScreenName());
        tvBody.setText(tweet.getBody());
        tvRelativeTime.setReferenceTime(TwitterHelper.getTwitterDate(tweet.getCreatedAt()).getTime());

        ivProfile.setImageResource(0);
        Picasso.with(getContext())
                .load(tweet.getUser().getProfileImgUrl())
                .placeholder(R.drawable.placeholder_img)
                .into(ivProfile);

        return convertView;
    }
}
