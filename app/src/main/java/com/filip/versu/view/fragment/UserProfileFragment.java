package com.filip.versu.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.filip.versu.model.dto.FollowingDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.model.view.UserProfileFeedViewModel;
import com.filip.versu.model.view.UserProfileFeedViewModel.UserCardViewModel;
import com.filip.versu.view.adapter.AbsBaseEntityRecyclerViewAdapter;
import com.filip.versu.view.adapter.UserProfileRecyclerViewAdapter;
import com.filip.versu.view.viewmodel.UserProfileViewModel;
import com.filip.versu.view.viewmodel.callback.IUserProfileViewModel;
import com.filip.versu.view.viewmodel.callback.IUserProfileViewModel.IUserProfileViewModelCallback;


public class UserProfileFragment
        extends AbsPostsTimelineFeedFragment<UserProfileFeedViewModel, UserProfileViewModel, IUserProfileViewModelCallback>
        implements IUserProfileViewModel.IUserProfileViewModelCallback {

    public static final String TAG = UserProfileFragment.class.getSimpleName();

    public static final String USER_KEY = "USER_KEY";
    private UserDTO user;

    public static UserProfileFragment newInstance(UserDTO userDTO) {
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER_KEY, userDTO);
        userProfileFragment.setArguments(bundle);
        return userProfileFragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        user = (UserDTO) getArguments().getSerializable(USER_KEY);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public AbsBaseEntityRecyclerViewAdapter createRecyclerViewAdapter() {
        UserProfileFeedViewModel userProfileFeedViewModel = new UserProfileFeedViewModel();
        UserCardViewModel userCardViewModel = new UserCardViewModel();
        userCardViewModel.userDTO = user;
        userProfileFeedViewModel.userCard = userCardViewModel;
        return new UserProfileRecyclerViewAdapter(userProfileFeedViewModel, getActivity().getApplicationContext(), getViewModel(), getActivity());
    }

    @Override
    public void addLoadedItemsToViews(UserProfileFeedViewModel item) {
        UserProfileRecyclerViewAdapter adapter = (UserProfileRecyclerViewAdapter) recyclerViewAdapter;
        adapter.setContent(item);
    }

    @Override
    public void clearView() {
        super.clearView();
    }

    @Override
    public void setModelView() {
        setModelView(this);
        getViewModel().setDependencies(user);
    }

    @Nullable
    @Override
    public Class<UserProfileViewModel> getViewModelClass() {
        return UserProfileViewModel.class;
    }

    @Override
    public void addItemToViewAdapter(FollowingDTO item) {
        recyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void addItemToViewAdapter(FollowingDTO item, int position) {
        addItemToViewAdapter(item);
    }

    @Override
    public void removeItemFromViewAdapter(FollowingDTO item) {
        recyclerViewAdapter.notifyDataSetChanged();
    }
}
