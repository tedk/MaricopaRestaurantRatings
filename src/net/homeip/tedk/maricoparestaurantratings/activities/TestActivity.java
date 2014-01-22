package net.homeip.tedk.maricoparestaurantratings.activities;

import java.util.List;

import net.homeip.tedk.maricoparestaurantratings.LocationManager;
import net.homeip.tedk.maricoparestaurantratings.R;
import net.homeip.tedk.maricoparestaurantratings.Version;
import net.homeip.tedk.maricoparestaurantratings.foursquare.Explore;
import net.homeip.tedk.maricoparestaurantratings.foursquare.Venue;
import net.homeip.tedk.maricoparestaurantratings.maricopa.Permit;
import net.homeip.tedk.maricoparestaurantratings.maricopa.Search;
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
													if (result != null
															&& result.size() > 1) {
														Venue v = result.get(0);
														//showAlert(v.getName());
														Search.execute(
																appContext,
																new Search.Listener() {
																	@Override
																	public void onResult(
																			List<Permit> result) {
																		if (result
																				.size() > 0) {
																			showAlert(result
																					.get(0).url);
																		} else {
																			showAlert("no permits found");
																		}
																	}
																}, v.getName(),
																v.getCity());
													}
												}
											}, location);
								} else {
									showAlert("no location available");
								}
							}
						});
			}
		});

	}

	public void showAlert(String message) {
		new AlertDialog.Builder(TestActivity.this).setTitle("Result")
				.setMessage(message).setNeutralButton("Close", null).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}

}
