package net.homeip.tedk.maricoparestaurantratings.maricopa;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class Search {
    
    public static interface Listener {
	public void onResult(List<String> permits);
    }
    
    public static void execute(Context context, final Listener listener, String name, String city) {
	MaricopaQueryBase mqb = new MaricopaQueryBase(context, new MaricopaQueryBase.Listener() {
	    @Override
	    public void onResult(MaricopaQueryBase.QueryResult result) {
		listener.onResult(handleResult(result));
	    }
	}, "biznameSubmit");
	
	String[] params = {
		"BName", name,
		"StNum", "",
		"StDir", "",
		"StName", "",
		"City", city,
		"Zip", "",
		"inspectionType", "F",
	};
	mqb.execute(params);
    }
    
    private static List<String> handleResult(MaricopaQueryBase.QueryResult result) {
	
	if(result.getSize() <= 0) {
	    return new ArrayList<String>(0);
	}
	
	List<String> permits = new ArrayList<String>(result.getSize());
	String html = result.getSubHtml();
	
	// skip the row of headers
	int startIndex = html.indexOf("</tr>") + 5;
	int endIndex;
	for(int i = 0; i < result.getSize(); ++i) {
	    endIndex = html.indexOf("</tr>", startIndex) + 5;
	    String row = html.substring(startIndex, endIndex);
	    permits.add(parseRow(row));
	    startIndex = endIndex;
	}
	
	return permits;
	
    }
    
    private static String parseRow(String row) {
	int startIndex = row.indexOf("<td");
	int endIndex = row.indexOf("</td>", startIndex);
	String field = row.substring(startIndex, endIndex);
	startIndex = field.indexOf(">") + 1;
	startIndex = field.indexOf(">", startIndex) + 1;
	endIndex = field.indexOf("<", startIndex);
	return field.substring(startIndex, endIndex);
    }
    
}
