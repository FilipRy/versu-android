package com.filip.versu.view.viewmodel.callback;


import com.filip.versu.controller.ICreateDeleteController;
import com.filip.versu.controller.ICreateDeleteController.ICreateDeleteControllerCallback;
import com.filip.versu.model.dto.CommentDTO;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.view.CommentsFeedViewModel;
import com.filip.versu.view.fragment.AbsPostsFeedFragment;


public interface ICommentViewModel extends IRefreshablePageableViewModel<CommentsFeedViewModel>, IDisplayingUserProfile, ICreateDeleteControllerCallback<CommentDTO>, ICreateDeleteController<CommentDTO> {

    public void setRecyclerViewAdapterCallback(AbsPostsFeedFragment.INotifyFeedbackActionChange notifyFeedbackActionChangeCallback);

    /**
     * This method must be called after setModelView()
     * @param postDTO
     */
    public void setDependencies(PostDTO postDTO);

    public void createCommentTask(String content);

    public interface ICommentViewCallback extends IRefreshablePageableViewCallback<CommentsFeedViewModel>, IDisplayUserProfileCallback, ICreateDeleteViewCallback<CommentDTO> {

        /**
         * Set the recycler view adapter callback, for the case that VotersFragment is displayed over the AbsPostsFeedFragment.
         * @param notifyFeedbackActionChangeCallback
         */
        public void setRecyclerViewAdapterCallback(AbsPostsFeedFragment.INotifyFeedbackActionChange notifyFeedbackActionChangeCallback);

        public void scrollToLastItem();

        public void hideLoadMoreBtn();

    }
}
