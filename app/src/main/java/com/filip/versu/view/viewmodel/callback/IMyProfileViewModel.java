package com.filip.versu.view.viewmodel.callback;


import com.filip.versu.model.view.UserProfileFeedViewModel;

public interface IMyProfileViewModel extends IPostsFeedViewModel<UserProfileFeedViewModel> {

    public void requestDisplaySettings();


    public interface IMyProfileViewModelCallback extends IPostsFeedViewCallback<UserProfileFeedViewModel> {

    }

}
