package net.homeip.tedk.maricoparestaurantratings;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.content.Context;
import android.location.Location;

public abstract class LocationManager {

    public static interface Listener {
	public void onResult(Location location);
    }

    public abstract void getLocation(Context context, Listener listener);
    
    private static volatile LocationManager locationManager = null;
    public static synchronized LocationManager getLocationManager(final Context context) {
	if(locationManager == null) {
	    if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS)
	    {
		locationManager = new PlayLocationManager();
	    } else {
		locationManager = new AospLocationManager();
	    }
	}
	return locationManager;
    }

}
