package net.homeip.tedk.maricoparestaurantratings;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class PlayLocationManager extends LocationManager implements
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

    private static final long ACQUISITION_TIMEOUT = 15000; // 15 secs
    private static final long LOCATION_TIMEOUT = 0; // 300000; // 5 mins

    public PlayLocationManager() {

    }

    private static volatile Location location = null;
    private static volatile PlayLocationManager locationManager = null;

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
	    locationManager = new PlayLocationManager(context, listener);
	}
    }

    @Override
    public void onConnected(Bundle dataBundle) {
	location = locationClient.getLastLocation();

	LocationRequest locationRequest = LocationRequest.create();
	locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
	locationRequest.setNumUpdates(1);
	locationRequest.setExpirationDuration(ACQUISITION_TIMEOUT);

	locationClient.requestLocationUpdates(locationRequest,
		PlayLocationManager.this);

	new Handler().postDelayed(new Runnable() {
	    @Override
	    public void run() {
		handleLocationUpdate(null, null);
	    }
	}, ACQUISITION_TIMEOUT);
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
	handleLocationUpdate(null, null);
    }

    @Override
    public void onLocationChanged(Location location) {
	if (location == null)
	    return;
	Log.d("LocationManager", "recieved location");
	handleLocationUpdate(location, "Play");
    }

    private List<Listener> listeners = new ArrayList<Listener>();
    private LocationClient locationClient = null;
    private Location lastLocation = null;

    private synchronized void addListener(Listener listener) {
	listeners.add(listener);
    }

    private synchronized void notifyListeners() {
	Log.d("LocationManager", "finished searching");

	locationClient.removeLocationUpdates(PlayLocationManager.this);
	locationClient.disconnect();

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
	if (PlayLocationManager.locationManager == null)
	    return;

	if (provider == null) {
	    Log.d("LocationManager", "timeout");
	    PlayLocationManager.locationManager = null;
	    if (PlayLocationManager.location == null) {
		// no network location
		if (lastLocation != null) {
		    PlayLocationManager.location = lastLocation;
		}
	    }
	    notifyListeners();
	} else {
	    Log.d("LocationManager", "got location");
	    PlayLocationManager.locationManager = null;
	    PlayLocationManager.location = location;
	    notifyListeners();
	}
    }

    private PlayLocationManager(final Context context, final Listener listener) {

	addListener(listener);

	locationClient = new LocationClient(context, PlayLocationManager.this,
		PlayLocationManager.this);
	locationClient.connect();

    }

}
