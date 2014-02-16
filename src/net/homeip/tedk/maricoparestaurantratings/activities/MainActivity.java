package net.homeip.tedk.maricoparestaurantratings.activities;

import net.homeip.tedk.maricoparestaurantratings.R;
import net.homeip.tedk.maricoparestaurantratings.Version;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Version.Init(getApplicationContext());
		
		final Button nearbyButton = (Button) findViewById(R.id.button1);
		final Button searchButton = (Button) findViewById(R.id.button2);
		
		nearbyButton.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        startActivity(new Intent(MainActivity.this, NearbyActivity.class));
		    }
		});
		
		searchButton.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        startActivity(new Intent(MainActivity.this, SearchActivity.class));
		    }
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
