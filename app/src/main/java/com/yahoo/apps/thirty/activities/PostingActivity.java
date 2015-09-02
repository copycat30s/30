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
    String video_path, gif_path;
    String parent_id;

    private static double compression = 0.1;
    TaskSaveGIF gif_maker;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        try {
            getSupportActionBar().hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        default_blog_name = getIntent().getStringExtra("blog_name");
        parent_id = getIntent().getStringExtra("targetId");
        video_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.mp4";
        gif_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.gif";

        convertVideoToGIF();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (gif_maker.getStatus() != TaskSaveGIF.Status.FINISHED) {
            Log.i("GIF TASK", "stop converting");
            gif_maker.cancel(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_posting, menu);
        return true;
    }

    private void convertVideoToGIF()
    {
        gif_maker = new TaskSaveGIF();
        gif_maker.execute(video_path);
    }

    public void onPost (View view)
    {
        view.setEnabled(false);

        File outFile = new File(gif_path);

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
        protected File doInBackground (String... params)
        {
            File outFile = new File(gif_path);

            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile));
                bos.write(genGIF(params[0]));
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
            Log.i("GIF Task", values[0] + " / " + values[1]);

            ProgressBar pb = (ProgressBar) PostingActivity.this.findViewById(R.id.progressBar);
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
            Ion.with(PostingActivity.this).load(outFile).noCache().intoImageView(image_view);

            ProgressBar pb = (ProgressBar) PostingActivity.this.findViewById(R.id.progressBar);
            pb.setVisibility(View.INVISIBLE);

            Button post_btn = (Button) PostingActivity.this.findViewById(R.id.btPost);
            post_btn.setVisibility(View.VISIBLE);

            TextView caption_text = (TextView) PostingActivity.this.findViewById(R.id.tvCaption);
            caption_text.setVisibility(View.VISIBLE);
        }

        private byte[] genGIF (String file_name)
        {
            int max_duration = getMaxDuration(file_name);
            int frames = max_duration / 200; // 5fps

            FrameGrabber frame_grabber = new FrameGrabber();
            frame_grabber.setDataSource(file_name);
            frame_grabber.init();

            AnimatedGifEncoder gif_encoder = new AnimatedGifEncoder();
            gif_encoder.setDelay(max_duration / frames);
            gif_encoder.setRepeat(0);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            gif_encoder.start(bos);

            Bitmap bitmap, scaled_bitmap;
            for (int i = 0; i <= frames && !isCancelled(); i += 1) {
                long frame_time = 1000L * max_duration * i / frames;

                bitmap = frame_grabber.getFrameAtTime(frame_time);
                if (bitmap == null) {
                    break;
                }

                scaled_bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * compression), (int) (bitmap.getHeight() * compression), true);
                gif_encoder.addFrame(scaled_bitmap);
                bitmaps.add(scaled_bitmap);

                publishProgress(i, frames);
            }

            gif_encoder.finish();
            frame_grabber.release();

            return bos.toByteArray();
        }

        private int getMaxDuration (String file_name)
        {
            MediaMetadataRetriever ret = new MediaMetadataRetriever();
            ret.setDataSource(file_name);

            String duration = ret.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            ret.release();

            return Math.min((int) Double.parseDouble(duration), 30000);
        }

    }

}
