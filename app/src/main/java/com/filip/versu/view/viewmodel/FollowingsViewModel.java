package com.filip.versu.view.viewmodel;

import android.content.Context;

import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.view.fragment.FollowingsFragment;
import com.filip.versu.view.viewmodel.callback.IMyFollowingsViewModel;
import com.filip.versu.view.viewmodel.callback.IMyFollowingsViewModel.IMyFollowingsViewModelCallback;


public class FollowingsViewModel extends AbsFollowingsViewModel<IMyFollowingsViewModelCallback> implements IMyFollowingsViewModel {

    @Override
    public void setDependencies(FollowingsFragment.FollowingType followingsType, UserDTO userDTO) {
        this.followingsType = followingsType;
        this.userDTO = userDTO;
    }

    @Override
    public void searchFollowingByUsername(String username, Context applicationContext) {

    }


}
