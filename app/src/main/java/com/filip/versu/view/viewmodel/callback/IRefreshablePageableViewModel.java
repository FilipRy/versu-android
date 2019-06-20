package com.filip.versu.view.viewmodel.callback;

import android.content.Context;
import android.view.View;

import com.filip.versu.model.view.abs.AbsBaseViewModel;
import com.filip.versu.service.helper.Page;

public interface IRefreshablePageableViewModel<K extends AbsBaseViewModel> extends IPegContentViewModel {

    /**
     * This time [in minutes] specifies the maximal life time of cached items/
     */
    public static final int DISK_CACHE_MAX_LIFE_TIME = 8;
    public static final int PAGE_SIZE = 20;

    public void requestItemsFromInternalStorage(Context applicationContext);


    public boolean requestItemsFromBackend();

    /**
     *
     * @param page
     * @return true if the request was submitted, false if the request was not submitted because of e.g.: another instance of this request is already running ...
     */
    public boolean requestItemsFromBackend(Page<Long> page);




    public interface IRefreshablePageableViewCallback<K extends AbsBaseViewModel> extends IPegContentViewCallback {

        public void clearView();
        public void addLoadedItemsToViews(K items);

        public void notifyDatasetChanged();

        public void showBigProgressBar(boolean show);
        public void showProgressBarAtBottom(boolean show);
        public void showSmallProgressBar(boolean show);


        public void initializeEndlessScrolling(View parentView);

        public void showErrorMessage(String errorMsg);
        public void showErrorMessage(int stringResID);
        public void hideErrorMessage();



    }

}
