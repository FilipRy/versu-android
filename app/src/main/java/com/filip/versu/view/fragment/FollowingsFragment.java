package com.filip.versu.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.filip.versu.R;
import com.filip.versu.model.dto.FollowingDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.view.adapter.AbsBaseEntityRecyclerViewAdapter;
import com.filip.versu.view.adapter.FollowingsAdapter;
import com.filip.versu.view.viewmodel.FollowingsViewModel;
import com.filip.versu.view.viewmodel.callback.IMyFollowingsViewModel.IMyFollowingsViewModelCallback;

import java.util.ArrayList;


public class FollowingsFragment extends AbsFollowingsFragment<FollowingsViewModel, IMyFollowingsViewModelCallback> implements IMyFollowingsViewModelCallback {

    public static final String FOLLOWING_TYPE_KEY = "FOLLOWING_TYPE_KEY";
    public static final String USER_KEY = "USER_KEY";


    protected ImageView searchView;
    protected EditText searchEditText;


    public enum FollowingType {
        /**
         * the people, who are following the user
         */
        FOLLOWERS,
        /**
         * the people who are followed by the user
         */
        FOLLOWINGS
    }

    private FollowingType followingType;

    /**
     * The user, whose followings/FOLLOWERS are being displayed
     */
    private UserDTO userDTO;


    public static FollowingsFragment newInstance(FollowingType followingType, UserDTO userDTO) {
        FollowingsFragment followingsFragment = new FollowingsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FOLLOWING_TYPE_KEY, followingType);
        bundle.putSerializable(USER_KEY, userDTO);
        followingsFragment.setArguments(bundle);
        return followingsFragment;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        followingType = (FollowingType) getArguments().getSerializable(FOLLOWING_TYPE_KEY);
        userDTO = (UserDTO) getArguments().getSerializable(USER_KEY);

        super.onViewCreated(view, savedInstanceState);

        searchView = (ImageView) view.findViewById(R.id.imageViewLayout);
        searchEditText = (EditText) view.findViewById(R.id.editTextLayout);

        searchEditText.setHint(R.string.search_users_text_view_hint);
        searchView.setImageResource(R.mipmap.ic_search_black);

        getViewModel().requestItemsFromInternalStorage(getActivity().getApplicationContext());
    }


    @Override
    public AbsBaseEntityRecyclerViewAdapter<FollowingDTO> createRecyclerViewAdapter() {
        FollowingsAdapter followingsAdapter = new FollowingsAdapter(new ArrayList<FollowingDTO>(), userDTO, getActivity().getApplicationContext(), getViewModel(), getActivity());
        return followingsAdapter;
    }


    @Override
    public void setModelView() {
        setModelView(this);
        getViewModel().setDependencies(followingType, userDTO);
    }

    @Nullable
    @Override
    public Class<FollowingsViewModel> getViewModelClass() {
        return FollowingsViewModel.class;
    }
}
