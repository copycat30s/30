package com.yahoo.apps.thirty.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.yahoo.apps.thirty.R;
import com.yahoo.apps.thirty.backbone.TumblrApplication;
import com.yahoo.apps.thirty.backbone.TumblrClient;

import org.apache.http.Header;
import org.json.JSONObject;


public class ComposeActivity extends ActionBarActivity {
    String targetId;
    TumblrClient tumblrClient;
    EditText etContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        targetId = getIntent().getStringExtra("targetId");
        if (targetId.equals("")) {
            setTitle("New Post " + targetId);
        } else {
            setTitle("Reply to " + targetId);
        }
        tumblrClient = TumblrApplication.getTumblrClient();
        etContent = (EditText) findViewById(R.id.etContent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        return true;
    }

    public void submitPost(View v) {
        tumblrClient.postStatus(etContent.getText().toString(), targetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.i("ERROR", json.toString());
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Log.i("ERROR", response.toString());
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.i("ERROR", response);
                finish();
            }
        });
    }
}
