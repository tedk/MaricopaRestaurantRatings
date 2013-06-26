package net.homeip.tedk.maricoparestaurantratings.foursquare;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class Explore {

    public static interface Listener {
	public void onResult(List<Venue> result);
    }

    public static void execute(final Context context, final Listener listener,
	    final Location location) {
	ApiBase ab = new ApiBase(context, new ApiBase.Listener() {
	    @Override
	    public void onResult(JSONObject result) {
		if (result == null) {
		    listener.onResult(null);
		    return;
		}

		List<Venue> venues = new ArrayList<Venue>();
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

		Log.d("Explore", "Venues: " + venues.size());
		listener.onResult(venues);
	    }
	}, "venues/explore");

	String[] params = new String[] {
		"section", "food",
		"ll", location.getLatitude() + "," + location.getLongitude(),
		"llAcc", location.hasAccuracy() ? Float.toString(location.getAccuracy()) : "10000.0",
		"alt", location.hasAltitude() ? Double.toString(location.getAltitude()) : "0", 
	};
	ab.execute(params);
    }

}
