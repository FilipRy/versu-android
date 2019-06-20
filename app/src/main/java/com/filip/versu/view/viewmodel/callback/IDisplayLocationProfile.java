package com.filip.versu.view.viewmodel.callback;


import com.filip.versu.model.Location;

public interface IDisplayLocationProfile {

    public void requestDisplayLocationProfile(Location location);

    public interface IDisplayLocationProfileCallback {

        public void displayLocationProfile(Location location);

    }

}
