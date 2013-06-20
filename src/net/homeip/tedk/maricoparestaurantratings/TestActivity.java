package net.homeip.tedk.maricoparestaurantratings;

import java.util.List;

import net.homeip.tedk.maricoparestaurantratings.foursquare.Explore;
import net.homeip.tedk.maricoparestaurantratings.foursquare.Venue;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_test);

	final Context appContext = getApplicationContext();
	Version.Init(getApplicationContext());

	Button buttonTest = (Button) findViewById(R.id.buttonTest);
	buttonTest.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		LocationManager.getLocation(appContext,
			new LocationManager.Listener() {
			    @Override
			    public void onResult(Location location) {
				if (location != null) {
				    Explore.execute(appContext,
					    new Explore.Listener() {
						@Override
						public void onResult(
							List<Venue> result) {
						    new AlertDialog.Builder(
							    TestActivity.this)
							    .setTitle("Result")
							    .setMessage(result.size())
							    .setNeutralButton(
								    "Close",
								    null)
							    .show();
						}
					    }, location);
				} else {
				    new AlertDialog.Builder(
					    TestActivity.this)
					    .setTitle("Result")
					    .setMessage("no location available")
					    .setNeutralButton(
						    "Close",
						    null)
					    .show();
				}
			    }
			});
	    }
	});

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.test, menu);
	return true;
    }

}
