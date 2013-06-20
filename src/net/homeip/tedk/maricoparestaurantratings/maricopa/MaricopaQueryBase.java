package net.homeip.tedk.maricoparestaurantratings.maricopa;

import net.homeip.tedk.maricoparestaurantratings.HttpGetBase;
import android.content.Context;

public class MaricopaQueryBase extends HttpGetBase<String> {
    
    public static interface Listener extends HttpGetBase.Listener<String> {};
    
    private String navigatedFrom = null;

    public MaricopaQueryBase(Context context, Listener listener, String navigatedFrom) {
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
    protected String handleResult(String result) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
