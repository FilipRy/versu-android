package com.filip.versu.view.viewmodel.callback;


import com.filip.versu.model.Location;
import com.filip.versu.model.dto.PostDTO;

import java.util.List;

import eu.inloop.viewmodel.IView;

public interface ICreatePostPlacesViewModel {

    public void setDependencies(PostDTO postDTO);
    public void setPostLocation(Location location);

    public boolean ignoreLocation();

    public void setIgnoreLocation(boolean ignore);

    public void searchLocationByConstraint(String constraint);

    public interface ICreatePostPlacesViewModelCallback extends IView {

        void postLocationSetCallback(Location location);

        void displayFoundLocations(List<Location> locationList);

        void showProgress(boolean show);
    }

}
