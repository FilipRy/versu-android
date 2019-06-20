package com.filip.versu.view.viewmodel;

import android.content.Context;
import android.os.AsyncTask;

import com.filip.versu.exception.ServiceException;
import com.filip.versu.service.IUserSession;
import com.filip.versu.service.helper.ConfigurationReader;
import com.filip.versu.service.impl.UserSession;
import com.filip.versu.view.viewmodel.callback.IEntryAppViewModel;
import com.filip.versu.view.viewmodel.callback.IEntryAppViewModel.IEntryAppViewModelCallback;

import eu.inloop.viewmodel.AbstractViewModel;


public class EntryAppViewModel extends AbstractViewModel<IEntryAppViewModelCallback> implements IEntryAppViewModel {

    private StartAppTask startAppTask;
    private Context applicationContext;

    private IUserSession userSession = UserSession.instance();


    @Override
    public void restoreState() {

    }

    @Override
    public void runStartAppTask(Context applicationContext) {
        this.applicationContext = applicationContext;

        if(startAppTask == null) {
            startAppTask = new StartAppTask();
            startAppTask.execute((Void) null);
        }

    }

    class StartAppTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            UserSession.init(applicationContext);
            ConfigurationReader.init(applicationContext);
            boolean validSession = userSession.isUserSessionValid();
            if (!validSession) {
                try {
                    userSession.logoutUser(applicationContext);
                } catch (ServiceException e) {
                }
            }
            return validSession;
        }

        @Override
        protected void onPostExecute(Boolean isSessionValid) {
            if(isSessionValid) {
                if(getView() != null) {
                    getView().continueToApp();
                }
            } else {
                if(getView() != null) {
                    getView().continueToLogin();
                }
            }
        }
    }

}
