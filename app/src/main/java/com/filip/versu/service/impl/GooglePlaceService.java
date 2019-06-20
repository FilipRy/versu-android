package com.filip.versu.service.impl;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.filip.versu.model.Location;
import com.filip.versu.service.IGooglePlaceService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * This component connects to Google Place API
 */
public class GooglePlaceService implements IGooglePlaceService, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    /**
     * Here is a singleton instance for each activity, because googleAPIClient seems not to work with only one singleton per application.
     */
    private static HashMap<String, GooglePlaceService> instances = new HashMap<>();

    private static final int GOOGLE_API_CLIENT_ID = 147;
    public static final String TAG = GooglePlaceService.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;


    private GooglePlaceService() {

    }

    public static IGooglePlaceService createInstanceForActivity(FragmentActivity activity, String activityTAG) {

        GooglePlaceService existing = instances.get(activityTAG);
        if(existing != null) {
            if(!existing.mGoogleApiClient.isConnected() && !existing.mGoogleApiClient.isConnecting()) {
                existing.mGoogleApiClient.connect();
            }
            return existing;
        }

        GooglePlaceService googlePlaceService = new GooglePlaceService();
        googlePlaceService.init(activity);

        instances.put(activityTAG, googlePlaceService);

        return googlePlaceService;
    }

    private void init(FragmentActivity activity) {
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(activity, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public List<Location> findPlacesByConstraint(CharSequence constraint, AutocompleteFilter mPlaceFilter, LatLngBounds mBounds) {
        if (mGoogleApiClient.isConnected()) {
            Log.i(TAG, "Starting autocomplete query for: " + constraint);

            // Submit the query to the autocomplete API and retrieve a PendingResult that will
            // contain the results when the query completes.
            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                            .getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
                                    mBounds, mPlaceFilter);

            // This method should have been called off the main UI thread. Block and wait for at most 60s
            // for a result from the API.
            AutocompletePredictionBuffer autocompletePredictions = results
                    .await(60, TimeUnit.SECONDS);

            // Confirm that the query completed successfully, otherwise return null
            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                Log.e("", "Error getting autocomplete prediction API call: " + status.toString());
                autocompletePredictions.release();
                return null;
            }

            Log.i("", "Query completed. Received " + autocompletePredictions.getCount()
                    + " predictions.");

            // Copy the results into our own data structure, because we can't hold onto the buffer.
            // AutocompletePrediction objects encapsulate the API response (place ID and description).

            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            List<Location> resultList = new ArrayList<>(autocompletePredictions.getCount());
            while (iterator.hasNext()) {
                AutocompletePrediction prediction = iterator.next();
                // Get the details of this prediction and copy it into a new PlaceAutocomplete object.

                Location location = new Location();
                location.googleID = prediction.getPlaceId();
                location.name = prediction.getFullText(null).toString();
                location.primaryText = prediction.getPrimaryText(null).toString();
                location.secondaryText = prediction.getSecondaryText(null).toString();

                resultList.add(location);
            }

            // Release the buffer now that all data has been copied.
            autocompletePredictions.release();

            return resultList;
        }
        Log.e(TAG, "Google API client is not connected for autocomplete query.");
        return new ArrayList<>();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Google API client connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Google API client disconnected, reconnecting");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Google API client connection failed " + connectionResult.getErrorCode() + ": " + connectionResult.getErrorMessage());
    }

}
