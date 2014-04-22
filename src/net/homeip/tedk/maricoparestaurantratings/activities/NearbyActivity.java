package net.homeip.tedk.maricoparestaurantratings.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.homeip.tedk.maricoparestaurantratings.LocationManager;
import net.homeip.tedk.maricoparestaurantratings.R;
import net.homeip.tedk.maricoparestaurantratings.foursquare.Explore;
import net.homeip.tedk.maricoparestaurantratings.foursquare.Venue;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class NearbyActivity extends ListActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_nearby);
	
	setListAdapter(getAdapter(new ArrayList<Venue>(0)));
	
	DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {
	    @Override
	    public void onCancel(DialogInterface dialog) {
		NearbyActivity.this.finish();
	    }
	};
	
	final ProgressDialog dialog = ProgressDialog.show(NearbyActivity.this, "Searching", "Starting", true, true, cancelListener);
	
	final Explore.Listener exploreListener = new Explore.Listener() {
	    @Override
	    public void onResult(List<Venue> result) {
		if(result == null) {
		    dialog.dismiss();
		    showAlert("Could not get a list of venues");
		    return;
		}
		if(result.size() == 0) {
		    dialog.dismiss();
		    showAlert("No venues found");
		    return;
		}
		dialog.setMessage("Getting venues");
		setListAdapter(getAdapter(result));
		dialog.dismiss();
	    }
	};
	
	final LocationManager.Listener locationListener = new LocationManager.Listener() {
	    @Override
	    public void onResult(Location location) {
		if(location == null) {
		    dialog.dismiss();
		    showAlert("Could not determine your location");
		    return;
		}
		dialog.setMessage("Searching for venues");
		Explore.execute(NearbyActivity.this, exploreListener, location);
	    }
	};
	
	dialog.setMessage("Determining your location");
	LocationManager.getLocationManager(NearbyActivity.this).getLocation(NearbyActivity.this, locationListener);
    }
    
    private ListAdapter getAdapter(List<Venue> venues) {
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(venues.size());
	for(Venue v : venues) {
	    Map<String, Object> map = new HashMap<String, Object>(3);
	    map.put("venue", v);
	    map.put("name", v.getName());
	    map.put("address", v.getAddress() + ", " + v.getCity());
	    list.add(map);
	}
	return new SimpleAdapter(
		NearbyActivity.this, 
		list, 
		android.R.layout.simple_list_item_2, 
		new String[] { "name", "address" }, 
		new int[] { android.R.id.text1, android.R.id.text2 });
    }
    
    private void showAlert(String message) {
	DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
	        NearbyActivity.this.finish();
	    }
	};
	new AlertDialog.Builder(NearbyActivity.this).setTitle("Error").setMessage(message).setNeutralButton("Close", clickListener).show();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Map<String, Object> map = (Map<String, Object>) getListView().getItemAtPosition(position);
        Venue venue = (Venue) map.get("venue");
        Intent i = new Intent(NearbyActivity.this, SearchActivity.class);
        i.putExtra("name", venue.getName());
        i.putExtra("city", venue.getCity());
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.nearby, menu);
	return true;
    }

}
