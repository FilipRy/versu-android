package com.filip.versu.view.viewmodel;

import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.service.IUserSession;
import com.filip.versu.service.impl.UserSession;
import com.filip.versu.view.viewmodel.callback.IRegistrationViewModel;
import com.filip.versu.view.viewmodel.callback.IRegistrationViewModel.IRegistrationViewModelCallback;

import eu.inloop.viewmodel.AbstractViewModel;


public class RegistrationViewModel extends AbstractViewModel<IRegistrationViewModelCallback> implements IRegistrationViewModel {

    private IUserSession userSession = UserSession.instance();
    private String lastErrorMessage;
    private RegistrationTask registrationTask;

    @Override
    public void restoreState() {
        getView().showErrorMessage(lastErrorMessage);
        if (registrationTask != null) {
            getView().showProgress(true);
        }
    }

    @Override
    public void attemptRegister(UserDTO userDTO) {
        getView().hideErrors();
        getView().showProgress(true);
        if (registrationTask != null) {
            return;
        }
        registrationTask = new RegistrationTask(userDTO);
        registrationTask.execute((Void) null);
    }

    class RegistrationTask extends AbsAsynchronTask<UserDTO> {

        private UserDTO userDTO;

        public RegistrationTask(UserDTO userDTO) {
            this.userDTO = userDTO;
        }

        @Override
        protected UserDTO asynchronOperation() throws ServiceException {
            return userSession.attemptUserRegistration(userDTO);
        }

        @Override
        protected void onPostExecuteSuccess(UserDTO item) {
            super.onPostExecuteSuccess(item);
            lastErrorMessage = errorMsg;
            if(item != null) {
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
        protected void onPostExecute(UserDTO userDTO) {
            super.onPostExecute(userDTO);
            cleanUp();
        }

        @Override
        protected void onCancelled() {
            cleanUp();
        }

        private void cleanUp() {
            if (getView() != null) {
                getView().showProgress(false);
            }
            registrationTask = null;
        }
    }
}
