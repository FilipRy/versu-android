package com.filip.versu.view.fragment;


import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.filip.versu.model.Location;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.service.IUserSession;
import com.filip.versu.service.impl.UserSession;
import com.filip.versu.view.FollowingsActivity;
import com.filip.versu.view.PostDetailActivity;
import com.filip.versu.view.UserProfileActivity;

public class ProfileDisplayer {

    private IUserSession userSession = UserSession.instance();

    public void displayUserProfile(UserDTO userDTO, FragmentActivity fragmentActivity) {
        if(userSession.getLogedInUser().equals(userDTO)) {
            return;//not displaying the profile of myself
        }

        Intent intent = new Intent(fragmentActivity, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.USER_KEY, userDTO);

        fragmentActivity.startActivity(intent);
    }


    public void displayLocationProfile(Location location, FragmentActivity fragmentActivity) {
        Intent intent = new Intent(fragmentActivity, PostDetailActivity.class);
        intent.putExtra(PostDetailActivity.POST_FEED_TYPE_KEY, PostDetailActivity.PostFeedType.LOCATION);
        intent.putExtra(PostsTimelineFeedFragment.LOCATION_KEY, location);

        fragmentActivity.startActivity(intent);
    }


    public void displayUserFollowers(UserDTO userDTO, FollowingsFragment.FollowingType followingType, FragmentActivity fragmentActivity) {

        Intent intent = new Intent(fragmentActivity, FollowingsActivity.class);
        intent.putExtra(FollowingsActivity.USER_KEY, userDTO);
        intent.putExtra(FollowingsActivity.FOLLOWING_TYPE, followingType);

        fragmentActivity.startActivity(intent);
    }
}
