package com.filip.versu.model;


import android.content.SharedPreferences;

import com.filip.versu.model.dto.abs.BaseDTO;
import com.google.android.gms.location.places.Place;

import java.io.Serializable;


public class Location implements Serializable, BaseDTO<Long>, Searchable {


    public static final String PREF_KEY_MY_LAT = "PREF_KEY_MY_LAT";
    public static final String PREF_KEY_MY_LONG = "PREF_KEY_MY_LONG";

    public static final String PREF_KEY_LAT = "PREF_KEY_LAT";
    public static final String PREF_KEY_LONG = "PREF_KEY_LONG";
    public static final String PREF_KEY_NAME = "PREF_KEY_NAME";
    public static final String PREF_KEY_PRIMARY_NAME = "PREF_KEY_PRIMARY_NAME";
    public static final String PREF_KEY_SECONDARY_NAME = "PREF_KEY_SEC_NAME";
    public static final String PREF_KEY_GOOGLE_ID = "PREF_KEY_GOOGLE_ID";

    public static final double LAT_UNKNOWN = -300;
    public static final double LON_UNKNOWN = -300;


    public double latitude;
    public double longitude;

    /**
     * This is an ID by google for this place.
     */
    public String googleID;

    /**
     * This is a human-readable name of the location
     */
    public String name;


    //this is used for autocomplete predictions; see https://developers.google.com/android/reference/com/google/android/gms/location/places/AutocompletePrediction.html#getPrimaryText(android.text.style.CharacterStyle)
    public String primaryText;
    public String secondaryText;



    public Location() {
        latitude = LAT_UNKNOWN;
        longitude = LON_UNKNOWN;
    }

    public Location(Place place) {
        this.latitude = place.getLatLng().latitude;
        this.longitude = place.getLatLng().longitude;
        this.googleID = place.getId();
        this.name = place.getName().toString();
        this.primaryText = place.getName().toString();
        this.secondaryText = place.getAddress().toString();
    }

    @Override
    public String getEntryName() {
        return name;
    }

    /**
     *
     * @param editor
     * @param isMyLocation true iff this is exact location of mine (the one used for nearby), false iff this is the location entered by user (city...)
     * @return
     */
    public static SharedPreferences.Editor sharedPreferencesEditorAdapter(SharedPreferences.Editor editor, Location location, boolean isMyLocation) {

        if(isMyLocation) {
            if (location == null) {
                return editor;
            }
            editor.putFloat(PREF_KEY_MY_LAT, (float) location.latitude);
            editor.putFloat(PREF_KEY_MY_LONG, (float) location.longitude);
            return editor;
        } else {
            if(location == null) {
                return editor;
            }

            editor.putString(PREF_KEY_NAME, location.name);

            if(location.primaryText != null) {
                editor.putString(PREF_KEY_PRIMARY_NAME, location.primaryText);
            }
            if(location.secondaryText != null) {
                editor.putString(PREF_KEY_SECONDARY_NAME, location.secondaryText);
            }

            editor.putString(PREF_KEY_GOOGLE_ID, location.googleID);
            editor.putFloat(PREF_KEY_LAT, (float) location.latitude);
            editor.putFloat(PREF_KEY_LONG, (float) location.longitude);
            return editor;
        }

    }

    /**
     *
     * @param sharedPreferences
     * @param isMyLocation true iff this is exact location of mine (the one used for nearby), false iff this is the location entered by user (city...)
     * @return
     */
    public static Location retrieveFromSharedPreferences(SharedPreferences sharedPreferences, boolean isMyLocation) {

        if(isMyLocation) {
            Location location = new Location();

            float lat = sharedPreferences.getFloat(PREF_KEY_MY_LAT, (float) LAT_UNKNOWN);
            float lon = sharedPreferences.getFloat(PREF_KEY_MY_LONG, (float) LON_UNKNOWN);

            location.latitude = lat;
            location.longitude = lon;

            return location;
        } else {
            Location location = new Location();

            float lat = sharedPreferences.getFloat(PREF_KEY_LAT, (float) LAT_UNKNOWN);
            float lon = sharedPreferences.getFloat(PREF_KEY_LONG, (float) LON_UNKNOWN);
            String googleID = sharedPreferences.getString(PREF_KEY_GOOGLE_ID, "");
            String name = sharedPreferences.getString(PREF_KEY_NAME, "");
            String primaryName = sharedPreferences.getString(PREF_KEY_PRIMARY_NAME, "");
            String secondaryName = sharedPreferences.getString(PREF_KEY_SECONDARY_NAME, "");

            if(lat == LAT_UNKNOWN || lon == LON_UNKNOWN) {
                return null;
            }

            location.latitude = lat;
            location.longitude = lon;
            location.name = name;
            location.primaryText = primaryName;
            location.secondaryText = secondaryName;
            location.googleID = googleID;

            return location;
        }

    }


    /**
     * Compute distance in meter.
     * @param pointA
     * @param pointB
     * @return
     */
    public static double computeDistance(Location pointA, Location pointB) {
        float lat1 = (float) pointA.latitude;
        float lng1 = (float) pointA.longitude;

        float lat2 = (float) pointB.latitude;
        float lng2 = (float) pointB.longitude;

        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    public boolean isUnknown() {
        return latitude == LAT_UNKNOWN && longitude == LON_UNKNOWN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (Double.compare(location.latitude, latitude) != 0) return false;
        if (Double.compare(location.longitude, longitude) != 0) return false;
        if (googleID != null ? !googleID.equals(location.googleID) : location.googleID != null)
            return false;
        return name != null ? name.equals(location.name) : location.name == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (googleID != null ? googleID.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long id) {

    }


    /**
     * Do not modify me!, I am used by AutocompleteTextView to display place name!
     * @return
     */
    @Override
    public String toString() {
        return name;
    }
}
