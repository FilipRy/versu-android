package com.filip.versu.view.viewmodel.callback;

import com.filip.versu.model.dto.PostFeedbackPossibilityDTO;
import com.filip.versu.model.view.PostFeedbackFeedViewModel;
import com.filip.versu.view.fragment.AbsPostsFeedFragment;


public interface IPostFeedbackViewModel extends IRefreshablePageableViewModel<PostFeedbackFeedViewModel>, IDisplayingUserProfile {

    /**
     * This method must be called after setModelView()
     */
    public void setDependencies(PostFeedbackPossibilityDTO possibilityDTO, AbsPostsFeedFragment.INotifyFeedbackActionChange notifyFeedbackActionChange);

    public interface IPostFeedbackViewModelCallback extends IRefreshablePageableViewCallback<PostFeedbackFeedViewModel>, IDisplayUserProfileCallback {

        /**
         * Set the recycler view adapter callback, for the case that PostFeedbackFragment is displayed over the AbsPostsFeedFragment.
         * @param notifyFeedbackActionChangeCallback
         */
        public void setRecyclerViewAdapterCallback(AbsPostsFeedFragment.INotifyFeedbackActionChange notifyFeedbackActionChangeCallback);

    }

}
