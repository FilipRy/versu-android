package com.filip.versu.view.viewmodel.callback;


import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.view.fragment.FollowingsFragment;

public interface IMyFollowingsViewModel extends IFollowingsViewModel {

    /**
     * The setDependencies method has to be called as soon as possible always!!!
     * @param followingsType
     * @param userDTO - the user, whose FOLLOWERS/followings are being displayed
     */
    public void setDependencies(FollowingsFragment.FollowingType followingsType, UserDTO userDTO);

    public interface IMyFollowingsViewModelCallback extends IFollowingsViewModelCallback {

    }

}
