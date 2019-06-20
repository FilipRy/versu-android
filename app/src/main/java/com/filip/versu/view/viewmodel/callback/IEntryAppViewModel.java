package com.filip.versu.view.viewmodel.callback;


import android.content.Context;

import eu.inloop.viewmodel.IView;

public interface IEntryAppViewModel extends IRestoreViewModel {

    public interface IEntryAppViewModelCallback extends IView {
        public void continueToApp();
        public void continueToLogin();
        public void continueToRegistration();
    }

    public void runStartAppTask(Context applicationContext);

}
