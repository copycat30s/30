package com.yahoo.apps.thirty.backbone;

import android.content.Context;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest tumblrClient.
 *
 *     TumblrClient tumblrClient = TumblrApplication.getRestClient();
 *     // use tumblrClient to send requests to API
 *
 */
public class TumblrApplication extends com.activeandroid.app.Application {
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		TumblrApplication.context = this;
	}

	public static TumblrClient getTumblrClient() {
		return (TumblrClient) TumblrClient.getInstance(TumblrClient.class, TumblrApplication.context);
	}
}