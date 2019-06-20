package com.filip.versu.view.adapter;

import android.support.v4.app.FragmentActivity;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.filip.versu.model.Location;
import com.filip.versu.service.IGooglePlaceService;
import com.filip.versu.service.impl.GooglePlaceService;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

public class GooglePlaceArrayAdapter
        extends ArrayAdapter<Location> implements Filterable {


    private static final String TAG = "PlaceArrayAdapter";

    private AutocompleteFilter mPlaceFilter;
    private LatLngBounds mBounds;
    private List<Location> mResultList;


    private IGooglePlaceService googlePlaceService;


    /**
     * Constructor
     *
     * @param activity  AppCompatActivity
     * @param resource Layout resource
     * @param bounds   Used to specify the search bounds
     * @param filter   Used to specify place types
     */
    public GooglePlaceArrayAdapter(FragmentActivity activity, int resource, LatLngBounds bounds,
                                   AutocompleteFilter filter, String activityTAG) {
        super(activity, resource);
        mBounds = bounds;
        mPlaceFilter = filter;
        this.googlePlaceService = GooglePlaceService.createInstanceForActivity(activity, activityTAG);
    }

    @Override
    public int getCount() {
        return mResultList.size();
    }

    @Override
    public Location getItem(int position) {
        return mResultList.get(position);
    }


    @Override
    public Filter getFilter() {
        final Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {
                    // Query the autocomplete API for the entered constraint
                    mResultList = googlePlaceService.findPlacesByConstraint(constraint, mPlaceFilter, mBounds);
                    if (mResultList != null) {
                        // Results
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    public class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence description;

        PlaceAutocomplete(CharSequence placeId, CharSequence description) {
            this.placeId = placeId;
            this.description = description;
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }
}