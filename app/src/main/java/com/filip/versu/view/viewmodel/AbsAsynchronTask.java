package com.filip.versu.view.viewmodel;

import android.os.AsyncTask;
import android.util.Log;

import com.filip.versu.exception.ServiceException;


public abstract class AbsAsynchronTask<T> extends AsyncTask<Void, Void, T> {

    public static final String TAG = AbsAsynchronTask.class.getSimpleName();

    protected String errorMsg;

    @Override
    protected T doInBackground(Void... params) {
        try {
            return asynchronOperation();
        } catch (ServiceException e) {
            Log.i(TAG, e.getMessage());
            errorMsg = e.getMessage();
        }
        return null;
    }

    /**
     * This method's task is to perform an asynchron operation (send request to backend, persist item to storage ...)
     * @return
     * @throws ServiceException
     */
    protected abstract T asynchronOperation() throws ServiceException;

    @Override
    protected void onPostExecute(T t) {
        super.onPostExecute(t);
        if(errorMsg == null) {
            onPostExecuteSuccess(t);
        } else {
            onPostExecuteError(t);
        }
    }


    protected void onPostExecuteSuccess(T item) {

    }

    protected void onPostExecuteError(T item) {
        displayErrorMessage(errorMsg);
    }

    protected abstract void displayErrorMessage(String errorMsg);

}
