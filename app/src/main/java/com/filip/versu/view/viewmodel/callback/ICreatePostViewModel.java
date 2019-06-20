package com.filip.versu.view.viewmodel.callback;


import com.filip.versu.model.Location;
import com.filip.versu.model.dto.PostDTO;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;

import eu.inloop.viewmodel.IView;

public interface ICreatePostViewModel {


    public PostDTO getSimplePostToCreate();

    /**
     * this method can be called as soon as the shopping item is fully initialized
     */
    void requestCreateShoppingItem();

    void incrementProgressPointer();

    void decrementProgressPointer();

    void submitCreatePostTask();

    void searchNearbyPlaces(GoogleApiClient googleApiClient);

    public interface ICreatePostViewModelCallback extends IView {

        public void showCreatePostProgress(boolean show);

        public void displayCreateMsg(String msg);

        public void displayCreateMsg(int resID);

        /**
         *
         * @param isReturning - true if this is a returning flow (going back from possibilities chooser)
         */
        public void displayPhotoSelector(boolean isReturning);

        public void displayPossibilitiesRequester();

        public void displayViewersSelector();

        public void displayPlacesSelector();

        public void displayPostDescriptionCreator();

        public void setNearbyPlaces(List<Location> nearbyPlaces);

        public List<Location> getNearbyPlaces();

        void leaveActivity();

    }

}
