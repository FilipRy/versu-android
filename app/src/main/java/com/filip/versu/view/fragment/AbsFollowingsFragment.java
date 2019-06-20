package com.filip.versu.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.filip.versu.R;
import com.filip.versu.model.dto.FollowingDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.model.view.FollowingsFeedViewModel;
import com.filip.versu.view.viewmodel.AbsFollowingsViewModel;
import com.filip.versu.view.viewmodel.callback.IFollowingsViewModel.IFollowingsViewModelCallback;


/**
 * This fragment displays all my following (who am I following and who is following me).
 */
public abstract class AbsFollowingsFragment<K extends AbsFollowingsViewModel<L>, L extends IFollowingsViewModelCallback>
        extends AbsRefreshablePageableFragment<L, FollowingsFeedViewModel, K>
        implements IFollowingsViewModelCallback {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void displayProfileOfUserFragment(UserDTO userDTO) {
        profileDisplayer.displayUserProfile(userDTO, getActivity());
    }


    @Override
    public void removeItemFromViewAdapter(FollowingDTO item) {
        recyclerViewAdapter.removeItem(item);
    }

    @Override
    public void addItemToViewAdapter(FollowingDTO item, int position) {
        recyclerViewAdapter.addItemToPosition(item, position);
    }

    @Override
    public void addItemToViewAdapter(FollowingDTO item) {
        recyclerViewAdapter.addItem(item);
    }
}
