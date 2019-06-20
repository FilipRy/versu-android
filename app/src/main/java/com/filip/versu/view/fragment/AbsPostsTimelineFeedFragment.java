package com.filip.versu.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.view.abs.AbsPostViewModel;
import com.filip.versu.view.adapter.AbsBaseEntityRecyclerViewAdapter;
import com.filip.versu.view.adapter.PostFeedRecyclerViewAdapter;
import com.filip.versu.view.viewmodel.AbsPostsTimelineFeedViewModel;
import com.filip.versu.view.viewmodel.callback.IPostsTimelineFeedViewModel.IPostsTimelineFeedViewModelCallback;

import java.util.ArrayList;


/**
 * This is an abstract fragment used to display profile of another user(not me).
 *
 * @param <K>
 */
public abstract class AbsPostsTimelineFeedFragment<T extends AbsPostViewModel, K extends AbsPostsTimelineFeedViewModel<T, C>, C extends IPostsTimelineFeedViewModelCallback<T>>
        extends AbsPostsFeedFragment<C, K, T>
        implements IPostsTimelineFeedViewModelCallback<T> {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViewModel().requestItemsFromInternalStorage(getActivity().getApplicationContext());
    }


    @Override
    public AbsBaseEntityRecyclerViewAdapter<PostDTO> createRecyclerViewAdapter() {
        PostFeedRecyclerViewAdapter postFeedRecyclerViewAdapter = new PostFeedRecyclerViewAdapter(new ArrayList<PostDTO>(), getActivity().getApplicationContext(), getViewModel());
        return postFeedRecyclerViewAdapter;
    }


}
