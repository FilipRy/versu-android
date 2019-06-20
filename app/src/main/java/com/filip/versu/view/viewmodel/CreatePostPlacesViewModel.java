package com.filip.versu.view.viewmodel;

import android.support.v4.app.Fragment;

import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.Location;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.service.IGooglePlaceService;
import com.filip.versu.service.impl.GooglePlaceService;
import com.filip.versu.view.CreatePostActivity;
import com.filip.versu.view.viewmodel.callback.ICreatePostPlacesViewModel;

import java.util.List;

import eu.inloop.viewmodel.AbstractViewModel;


public class CreatePostPlacesViewModel
        extends AbstractViewModel<ICreatePostPlacesViewModel.ICreatePostPlacesViewModelCallback>
        implements ICreatePostPlacesViewModel {

    private PostDTO postDTO;

    private IGooglePlaceService googlePlaceService;

    @Override
    public void setDependencies(PostDTO postDTO) {
        this.postDTO = postDTO;
        Fragment fragment = (Fragment) getView();
        googlePlaceService = GooglePlaceService.createInstanceForActivity(fragment.getActivity(), CreatePostActivity.TAG);
    }

    @Override
    public boolean ignoreLocation() {
        return postDTO.location == null;
    }

    @Override
    public void setIgnoreLocation(boolean ignore) {
        if (ignore) {
            postDTO.location = null;
        } else {
            postDTO.location = new Location();
        }
    }

    @Override
    public void setPostLocation(Location location) {
        postDTO.location = location;
        if (getView() != null) {
            getView().postLocationSetCallback(location);
        }
    }

    @Override
    public void searchLocationByConstraint(String constraint) {
        if (constraint.length() < 3) {
            return;
        }
        getView().showProgress(true);
        new LocationSearch(constraint).execute();
    }

    class LocationSearch extends AbsAsynchronTask<List<Location>> {

        private String constraint;

        LocationSearch(String constraint) {
            this.constraint = constraint;
        }

        @Override
        protected List<Location> asynchronOperation() throws ServiceException {
            return googlePlaceService.findPlacesByConstraint(constraint, null, null);
        }

        @Override
        protected void onPostExecuteSuccess(List<Location> item) {
            super.onPostExecuteSuccess(item);
            if (getView() != null) {
                getView().showProgress(false);
                getView().displayFoundLocations(item);
            }

        }

        @Override
        protected void displayErrorMessage(String errorMsg) {

        }
    }

}
