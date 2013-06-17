package net.homeip.tedk.maricoparestaurantratings.foursquare;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;

public class LocationManager {
    
    public static interface Listener {
	public void onResult(Location location);
    }
    
    public static void getLocation(final Context context, final Listener listener) {
	String locationProvider = android.location.LocationManager.GPS_PROVIDER;
	
	final android.location.LocationManager locationManager = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

	LocationListener locationListener = new LocationListener() {
	    public void onLocationChanged(final Location location) {
		locationManager.removeUpdates(this);
		new Handler().post(new Runnable() {
		    @Override
		    public void run() {
			listener.onResult(location);
		    }
		});
	    }

	    public void onStatusChanged(String provider, int status, Bundle extras) {}

	    public void onProviderEnabled(String provider) {}

	    public void onProviderDisabled(String provider) {}
	  };

	locationManager.requestLocationUpdates(locationProvider, 60000, 100, locationListener);
    }

}
