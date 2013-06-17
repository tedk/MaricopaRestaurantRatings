package net.homeip.tedk.maricoparestaurantratings.foursquare;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class Explore {

    public static interface Listener {
	public void onResult(String result);
    }

    public static void execute(final Context context, final Listener listener,
	    final Location location) {
	BaseGet bg = new BaseGet(context, new BaseGet.Listener() {
	    @Override
	    public void onResult(JSONObject result) {
		if (result == null) {
		    listener.onResult("no results");
		    return;
		}

		List<Venue> venues = new LinkedList<Venue>();
		try {
		    if (result.has("groups")) {
			JSONArray groups = result.getJSONArray("groups");
			for (int i = 0; i < groups.length(); ++i) {
			    JSONObject group = groups.getJSONObject(i);
			    if (group.has("items")) {
				JSONArray items = group.getJSONArray("items");
				for (int j = 0; j < items.length(); ++j) {
				    JSONObject item = items.getJSONObject(j);
				    if (item.has("venue")) {
					JSONObject jsonVenue = item
						.getJSONObject("venue");
					Venue venue = new Venue(jsonVenue);
					if (!venues.contains(venue)) {
					    venues.add(venue);
					}
				    }
				}
			    }
			}
		    }
		} catch (Exception e) {
		    Log.e("Explore", "unable to parse JSON", e);
		}

		StringBuilder sb = new StringBuilder();
		for (Venue venue : venues) {
		    sb.append(venue.getName() + "\n");
		}
		String output = sb.toString();

		Log.d("Explore", output);
		listener.onResult(output);
	    }
	});

	String[] params = new String[] {
		"venues/explore",
		"section",
		"food",
		"ll",
		location.getLatitude() + "," + location.getLongitude(),
		"llAcc",
		location.hasAccuracy() ? Float.toString(location.getAccuracy())
			: "10000.0",
		"alt",
		location.hasAltitude() ? Double
			.toString(location.getAltitude()) : "0", };
	bg.execute(params);
    }

}
