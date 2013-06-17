package net.homeip.tedk.maricoparestaurantratings.foursquare;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import net.homeip.tedk.maricoparestaurantratings.Version;

import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

/**
 * Base class for foursquare get operations
 * 
 * Parameters: 0: action 1, 3, 5, ...: querystring name 2, 4, 6, ...:
 * querystring value
 */
public class BaseGet extends AsyncTask<String, Void, JSONObject> {
    
    public static interface Listener {
	public void onResult(JSONObject result);
    }
    
    private Context context;
    private Listener listener;
    
    public BaseGet(Context context, Listener listener) {
	this.context = context;
	this.listener = listener;
    }

    @Override
    protected JSONObject doInBackground(String... params) {

	if (params == null || params.length == 0) {
	    Log.e("BaseGet", "no action provided");
	    throw new IllegalArgumentException("must provide an action");
	}
	if (params.length % 2 != 1) {
	    Log.e("BaseGet", "wrong number of querystring parameters: " + params.length);
	    throw new IllegalArgumentException(
		    "must provide a querystring value for every querystring name");
	}
	
	ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	NetworkInfo ni = cm.getActiveNetworkInfo();
	if (ni == null || !ni.getState().equals(NetworkInfo.State.CONNECTED))
	{
	    Log.d("BaseGet", "no connection");
	    return null;
	}

	InputStream is = null;
	Scanner s = null;
	JSONObject  jsonOutput = null;
	try {

	    StringBuilder url = new StringBuilder(
		    "https://api.foursquare.com/v2/");
	    url.append(params[0]);
	    url.append("?v=" + Version.getInstallDateString());
	    url.append("&client_id=1IVDCV2P3I32ZTV3XQKUUV3QS0OWMH1SK0ZEAVPHJYNREFDT");
	    url.append("&client_secret=BVX0CEGDZP3ZCDGLMPPZJ2XR2PUVMZATAIZ20SGVHUWJO1KM");
	    if (params.length > 1) {
		for (int i = 1; i < params.length; i += 2) {
		    url.append("&");
		    url.append(URLEncoder.encode(params[i], "UTF-8"));
		    if (params[i + 1] != null) {
			url.append("=");
			url.append(URLEncoder.encode(params[i + 1], "UTF-8"));
		    }
		}
	    }
	    String urlStr = url.toString();
	    int start = urlStr.indexOf("&client_id=") + 11;
	    int end = urlStr.indexOf("&", start);
	    String cleanUrl = urlStr.substring(0, start) + "*" + urlStr.substring(end);
	    start = cleanUrl.indexOf("&client_secret=") + 15;
	    end = cleanUrl.indexOf("&", start);
	    cleanUrl = cleanUrl.substring(0, start) + "*" + cleanUrl.substring(end);
	    Log.d("BaseGet", "connecting to: " + cleanUrl);
	    URL serverUrl = new URL(urlStr);

	    HttpsURLConnection conn = (HttpsURLConnection) serverUrl
		    .openConnection();
	    conn.setConnectTimeout(5000);
	    conn.setReadTimeout(5000);
	    conn.setDoOutput(false);
	    conn.setRequestMethod("GET");
	    is = conn.getInputStream();
	    s = new Scanner(is).useDelimiter("\\A");
	    String output = s.hasNext() ? s.next() : null;
	    
	    if(output != null) {
		JSONObject root = new JSONObject(output);
		JSONObject meta = root.getJSONObject("meta");
		int code = meta.getInt("code");
		Log.d("BaseGet", "response code " + code);
		if(meta.has("errorType")) {
		    String errorType = meta.getString("errorType");
		    String errorDetail = meta.getString("errorDetail");
		    Log.d("BaseGet", "error: " + errorType + ": " + errorDetail);
		}
		if(root.has("response")) {
		    jsonOutput = root.getJSONObject("response");
		    Log.d("BaseGet", "response size: " + jsonOutput.length());
		}
	    } else {
		jsonOutput = null;
		Log.d("BaseGet", "no response");
	    }
	} catch (Exception e) {
	    Log.e("BaseGet", "Could not get http response", e);
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
	
	return jsonOutput;
    }
    
    @Override
    protected void onPostExecute(final JSONObject result) {
        super.onPostExecute(result);
        new Handler().post(new Runnable() {
	    @Override
	    public void run() {
	        listener.onResult(result);
	    }
	});
    }

}
