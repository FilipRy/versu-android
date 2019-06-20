package com.filip.versu.view.viewmodel.callback;

import android.app.Activity;

import com.filip.versu.model.Location;

import eu.inloop.viewmodel.IView;


public interface ISettingsViewModel {

    public void logoutUser(Activity activity);

    public void updateEmail(String newEmail);

    public void updateUsername(String newUsername);

    /**
     *
     * @param locationToPersist - this location is to update
     */
    public void setLocationToPersist(Location locationToPersist);

    public void updateQuote(String quote);

    /**
     * Updates location of user to @locationToPersist
     */
    public void updateLocation();

    public interface ISettingsViewModelCallback extends IView {

        public void displayMessage(String msg);
        public void displayMessage(int msgResID);

        public void onUpdateEmailCallback();
        public void onUpdateUsernameCallback();
        public void onUpdateLocationCallback();
        public void onUpdateQuoteCAllback();

        public void showProgressBar(boolean show);

    }

}
