package com.filip.versu.view.viewmodel.callback;

import android.location.Location;

import eu.inloop.viewmodel.IView;


public interface IActivityViewModel {

    public void storeUserLocation(Location location);

    public interface IActivityViewModelCallback extends IView {

    }
}
