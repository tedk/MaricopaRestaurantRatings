package net.homeip.tedk.maricoparestaurantratings.foursquare;

import net.homeip.tedk.maricoparestaurantratings.HttpGetBase;
import net.homeip.tedk.maricoparestaurantratings.Version;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class ApiBase extends HttpGetBase<JSONObject> {

	public static interface Listener extends HttpGetBase.Listener<JSONObject> {
	};

	private String action = null;

	public ApiBase(Context context, Listener listener, String action) {
		super(context, listener);
		this.action = action;
	}

	@Override
	protected String getBaseUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append("https://api.foursquare.com/v2/");
		sb.append(action);
		sb.append("?v=" + Version.getInstallDateString());
		sb.append("&client_id=1IVDCV2P3I32ZTV3XQKUUV3QS0OWMH1SK0ZEAVPHJYNREFDT");
		sb.append("&client_secret=BVX0CEGDZP3ZCDGLMPPZJ2XR2PUVMZATAIZ20SGVHUWJO1KM");
		return sb.toString();
	}

	@Override
	protected JSONObject handleResult(String result) {
		JSONObject jsonOutput = null;
		if (result != null) {
			try {
				JSONObject root = new JSONObject(result);
				JSONObject meta = root.getJSONObject("meta");
				int code = meta.getInt("code");
				Log.d("ApiBase", "response code " + code);
				if (meta.has("errorType")) {
					String errorType = meta.getString("errorType");
					String errorDetail = meta.getString("errorDetail");
					Log.d("ApiBase", "error: " + errorType + ": " + errorDetail);
				}
				if (root.has("response")) {
					jsonOutput = root.getJSONObject("response");
					Log.d("ApiBase", "response size: " + jsonOutput.length());
				}
			} catch (Exception e) {
				Log.e("ApiBase", "could not parse JSON: " + result);
			}
		}
		return jsonOutput;
	}

}
