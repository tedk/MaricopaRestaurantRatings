package net.homeip.tedk.maricoparestaurantratings.maricopa;

import android.content.Context;

public class Search {
    
    public static void execute(Context context, String name, String zip) {
	MaricopaQueryBase mqb = new MaricopaQueryBase(context, new MaricopaQueryBase.Listener() {
	    @Override
	    public void onResult(String result) {
	        // TODO Auto-generated method stub
	        
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
