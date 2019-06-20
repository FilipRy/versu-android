package com.filip.versu.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.view.PostFeedViewModel;
import com.filip.versu.view.viewmodel.MyFeedbackActionsViewModel;
import com.filip.versu.view.viewmodel.callback.IMyFeedbackActionViewModel;
import com.filip.versu.view.viewmodel.callback.IMyFeedbackActionViewModel.IMyFeedbackActionViewModelCallback;


/**
 * This fragment is used to display the shopping items, which I liked or I marked as favourite.
 */
public class MyFeedbackActionsFragment
        extends AbsPostsTimelineFeedFragment<PostFeedViewModel, MyFeedbackActionsViewModel, IMyFeedbackActionViewModelCallback>
        implements IMyFeedbackActionViewModel.IMyFeedbackActionViewModelCallback {

    public static final String MY_FEEDBACK_ACTION_TYPE_KEY = "MY_FEEDBACK_ACTION_TYPE_KEY";

    private MyFeedbackActionsViewModel.MyFeedbackActionFragmentType myFeedbackActionFragmentType;

    public static MyFeedbackActionsFragment newInstance(MyFeedbackActionsViewModel.MyFeedbackActionFragmentType myFeedbackActionFragmentType) {
        MyFeedbackActionsFragment myFeedbackActionsFragment = new MyFeedbackActionsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MY_FEEDBACK_ACTION_TYPE_KEY, myFeedbackActionFragmentType);
        myFeedbackActionsFragment.setArguments(bundle);
        return myFeedbackActionsFragment;
    }


    public void addItemToViewAdapter(PostDTO item) {
        recyclerViewAdapter.addItem(item);
    }

    public void addItemToViewAdapter(PostDTO item, int position) {
        recyclerViewAdapter.addItemToPosition(item, position);
    }

    public void removeItemFromViewAdapter(PostDTO item) {
        recyclerViewAdapter.removeItem(item);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        myFeedbackActionFragmentType = (MyFeedbackActionsViewModel.MyFeedbackActionFragmentType) getArguments().getSerializable(MY_FEEDBACK_ACTION_TYPE_KEY);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void setModelView() {
        setModelView(this);
        getViewModel().setDependencies(myFeedbackActionFragmentType);
    }

    @Nullable
    @Override
    public Class<MyFeedbackActionsViewModel> getViewModelClass() {
        return MyFeedbackActionsViewModel.class;
    }
}
