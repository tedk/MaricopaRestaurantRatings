package net.homeip.tedk.maricoparestaurantratings.activities;

import net.homeip.tedk.maricoparestaurantratings.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SearchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_search);
	
	final EditText name = (EditText) findViewById(R.id.editText1);
	final EditText city = (EditText) findViewById(R.id.editText2);
	final Button searchButton = (Button) findViewById(R.id.button1);
	
	if(getIntent().hasExtra("name"))
	{
	    name.setText(getIntent().getExtras().getString("name"));
	}
	if(getIntent().hasExtra("city"))
	{
	    city.setText(getIntent().getExtras().getString("city"));
	}
	
	searchButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		Intent i = new Intent(SearchActivity.this, PermitsActivity.class);
		i.putExtra("name", name.getText().toString());
		i.putExtra("city", city.getText().toString());
		startActivity(i);
	    }
	});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.search, menu);
	return true;
    }

}
