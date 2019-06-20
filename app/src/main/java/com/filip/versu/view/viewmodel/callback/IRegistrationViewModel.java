package com.filip.versu.view.viewmodel.callback;


import com.filip.versu.model.dto.UserDTO;

import eu.inloop.viewmodel.IView;

public interface IRegistrationViewModel extends IRestoreViewModel {

    public interface IRegistrationViewModelCallback extends IView {

        public void showErrorMessage(int resId);
        public void showErrorMessage(String msg);
        public void hideErrors();

        public void showProgress(boolean show);

        public void continueToApp();

    }

    public void attemptRegister(UserDTO userDTO);

}
