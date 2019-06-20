package com.filip.versu.view.viewmodel;

import android.app.Activity;
import android.content.Intent;

import com.filip.versu.R;
import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.Location;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.service.IUserService;
import com.filip.versu.service.IUserSession;
import com.filip.versu.service.impl.UserService;
import com.filip.versu.service.impl.UserSession;
import com.filip.versu.view.StartupActivity;
import com.filip.versu.view.viewmodel.callback.ISettingsViewModel;

import eu.inloop.viewmodel.AbstractViewModel;


public class SettingsViewModel extends AbstractViewModel<ISettingsViewModel.ISettingsViewModelCallback> implements ISettingsViewModel {

    private IUserSession userSession = UserSession.instance();

    private IUserService userService = UserService.instance();


    private Location locationToPersist;

    enum UPDATE_FIELD {
        email, username, location, quote
    }

    @Override
    public void logoutUser(Activity activity) {
        new UserLogoutTask(activity).execute();
    }

    @Override
    public void setLocationToPersist(Location locationToPersist) {
        this.locationToPersist = locationToPersist;
    }

    @Override
    public void updateLocation() {
        UserDTO updated = new UserDTO(userSession.getLogedInUser());
        updated.location = locationToPersist;

        if (getView() != null) {
            getView().showProgressBar(true);
        }
        new UserUpdateTask(updated, UPDATE_FIELD.location).execute();
    }

    @Override
    public void updateEmail(String newEmail) {
        UserDTO updated = new UserDTO(userSession.getLogedInUser());
        updated.email = newEmail;

        if (getView() != null) {
            getView().showProgressBar(true);
        }
        new UserUpdateTask(updated, UPDATE_FIELD.email).execute();
    }

    @Override
    public void updateUsername(String newUsername) {
        UserDTO updated = new UserDTO(userSession.getLogedInUser());
        updated.username = newUsername;

        if (getView() != null) {
            getView().showProgressBar(true);
        }
        new UserUpdateTask(updated, UPDATE_FIELD.username).execute();
    }

    @Override
    public void updateQuote(String quote) {
        UserDTO updated = new UserDTO(userSession.getLogedInUser());
        updated.quote = quote;
        if (getView() != null) {
            getView().showProgressBar(true);
        }
        new UserUpdateTask(updated, UPDATE_FIELD.quote).execute();
    }

    class UserUpdateTask extends AbsAsynchronTask<UserDTO> {

        private UserDTO userDTO;
        private UPDATE_FIELD update_field;

        public UserUpdateTask(UserDTO userDTO, UPDATE_FIELD update_field) {
            this.userDTO = userDTO;
            this.update_field = update_field;
        }

        @Override
        protected UserDTO asynchronOperation() throws ServiceException {
            return userService.update(userDTO);
        }

        @Override
        protected void onPostExecute(UserDTO userDTO) {
            super.onPostExecute(userDTO);
            if (getView() != null) {
                getView().showProgressBar(false);
            }
        }

        @Override
        protected void onPostExecuteSuccess(UserDTO item) {
            super.onPostExecuteSuccess(item);
            userSession.mergeWithLogedInUserData(item);
            if (getView() != null) {
                getView().displayMessage(R.string.updated_successfully);
                if (update_field == UPDATE_FIELD.email) {
                    getView().onUpdateEmailCallback();
                } else if (update_field == UPDATE_FIELD.username) {
                    getView().onUpdateUsernameCallback(); // TODO username (as one of JWT tokens claims) has been changed -> access token has been changed as well -> user should loggout and login again - this is not good, find a better solution
                } else if (update_field == UPDATE_FIELD.location) {
                    getView().onUpdateLocationCallback();
                } else if (update_field == UPDATE_FIELD.quote) {
                    getView().onUpdateQuoteCAllback();
                }
            }

            if(update_field == UPDATE_FIELD.location) {
                locationToPersist = null;
            }
        }

        @Override
        protected void displayErrorMessage(String errorMsg) {
            if (getView() != null) {//TODO find better solution
                if (update_field == UPDATE_FIELD.email) {
                    getView().displayMessage("Your chosen email is already associated with some account");
                } else if (update_field == UPDATE_FIELD.username) {
                    getView().displayMessage("Your chosen username is already taken");
                } else {
                    getView().displayMessage(errorMsg);
                }
            }
        }
    }

    class UserLogoutTask extends AbsAsynchronTask<Boolean> {

        private Activity activity;

        public UserLogoutTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Boolean asynchronOperation() throws ServiceException {
            return userSession.logoutUser(activity);
        }

        @Override
        protected void onPostExecuteSuccess(Boolean item) {
            super.onPostExecuteSuccess(item);
            if (getView() != null) {
                getView().displayMessage(R.string.log_out_success_message);
            }

            Intent intent = new Intent(activity, StartupActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();
        }

        @Override
        protected void displayErrorMessage(String errorMsg) {
            getView().displayMessage(errorMsg);
        }
    }

}
