package net.homeip.tedk.maricoparestaurantratings;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

/**
 * Base class for http get operations
 * 
 * Parameters: 0: baseUrl; 1, 3, 5, ...: querystring name; 2, 4, 6, ...:
 * querystring value
 */
public abstract class HttpGetBase<T> extends AsyncTask<String, Void, T> {

	public static interface Listener<T> {
		public void onResult(T result);
	}

	protected abstract String getBaseUrl();

	protected abstract T handleResult(String result);

	private Context context;
	private Listener<T> listener;

	public HttpGetBase(Context context, Listener<T> listener) {
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected T doInBackground(String... params) {

		if (params == null || params.length == 0) {
			Log.e("HttpGetBase", "no action provided");
			throw new IllegalArgumentException("must provide an action");
		}
		if (params.length % 2 != 0) {
			Log.e("HttpGetBase", "wrong number of querystring parameters: "
					+ params.length);
			throw new IllegalArgumentException(
					"must provide a querystring value for every querystring name");
		}

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null || !ni.getState().equals(NetworkInfo.State.CONNECTED)) {
			Log.d("HttpGetBase", "no connection");
			return null;
		}

		InputStream is = null;
		Scanner s = null;
		T result = null;
		try {

			StringBuilder url = new StringBuilder(getBaseUrl());

			if (params.length > 1) {
				for (int i = 0; i < params.length; i += 2) {
					url.append("&");
					url.append(URLEncoder.encode(params[i], "UTF-8"));
					if (params[i + 1] != null) {
						url.append("=");
						url.append(URLEncoder.encode(params[i + 1], "UTF-8"));
					}
				}
			}
			String urlStr = url.toString();
			String cleanUrl = urlStr.contains("?") ? urlStr.substring(0,
					urlStr.indexOf("?")) : urlStr;
			Log.d("HttpGetBase", "connecting to: " + cleanUrl);
			URL serverUrl = new URL(urlStr);

			HttpURLConnection conn = (HttpURLConnection) serverUrl
					.openConnection();
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(15000);
			conn.setDoOutput(false);
			conn.setRequestMethod("GET");
			is = conn.getInputStream();
			s = new Scanner(is).useDelimiter("\\A");
			String output = s.hasNext() ? s.next() : null;

			if (output != null) {
				result = handleResult(output);
			} else {
				result = null;
				Log.d("HttpGetBase", "no response");
			}
		} catch (Exception e) {
			Log.e("HttpGetBase", "Could not get http response", e);
			if (s != null) {
				try {
					s.close();
				} catch (Exception ie) {
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (Exception ie) {
				}
			}
		}

		return result;
	}

	@Override
	protected void onPostExecute(final T result) {
		super.onPostExecute(result);
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				listener.onResult(result);
			}
		});
	}

}
