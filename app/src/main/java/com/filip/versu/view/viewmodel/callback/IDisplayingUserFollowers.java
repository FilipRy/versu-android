package com.filip.versu.view.viewmodel.callback;

import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.view.fragment.FollowingsFragment;


public interface IDisplayingUserFollowers {

    /**
     *
     * @param followingType - which following type should be displayed to the user
     */
    public void requestUserFollowers(UserDTO user, FollowingsFragment.FollowingType followingType);


    public interface IDisplayingUserFollowersCallback {

        /**
         * @param followingType - which following type should be displayed to the user
         */
        public void displayUserFollowers(UserDTO userDTO, FollowingsFragment.FollowingType followingType);

    }


}
