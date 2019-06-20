package com.filip.versu.view.viewmodel.callback;


import android.content.Context;

import com.filip.versu.controller.ICreateDeleteController;
import com.filip.versu.controller.ICreateDeleteController.ICreateDeleteControllerCallback;
import com.filip.versu.model.Searchable;
import com.filip.versu.model.view.SearchHistoryFeedViewModel;
import com.filip.versu.view.fragment.SearchHistoryFragment;

public interface ISearchHistoryViewModel extends IRefreshablePageableViewModel<SearchHistoryFeedViewModel>, IDisplayingUserProfile,
                                                ICreateDeleteControllerCallback<Searchable>, ICreateDeleteController<Searchable>, IDisplayLocationProfile {

    public void setDependencies(SearchHistoryFragment.SearchHistoryType historyType);

    public void searchEntryByName(String name, Context applicationContext);

    public void displaySearchHistory(Context context);

    public void requestPostsByFeedbackPossibilities(String[] feedbackPossibilities);

    public interface ISearchHistoryViewModelCallback extends IRefreshablePageableViewCallback<SearchHistoryFeedViewModel>, IDisplayUserProfileCallback, ICreateDeleteViewCallback<Searchable>, IDisplayLocationProfileCallback {

        public void loadPostsByFeedbackPossibilities(String[] feedbackPossbilities);

    }

}
