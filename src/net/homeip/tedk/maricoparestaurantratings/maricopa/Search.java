package net.homeip.tedk.maricoparestaurantratings.maricopa;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Html;

public class Search {

	public static interface Listener {
		public void onResult(List<Permit> permits);
	}

	public static void execute(Context context, final Listener listener,
			String name, String city) {
		MaricopaQueryBase mqb = new MaricopaQueryBase(context,
				new MaricopaQueryBase.Listener() {
					@Override
					public void onResult(MaricopaQueryBase.QueryResult result) {
						listener.onResult(handleResult(result));
					}
				}, "biznameSubmit");

		String cleanName = name.replaceAll("[^\\w\\s]","");
		String[] params = { "BName", cleanName, "StNum", "", "StDir", "", "StName",
				"", "City", city, "Zip", "", "inspectionType", "F", };
		mqb.execute(params);
	}

	private static List<Permit> handleResult(
			MaricopaQueryBase.QueryResult result) {

		if (result.getSize() <= 0) {
			return new ArrayList<Permit>(0);
		}

		List<Permit> permits = new ArrayList<Permit>(result.getSize());
		String html = result.getSubHtml();

		// skip the row of headers
		int startIndex = html.indexOf("</tr>") + 5;
		int endIndex;
		for (int i = 0; i < result.getSize(); ++i) {
			endIndex = html.indexOf("</tr>", startIndex) + 5;
			String row = html.substring(startIndex, endIndex);
			permits.add(parseRow(row));
			startIndex = endIndex;
		}

		return permits;

	}

	private static Permit parseRow(String row) {
		Permit p = new Permit();
		int startIndex = row.indexOf("<td");
		startIndex = row.indexOf("><a href=\"") + 10;
		int endIndex = row.indexOf("\"", startIndex);
		p.setUrl("http://www.maricopa.gov" + Html.fromHtml(row.substring(startIndex, endIndex).toString()));
		startIndex = row.indexOf(">", endIndex) + 1;
		endIndex = row.indexOf("</a>", startIndex);
		p.setId(row.substring(startIndex, endIndex).trim());
		startIndex = row.indexOf("<td", endIndex);
		startIndex = row.indexOf("<font", startIndex);
		startIndex = row.indexOf(">", startIndex) + 1;
		endIndex = row.indexOf("</font>", startIndex);
		p.setName(row.substring(startIndex, endIndex).trim());
		startIndex = row.indexOf("><a href=\"", endIndex) + 10;
		startIndex = row.indexOf(">", startIndex) + 1;
		endIndex = row.indexOf("</a>", startIndex);
		p.setAddress(row.substring(startIndex, endIndex).trim());
		startIndex = row.indexOf("<td", endIndex);
		startIndex = row.indexOf("<font", startIndex);
		startIndex = row.indexOf(">", startIndex) + 1;
		endIndex = row.indexOf("</font>", startIndex);
		p.setCity(row.substring(startIndex, endIndex).trim());
		startIndex = row.indexOf("<td", endIndex);
		startIndex = row.indexOf("<font", startIndex);
		startIndex = row.indexOf(">", startIndex) + 1;
		endIndex = row.indexOf("</font>", startIndex);
		p.setZip(row.substring(startIndex, endIndex).trim());
		return p;
	}

}
