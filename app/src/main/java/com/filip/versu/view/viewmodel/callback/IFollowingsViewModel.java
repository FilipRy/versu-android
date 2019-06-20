package com.filip.versu.view.viewmodel.callback;

import android.content.Context;

import com.filip.versu.controller.ICreateDeleteController;
import com.filip.versu.controller.ICreateDeleteController.ICreateDeleteControllerCallback;
import com.filip.versu.model.dto.FollowingDTO;
import com.filip.versu.model.view.FollowingsFeedViewModel;


public interface IFollowingsViewModel extends IRefreshablePageableViewModel<FollowingsFeedViewModel>, IDisplayingUserProfile, ICreateDeleteController<FollowingDTO>, ICreateDeleteControllerCallback<FollowingDTO> {

    public void searchFollowingByUsername(String username, Context applicationContext);

    public interface IFollowingsViewModelCallback extends IRefreshablePageableViewCallback<FollowingsFeedViewModel>, IDisplayUserProfileCallback, ICreateDeleteViewCallback<FollowingDTO> {

    }

}
