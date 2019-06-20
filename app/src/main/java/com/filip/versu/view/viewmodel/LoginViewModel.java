package com.filip.versu.view.viewmodel;


import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.service.IUserSession;
import com.filip.versu.service.impl.UserSession;
import com.filip.versu.view.viewmodel.callback.ILoginViewModel;
import com.filip.versu.view.viewmodel.callback.ILoginViewModel.ILoginViewModelCallback;

import eu.inloop.viewmodel.AbstractViewModel;


public class LoginViewModel extends AbstractViewModel<ILoginViewModelCallback> implements ILoginViewModel {

    private IUserSession userSession = UserSession.instance();

    private LoginTask loginTask;

    private String lastErrorMessage;

    @Override
    public void restoreState() {
        if (lastErrorMessage != null) {
            getView().showErrorMessage(lastErrorMessage);
        }
        if(loginTask != null) {
            getView().showProgress(true);
        }
    }

    @Override
    public void attemptLogin(UserDTO userDTO) {
        if(getView() != null) {
            getView().hideErrors();
            getView().showProgress(true);
        }

        if(loginTask != null) {
            return;
        }

        loginTask = new LoginTask(userDTO);
        loginTask.execute((Void) null);
    }


    class LoginTask extends AbsAsynchronTask<UserDTO> {

        private UserDTO userDTO;

        public LoginTask(UserDTO userDTO) {
            this.userDTO = userDTO;
        }

        @Override
        protected UserDTO asynchronOperation() throws ServiceException {
            return userSession.attemptUserLogin(this.userDTO);
        }

        @Override
        protected void onPostExecute(UserDTO userDTO) {
            super.onPostExecute(userDTO);
            cleanUp();
        }

        @Override
        protected void onPostExecuteSuccess(UserDTO userDTO) {
            super.onPostExecuteSuccess(userDTO);
            lastErrorMessage = errorMsg;
            if(userDTO.getId() != null) {//user is already known to the app
                if(getView() != null) {
                    getView().continueToApp();
                }
            }
        }

        @Override
        protected void onPostExecuteError(UserDTO item) {
            super.onPostExecuteError(item);
            lastErrorMessage = errorMsg;
        }

        @Override
        protected void displayErrorMessage(String errorMsg) {
            if (getView() != null) {
                getView().showErrorMessage(errorMsg);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cleanUp();
        }

        private void cleanUp() {
            loginTask = null;
            if (getView() != null) {
                getView().showProgress(false);
            }
        }
    }
}
