package com.filip.versu.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.filip.versu.model.view.UserProfileFeedViewModel;
import com.filip.versu.service.impl.UserSession;
import com.filip.versu.view.adapter.AbsBaseEntityRecyclerViewAdapter;
import com.filip.versu.view.adapter.MyProfileRecyclerViewAdapter;
import com.filip.versu.view.viewmodel.MyProfileViewModel;
import com.filip.versu.view.viewmodel.callback.IMyProfileViewModel.IMyProfileViewModelCallback;

public class MyProfileFragment
        extends AbsPostsFeedFragment<IMyProfileViewModelCallback, MyProfileViewModel, UserProfileFeedViewModel>
        implements IMyProfileViewModelCallback {

    /**
     *
     * @return
     */
    public static MyProfileFragment newInstance() {
        MyProfileFragment myProfileFragment = new MyProfileFragment();

        return myProfileFragment;
    }

    @Override
    public AbsBaseEntityRecyclerViewAdapter createRecyclerViewAdapter() {
        UserProfileFeedViewModel userProfileFeedViewModel = new UserProfileFeedViewModel();
        UserProfileFeedViewModel.UserCardViewModel userCardViewModel = new UserProfileFeedViewModel.UserCardViewModel();
        userCardViewModel.userDTO = UserSession.instance().getLogedInUser();
        userProfileFeedViewModel.userCard = userCardViewModel;
        return new MyProfileRecyclerViewAdapter(userProfileFeedViewModel, getActivity().getApplicationContext(), getViewModel());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViewModel().requestItemsFromInternalStorage(getActivity().getApplicationContext());
    }


    @Override
    public void addLoadedItemsToViews(UserProfileFeedViewModel item) {
        MyProfileRecyclerViewAdapter adapter = (MyProfileRecyclerViewAdapter) recyclerViewAdapter;
        adapter.setContent(item);
    }

    @Override
    public void setModelView() {
        setModelView(this);
    }

    @Nullable
    @Override
    public Class<MyProfileViewModel> getViewModelClass() {
        return MyProfileViewModel.class;
    }

}
