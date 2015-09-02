package com.yahoo.apps.thirty.backbone;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yahoo.apps.thirty.models.AsyncResult;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.TumblrApi;

import java.io.UnsupportedEncodingException;

public class TumblrClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TumblrApi.class;
	public static final String REST_URL = "https://api.tumblr.com/v2";
	public static final String REST_CONSUMER_KEY = "P3H5ykAipCbRs6Dhxyo38ECo0ZlpC8mgUreeWIUU7vkKY1q1i0";
	public static final String REST_CONSUMER_SECRET = "wtrgiIR2Y2N4hwM0WO2hn9jlxoa0Cyhf2OhRQO6eRVi8I49IEi";
	public static final String REST_CALLBACK_URL = "oauth://tumblrclient";
	public static final String THIRTY_WEB_API_URL = "http://10.101.136.164:8080";

	public TumblrClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

    // Get single post content for given post ID
    private void getPostFromId(String id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("blog/thirtyapp.tumblr.com/posts");
        RequestParams params = new RequestParams();
        params.put("id", id);
        client.get(apiUrl, params, handler);
    }

    // Gets ID list from Thirty API first, and get corresponding post contents from Tumblr
    private void getPostsFromThirtyAPI(String url, final JsonHttpResponseHandler handler) {
        AsyncHttpClient thirtyClient = new AsyncHttpClient();

        thirtyClient.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                try {
                    final JSONArray results = jsonObject.getJSONObject("_embedded").getJSONArray("rh:doc");
                    final AsyncResult asyncResult = new AsyncResult();
                    for (int i = 0, n = results.length(); i < n; i++) {
                        final int j = i;
                        try {
                            final JSONObject result = results.getJSONObject(i);
                            String id = result.getString("_id");
                            getPostFromId(id, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                                    try {
                                        JSONObject result = json.getJSONObject("response").getJSONArray("posts").getJSONObject(0);
                                        asyncResult.addResult(j, result);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if (asyncResult.getCount() == results.length()) {
                                        handler.onSuccess(statusCode, headers, asyncResult.getResults());
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                                    Log.i("ERROR", response.toString());
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            continue;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Log.i("ERROR", response.toString());
            }
        });
    }

    // Get Hot Posts in a Topic
    public void getHotPosts(String topicId, JsonHttpResponseHandler handler) {
        String url = THIRTY_WEB_API_URL + "/30/posts?sort_by=-heat&filter=%7B%22topic%22:%22" + topicId + "%22%7D";
        getPostsFromThirtyAPI(url, handler);
    }

    // Get New Posts in a Topic
    public void getNewPosts(String topicId, JsonHttpResponseHandler handler) {
        String url = THIRTY_WEB_API_URL + "/30/posts?sort_by=-timestamp&filter=%7B%22topic%22:%22" + topicId + "%22%7D";
        getPostsFromThirtyAPI(url, handler);
    }

	// Get Hot Topics
	public void getHotTopics(JsonHttpResponseHandler handler) {
        String url = THIRTY_WEB_API_URL + "/30/topics?sort_by=-heat";
        getPostsFromThirtyAPI(url, handler);
	}

    // Get New Topics
    public void getNewTopics(JsonHttpResponseHandler handler) {
        String url = THIRTY_WEB_API_URL + "/30/topics?sort_by=-timestamp";
        getPostsFromThirtyAPI(url, handler);
    }

	// Post Status Update
	public void postStatus(String content, final String pid, final AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("blog/thirtyapp.tumblr.com/post");
		RequestParams params = new RequestParams();
		params.put("type", "text");
		params.put("title", "Thirty Blog");
		params.put("body", content);
		client.post(apiUrl, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
				Log.i("ERROR", json.toString());
				try {
					String id = json.getJSONObject("response").getString("id");
					String url = THIRTY_WEB_API_URL + "/30/posts/" + id;
					AsyncHttpClient thirtyClient = new AsyncHttpClient();
					JSONObject payload = new JSONObject();
					payload.put("timestamp", System.currentTimeMillis() / 1000L);
                    if (pid != "") {
                        payload.put("parent", pid);
                    }

					try {
						StringEntity stringEntity = new StringEntity(payload.toString());
						stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
						thirtyClient.put(context, url, stringEntity, "application/json", handler);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						handler.onFailure(statusCode, headers, null, e);
					}

				} catch (JSONException e) {
					handler.onFailure(statusCode, headers, null, e);
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                handler.onFailure(statusCode, headers, null, throwable);
			}
		});
	}
}