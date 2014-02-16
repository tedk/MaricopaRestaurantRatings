package net.homeip.tedk.maricoparestaurantratings.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.homeip.tedk.maricoparestaurantratings.R;
import net.homeip.tedk.maricoparestaurantratings.maricopa.Permit;
import net.homeip.tedk.maricoparestaurantratings.maricopa.Search;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

public class PermitsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_permits);
	
	setListAdapter(getAdapter(new ArrayList<Permit>(0)));
	
	if(
		!getIntent().hasExtra("name") ||
		!getIntent().hasExtra("city") ||
		getIntent().getExtras().getString("name").isEmpty() ||
		getIntent().getExtras().getString("city").isEmpty()
		) {
	    showAlert("Must specify a name and city");
	    return;
	}
	
	String name = getIntent().getExtras().getString("name");
	String city = getIntent().getExtras().getString("city");
	
	DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {
	    @Override
	    public void onCancel(DialogInterface dialog) {
		PermitsActivity.this.finish();
	    }
	};
	
	final ProgressDialog dialog = ProgressDialog.show(PermitsActivity.this, "Searching", "Starting", true, true, cancelListener);
	
	Search.Listener searchListener = new Search.Listener() {
	    @Override
	    public void onResult(List<Permit> permits) {
		if(permits == null) {
		    dialog.dismiss();
		    showAlert("Could not get a list of permits");
		    return;
		}
		if(permits.size() == 0) {
		    dialog.dismiss();
		    showAlert("No permits found");
		    return;
		}
		dialog.setMessage("Getting permits");
		setListAdapter(getAdapter(permits));
		dialog.dismiss();
	    }
	};
	
	dialog.setMessage("Querying permits");
	Search.execute(PermitsActivity.this, searchListener, name, city);
    }
    
    private void showAlert(String message) {
	DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
	        PermitsActivity.this.finish();
	    }
	};
	new AlertDialog.Builder(PermitsActivity.this).setTitle("Error").setMessage(message).setNeutralButton("Close", clickListener).show();
    }
    
    private ListAdapter getAdapter(List<Permit> permits) {
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(permits.size());
	for(Permit p : permits) {
	    Map<String, Object> map = new HashMap<String, Object>(3);
	    map.put("permit", p);
	    map.put("name", p.getName());
	    map.put("address", p.getAddress() + ", " + p.getCity());
	    list.add(map);
	}
	return new SimpleAdapter(
		PermitsActivity.this, 
		list, 
		android.R.layout.simple_list_item_2, 
		new String[] { "name", "address" }, 
		new int[] { android.R.id.text1, android.R.id.text2 });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.permits, menu);
	return true;
    }

}
