package com.filip.versu.view.viewmodel;

import android.location.Location;

import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.service.IUserSession;
import com.filip.versu.service.impl.UserSession;
import com.filip.versu.view.viewmodel.callback.IActivityViewModel;

import eu.inloop.viewmodel.AbstractViewModel;


public class ActivityViewModel extends AbstractViewModel<IActivityViewModel.IActivityViewModelCallback> implements IActivityViewModel {


    private IUserSession userSession = UserSession.instance();

    @Override
    public void storeUserLocation(Location location) {
        UserDTO logedInUser = userSession.getLogedInUser();

        UserDTO logedInUserCopy = new UserDTO(logedInUser);

        com.filip.versu.model.Location mLocation = new com.filip.versu.model.Location();

        mLocation.longitude = location.getLongitude();
        mLocation.latitude = location.getLatitude();

        logedInUserCopy.myLocation = mLocation;

        userSession.mergeWithLogedInUserData(logedInUserCopy);
    }
}
