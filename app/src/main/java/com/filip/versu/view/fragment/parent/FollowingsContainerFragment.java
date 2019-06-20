package com.filip.versu.view.fragment.parent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.filip.versu.R;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.view.fragment.AbsPegContentFragment;
import com.filip.versu.view.fragment.FollowingsFragment;
import com.filip.versu.view.fragment.FollowingsFragment.FollowingType;
import com.filip.versu.view.viewmodel.callback.parent.IFollowingsContainerViewModel.IFollowingsContainerViewModelCallback;
import com.filip.versu.view.viewmodel.parent.FollowingsContainerViewModel;

import java.util.ArrayList;
import java.util.List;


public class FollowingsContainerFragment extends AbsTabsContainerFragment<IFollowingsContainerViewModelCallback, FollowingsContainerViewModel> implements IFollowingsContainerViewModelCallback {

    public static final String TAG = FollowingsContainerFragment.class.getSimpleName();

    public static final String USER_KEY = "user_key";
    public static final String FOLLOWING_TYPE_KEY = "following_type_key";

    /**
     * The user, whose FOLLOWERS/FOLLOWERS are displayed in this container.
     */
    private UserDTO userDTO;
    private FollowingType followingType;

    public static FollowingsContainerFragment newInstance(UserDTO userDTO, FollowingsFragment.FollowingType followingType) {
        FollowingsContainerFragment followingsContainerFragment = new FollowingsContainerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER_KEY, userDTO);
        bundle.putSerializable(FOLLOWING_TYPE_KEY, followingType);
        followingsContainerFragment.setArguments(bundle);
        return followingsContainerFragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        userDTO = (UserDTO) getArguments().getSerializable(USER_KEY);
        followingType = (FollowingType) getArguments().getSerializable(FOLLOWING_TYPE_KEY);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected List<AbsPegContentFragment> createFragmentsContentList() {

        List<AbsPegContentFragment> followingsFragments = new ArrayList<>();

        FollowingsFragment followersFragment = FollowingsFragment.newInstance(FollowingType.FOLLOWERS, userDTO);
        followersFragment.setTitle(getString(R.string.followers));
        followersFragment.setImageResource(R.mipmap.ic_account_circle_white_48dp);

        FollowingsFragment followingsFragment = FollowingsFragment.newInstance(FollowingType.FOLLOWINGS, userDTO);
        followingsFragment.setTitle(getString(R.string.followings));
        followingsFragment.setImageResource(R.mipmap.ic_account_circle_white_48dp);

        followingsFragments.add(followersFragment);
        followingsFragments.add(followingsFragment);

        return followingsFragments;
    }

    @Override
    protected int getViewPagerCurrentItem() {
        if(followingType == FollowingType.FOLLOWERS) {
            return 0;
        }
        return 1;
    }

    @Override
    public void setModelView() {
        setModelView(this);
    }


    @Nullable
    @Override
    public Class<FollowingsContainerViewModel> getViewModelClass() {
        return FollowingsContainerViewModel.class;
    }
}
