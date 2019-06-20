package com.filip.versu.view.viewmodel.callback;


import com.filip.versu.controller.IFollowingCreateDeleteController;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.model.view.UserProfileFeedViewModel;

public interface IUserProfileViewModel
        extends IPostsFeedViewModel<UserProfileFeedViewModel>,
                IFollowingCreateDeleteController,
                IFollowingCreateDeleteController.IFollowingCreateDeleteControllerCallback,
                IDisplayingUserFollowers {

    public void setDependencies(UserDTO userDTO);

    public interface IUserProfileViewModelCallback
            extends IPostsTimelineFeedViewModel.IPostsTimelineFeedViewModelCallback<UserProfileFeedViewModel>,
            IFollowingCreateDeleteController.IFollowingCreateDeleteViewCallback,
                    IDisplayingUserFollowersCallback {


    }
}
