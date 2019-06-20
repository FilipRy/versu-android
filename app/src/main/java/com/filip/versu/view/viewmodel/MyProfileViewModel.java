package com.filip.versu.view.viewmodel;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.filip.versu.R;
import com.filip.versu.controller.impl.PostCreateDeleteController;
import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.UserProfileDTO;
import com.filip.versu.model.view.UserProfileFeedViewModel;
import com.filip.versu.service.cache.ILocalCacheService;
import com.filip.versu.service.factory.LocalCacheFactory;
import com.filip.versu.service.helper.Page;
import com.filip.versu.view.SettingsActivity;
import com.filip.versu.view.viewmodel.callback.IMyProfileViewModel;
import com.filip.versu.view.viewmodel.callback.IMyProfileViewModel.IMyProfileViewModelCallback;


public class MyProfileViewModel
        extends AbsPostsFeedViewModel<UserProfileFeedViewModel, IMyProfileViewModelCallback>
        implements IMyProfileViewModel {


    private PostCreateDeleteController postCreateDeleteController = new PostCreateDeleteController(this);


    @Override
    public UserProfileFeedViewModel retrieveItemsFromBackend(Page<Long> page) throws ServiceException {
        UserProfileDTO userProfileDTO = postService.getUserProfile(userSession.getLogedInUser().getId(), page);
        UserProfileFeedViewModel userProfileFeedViewModel = new UserProfileFeedViewModel(userProfileDTO);
        return userProfileFeedViewModel;
    }

    @Override
    public UserProfileFeedViewModel createInstance() {
        return new UserProfileFeedViewModel();
    }

    @Override
    public String getInternalStorageKey() {
        return MyProfileViewModel.class.getSimpleName().toLowerCase();
    }

    @Override
    public String getLastTimeRefreshInternalStorageKey() {
        return MyProfileViewModel.class.getSimpleName().toLowerCase();
    }


    @Override
    protected String getCustomNothingToShowMessage() {
        Fragment fragment = (Fragment) getView();
        if(fragment != null) {
            return fragment.getString(R.string.nothing_to_show_in_my_profile);
        }
        return "";
    }

    @Override
    public ILocalCacheService<UserProfileFeedViewModel> getLocalCache() {
        return LocalCacheFactory.createForMyProfile();
    }

    @Override
    public void requestDisplaySettings() {
        if (getView() != null) {
            Fragment fragment = (Fragment) getView();
            Activity activity = fragment.getActivity();

            Intent i = new Intent(activity, SettingsActivity.class);
            activity.startActivity(i);
        }
    }


}
