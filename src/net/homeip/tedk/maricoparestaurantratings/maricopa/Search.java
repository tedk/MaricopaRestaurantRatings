package net.homeip.tedk.maricoparestaurantratings.maricopa;

import android.content.Context;

public class Search {
    
    public static interface Listener {
	public void onResult(boolean result);
    }
    
    public static void execute(Context context, final Listener listener, String name, String zip) {
	MaricopaQueryBase mqb = new MaricopaQueryBase(context, new MaricopaQueryBase.Listener() {
	    @Override
	    public void onResult(String result) {
	        listener.onResult(true);
	    }
	}, "biznameSubmit");
	
	String[] params = {
		"BName", name,
		"StNum", "",
		"StDir", "",
		"StName", "",
		"City", "",
		"Zip", zip,
		"inspectionType", "F",
	};
	mqb.execute(params);
    }
    
}
