package net.homeip.tedk.maricoparestaurantratings;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class AospLocationManager extends LocationManager {

	private static final long ACQUISITION_TIMEOUT = 15000; // 15 secs
	private static final long LOCATION_TIMEOUT = 0; // 300000; // 5 mins

	private static final String GPS_PROVIDER = android.location.LocationManager.GPS_PROVIDER;
	private static final String NETWORK_PROVIDER = android.location.LocationManager.NETWORK_PROVIDER;

	private class LocationListener implements android.location.LocationListener {

		private String provider;

		public LocationListener(String provider) {
			this.provider = provider;
			androidLocationManager.requestLocationUpdates(provider, 0, 0, this);
		}

		public void onLocationChanged(final Location location) {
			if (location == null)
				return;
			Log.d("LocationManager", "recieved location from provider "
					+ provider);
			androidLocationManager.removeUpdates(this);
			handleLocationUpdate(location, provider);
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}
	}

	private static volatile Location location = null;
	private static volatile AospLocationManager locationManager = null;
	
	public AospLocationManager() {
	    
	}

	public synchronized void getLocation(final Context context,
			final Listener listener) {
		if (location != null
				&& System.currentTimeMillis() - location.getTime() < LOCATION_TIMEOUT) {
			Log.d("LocationManager", "using cached location");
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					listener.onResult(location);
				}
			});
		} else if (locationManager != null) {
			Log.d("LocationManager", "already searching for location");
			locationManager.addListener(listener);
		} else {
			Log.d("LocationManager", "starting");
			location = null;
			locationManager = new AospLocationManager(context, listener);
		}
	}

	private List<Listener> listeners = new ArrayList<Listener>();
	private android.location.LocationManager androidLocationManager = null;
	private Location lastGpsLocation = null;
	private Location lastNetworkLocation = null;
	private LocationListener gpsListener = null;
	private LocationListener networkListener = null;

	private synchronized void addListener(Listener listener) {
		listeners.add(listener);
	}

	private synchronized void notifyListeners() {
		Log.d("LocationManager", "finished searching");
		if (gpsListener != null)
			androidLocationManager.removeUpdates(gpsListener);
		if (networkListener != null)
			androidLocationManager.removeUpdates(networkListener);
		for (final Listener l : listeners) {
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					l.onResult(location);
				}
			});
		}
	}

	private synchronized void handleLocationUpdate(Location location,
			String provider) {
		// already picked a location
		if (AospLocationManager.locationManager == null)
			return;

		if (provider == null) {
			Log.d("LocationManager", "timeout");
			AospLocationManager.locationManager = null;
			if (AospLocationManager.location == null) {
				// no network location
				if (lastGpsLocation != null) {
					AospLocationManager.location = lastGpsLocation;
				} else if (lastNetworkLocation != null) {
					AospLocationManager.location = lastNetworkLocation;
				}
			}
			notifyListeners();
		} else if (provider.equals(GPS_PROVIDER)) {
			Log.d("LocationManager", "got location from gps");
			AospLocationManager.locationManager = null;
			AospLocationManager.location = location;
			notifyListeners();
		} else {
			Log.d("LocationManager", "got network location");
			if (AospLocationManager.location == null) {
				AospLocationManager.location = location;
			}
		}
	}

	private AospLocationManager(final Context context, final Listener listener) {

		addListener(listener);

		androidLocationManager = (android.location.LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		lastGpsLocation = androidLocationManager
				.getLastKnownLocation(GPS_PROVIDER);
		lastNetworkLocation = androidLocationManager
				.getLastKnownLocation(NETWORK_PROVIDER);

		if (androidLocationManager.isProviderEnabled(GPS_PROVIDER)) {
			gpsListener = new LocationListener(GPS_PROVIDER);
		}
		if (androidLocationManager.isProviderEnabled(NETWORK_PROVIDER)) {
			networkListener = new LocationListener(NETWORK_PROVIDER);
		}

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				handleLocationUpdate(null, null);
			}
		}, ACQUISITION_TIMEOUT);

	}

}
