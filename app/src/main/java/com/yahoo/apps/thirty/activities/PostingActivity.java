package com.yahoo.apps.thirty.activities;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.tam.media.FrameGrabber;
import com.yahoo.apps.thirty.R;
import com.yahoo.apps.thirty.backbone.TumblrApplication;
import com.yahoo.apps.thirty.backbone.TumblrClient;
import com.yahoo.apps.thirty.common.AnimatedGifEncoder;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class PostingActivity extends ActionBarActivity {

    String default_blog_name;
    String video_path;
    String parent_id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        default_blog_name = getIntent().getStringExtra("blog_name");
        parent_id = getIntent().getStringExtra("targetId");
        video_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.mp4";

        TaskSaveGIF gif_maker = new TaskSaveGIF();

        gif_maker.execute(video_path);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_posting, menu);
        return true;
    }

    private void uploadVideo ()
    {
        TaskSaveGIF gif_maker = new TaskSaveGIF();

        gif_maker.execute(video_path);
    }

    public void onPost (View view)
    {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File outFile = new File(extStorageDirectory, "test.gif");

        final ProgressBar pb = (ProgressBar) PostingActivity.this.findViewById(R.id.progressBar);
        Log.i("File length", String.valueOf(outFile.length()));
        //pb.setMax((int) outFile.length());
        pb.setProgress(0);
        pb.setVisibility(View.VISIBLE);
        pb.bringToFront();

        TextView caption_text = (TextView) PostingActivity.this.findViewById(R.id.tvCaption);

        TumblrApplication.getTumblrClient().postPhoto(default_blog_name, caption_text.getText().toString(), outFile.getAbsolutePath(), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("XXX", String.valueOf(statusCode));
                try {
                    Log.i("XXX", response.toString());
                    String id = response.getJSONObject("response").getString("id");

                    String url = TumblrClient.THIRTY_WEB_API_URL + "/30/posts/" + id;
                    AsyncHttpClient thirtyClient = new AsyncHttpClient();
                    JSONObject payload = new JSONObject();
                    payload.put("timestamp", System.currentTimeMillis() / 1000L);
                    if (parent_id != "") {
                        payload.put("parent", parent_id);
                    }

                    try {
                        StringEntity stringEntity = new StringEntity(payload.toString());
                        stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                        thirtyClient.put(PostingActivity.this, url, stringEntity, "application/json", new TextHttpResponseHandler() {
                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                Log.i("JACKY API XXX", throwable.getMessage());
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                Log.i("JACKY API XXX", responseString);
                            }
                        });
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        onFailure(statusCode, headers, "", e);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i("XXX", throwable.getMessage());
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                Log.i("====progress====", bytesWritten + " / " + totalSize);
                pb.setMax(totalSize);
                pb.setProgress(bytesWritten);
            }

            @Override
            public void onFinish() {
                PostingActivity.this.finish();
            }
        });
    }

    public class TaskSaveGIF extends AsyncTask<String, Integer, File> {

        List<Bitmap> bitmaps = new ArrayList<>();

        @Override
        protected File doInBackground(String... params) {
            MediaMetadataRetriever ret = new MediaMetadataRetriever();

            FrameGrabber fg = new FrameGrabber();

            ret.setDataSource(params[0]);
            fg.setDataSource(params[0]);
            fg.init();

            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File outFile = new File(extStorageDirectory, "test.gif");

            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile));
                bos.write(genGIF(fg, ret));
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return outFile;
        }

        @Override
        protected void onPreExecute() {
            ProgressBar pb = (ProgressBar) PostingActivity.this.findViewById(R.id.progressBar);
            pb.bringToFront();
        }

        @Override
        protected void onProgressUpdate (Integer... values)
        {
            ProgressBar pb = (ProgressBar) PostingActivity.this.findViewById(R.id.progressBar);

            Log.i("====progress====", values[0] + " / " + values[1]);
            pb.setMax(values[1]);
            pb.setProgress(values[0]);

            ImageView image_view = (ImageView) PostingActivity.this.findViewById(R.id.imageView);

            if (bitmaps.size() > values[0]) {
                image_view.setImageBitmap(bitmaps.get(values[0]));
            }
        }

        @Override
        protected void onPostExecute(File outFile) {
            ImageView image_view = (ImageView) PostingActivity.this.findViewById(R.id.imageView);

            image_view.setImageResource(0);
            Ion.with(PostingActivity.this).load(outFile).withBitmap().intoImageView(image_view);

            ProgressBar pb = (ProgressBar) PostingActivity.this.findViewById(R.id.progressBar);

            pb.setVisibility(View.INVISIBLE);

            Button post_btn = (Button) PostingActivity.this.findViewById(R.id.btPost);

            post_btn.setVisibility(View.VISIBLE);

            TextView caption_text = (TextView) PostingActivity.this.findViewById(R.id.tvCaption);

            caption_text.setVisibility(View.VISIBLE);
        }

        private byte[] genGIF (FrameGrabber fg, MediaMetadataRetriever ret)
        {
            String duration = ret.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long maxDur = Math.min((long)(1000 * Double.parseDouble(duration)), 30000000L);
            int frames = (int)maxDur / 200000; // 5fps

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            AnimatedGifEncoder animatedGifEncoder = new AnimatedGifEncoder();
            animatedGifEncoder.setDelay(200);
            animatedGifEncoder.setRepeat(0);

            Bitmap bmFrame, scaleBmFrame;
            animatedGifEncoder.start(bos);
            for (int i = 0; i < frames; i += 1) {
                long frameTime = maxDur * i/frames;
                bmFrame = fg.getFrameAtTime(frameTime);
                if (bmFrame == null) {
                    break;
                }
                scaleBmFrame = Bitmap.createScaledBitmap(bmFrame, (int) (bmFrame.getWidth() * 0.1), (int) (bmFrame.getHeight() * 0.1), true);
                animatedGifEncoder.addFrame(scaleBmFrame);
                bitmaps.add(scaleBmFrame);
                publishProgress(i, frames);
            }

            //last from at end
            bmFrame = fg.getFrameAtTime(maxDur);
            if (bmFrame != null) {
                scaleBmFrame = Bitmap.createScaledBitmap(bmFrame, (int) (bmFrame.getWidth() * 0.1), (int) (bmFrame.getHeight() * 0.1), true);
                bitmaps.add(scaleBmFrame);
                animatedGifEncoder.addFrame(scaleBmFrame);
            }
            publishProgress(frames, frames);

            animatedGifEncoder.finish();
            return bos.toByteArray();
        }

    }

}
