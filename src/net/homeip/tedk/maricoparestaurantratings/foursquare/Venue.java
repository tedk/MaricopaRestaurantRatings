package net.homeip.tedk.maricoparestaurantratings.foursquare;

import org.json.JSONException;
import org.json.JSONObject;

public class Venue {
    
    private String id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    
    @Override
    public boolean equals(Object o) {
	if(o instanceof Venue)
	{
	   Venue v = (Venue) o;
	   if(this.id == null || v.id == null) {
	       return false;
	   }
	   return this.id.equals(v.id);
	}
	else
	{
	    return false;
	}
    }
    
    public Venue(JSONObject venue) throws JSONException {
	this.id = venue.getString("id");
	this.name = venue.getString("name");
	JSONObject location = venue.getJSONObject("location");
	if(location.has("address")) {
	    this.address = location.getString("address");
	}
	if(location.has("city")) {
	    this.city = location.getString("city");
	}
	if(location.has("state")) {
	    this.state = location.getString("state");
	}
	if(location.has("country")) {
	    this.country = location.getString("country");
	}
	if(location.has("postalCode")) {
	    this.postalCode = location.getString("postalCode");
	}
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getPostalCode() {
        return postalCode;
    }

}
