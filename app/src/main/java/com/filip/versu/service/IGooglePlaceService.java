package com.filip.versu.service;


import com.filip.versu.model.Location;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

public interface IGooglePlaceService {

    public List<Location> findPlacesByConstraint(CharSequence constraint, AutocompleteFilter mPlaceFilter, LatLngBounds mBounds);


}
