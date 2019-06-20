package com.filip.versu.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.filip.versu.R;
import com.filip.versu.model.dto.PostFeedbackPossibilityDTO;
import com.filip.versu.model.dto.PostFeedbackVoteDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.model.view.PostFeedbackFeedViewModel;
import com.filip.versu.view.adapter.AbsBaseEntityRecyclerViewAdapter;
import com.filip.versu.view.adapter.PostFeedbackRecyclerViewAdapter;
import com.filip.versu.view.viewmodel.PostFeedbackViewModel;
import com.filip.versu.view.viewmodel.callback.IPostFeedbackViewModel;

import java.util.ArrayList;


/**
 * This fragment displays the "voters" for a feedback possibility.
 */
public class PostFeedbackFragment
        extends AbsRefreshablePageableFragment<IPostFeedbackViewModel.IPostFeedbackViewModelCallback, PostFeedbackFeedViewModel, PostFeedbackViewModel>
        implements IPostFeedbackViewModel.IPostFeedbackViewModelCallback {


    public static final String POSSIBILITY_KEY = "FEEDBACK_NAME_KEY";

    private AbsPostsFeedFragment.INotifyFeedbackActionChange notifyFeedbackActionChange;

    private PostFeedbackPossibilityDTO possibilityDTO;

    public static PostFeedbackFragment newInstance(PostFeedbackPossibilityDTO possibilityDTO) {
        PostFeedbackFragment postFeedbackFragment = new PostFeedbackFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(POSSIBILITY_KEY, possibilityDTO);
        postFeedbackFragment.setArguments(bundle);
        return postFeedbackFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_feedback, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        possibilityDTO = (PostFeedbackPossibilityDTO) getArguments().getSerializable(POSSIBILITY_KEY);
        super.onViewCreated(view, savedInstanceState);

        getViewModel().requestItemsFromInternalStorage(getActivity().getApplicationContext());

    }

    @Override
    public AbsBaseEntityRecyclerViewAdapter createRecyclerViewAdapter() {
        PostFeedbackRecyclerViewAdapter postFeedbackRecyclerViewAdapter = new PostFeedbackRecyclerViewAdapter(new ArrayList<PostFeedbackVoteDTO>(), getActivity().getApplicationContext(), getViewModel());
        return postFeedbackRecyclerViewAdapter;
    }

    @Override
    public void setModelView() {
        setModelView(this);
        getViewModel().setDependencies(possibilityDTO, notifyFeedbackActionChange);
    }

    @Override
    public void displayProfileOfUserFragment(UserDTO userDTO) {
        profileDisplayer.displayUserProfile(userDTO, getActivity());
    }

    @Nullable
    @Override
    public Class<PostFeedbackViewModel> getViewModelClass() {
        return PostFeedbackViewModel.class;
    }

    @Override
    public void setRecyclerViewAdapterCallback(AbsPostsFeedFragment.INotifyFeedbackActionChange notifyFeedbackActionChangeCallback) {
        this.notifyFeedbackActionChange = notifyFeedbackActionChangeCallback;
    }
}
