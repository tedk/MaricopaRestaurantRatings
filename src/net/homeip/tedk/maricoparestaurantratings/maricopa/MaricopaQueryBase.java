package net.homeip.tedk.maricoparestaurantratings.maricopa;

import net.homeip.tedk.maricoparestaurantratings.HttpGetBase;
import android.content.Context;
import android.util.Log;

public class MaricopaQueryBase extends
		HttpGetBase<MaricopaQueryBase.QueryResult> {

	public static class QueryResult {

		private int size;
		private String subHtml;

		private QueryResult(int size, String subHtml) {
			this.size = size;
			this.subHtml = subHtml;
		}

		public int getSize() {
			return this.size;
		}

		public String getSubHtml() {
			return this.subHtml;
		}
	}

	public static interface Listener extends
			HttpGetBase.Listener<MaricopaQueryBase.QueryResult> {
	};

	private String navigatedFrom = null;

	public MaricopaQueryBase(Context context, Listener listener,
			String navigatedFrom) {
		super(context, listener);
		this.navigatedFrom = navigatedFrom;
	}

	@Override
	protected String getBaseUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append("http://www.maricopa.gov/envsvc/envwebapp/tabs/results.aspx");
		sb.append("?navigatedFrom=" + navigatedFrom);
		return sb.toString();
	}

	@Override
	protected MaricopaQueryBase.QueryResult handleResult(String result) {
		int startIndex = result
				.indexOf("<span id=\"displayGridStyle_LabelNumrows\"");
		startIndex = result.indexOf("Total Records Retrieved", startIndex);
		startIndex = result.indexOf("[", startIndex) + 1;
		int endIndex = result.indexOf("]", startIndex);
		String numResultsStr = result.substring(startIndex, endIndex);
		startIndex = result.indexOf("<table", endIndex);
		endIndex = result.indexOf("</table>", startIndex) + 8;
		String html = result.substring(startIndex, endIndex);
		int numResults = 0;
		if (numResultsStr != null && numResultsStr.length() > 0) {
			try {
				numResults = Integer.parseInt(numResultsStr);
			} catch (Exception e) {
				Log.d("MaricopaQueryBase", "could not read records retrieved",
						e);
			}
		}
		return new MaricopaQueryBase.QueryResult(numResults, html);
	}

}
