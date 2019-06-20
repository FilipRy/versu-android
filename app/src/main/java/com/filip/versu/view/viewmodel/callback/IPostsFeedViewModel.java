package com.filip.versu.view.viewmodel.callback;


import com.filip.versu.controller.ICreateDeleteController;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.dto.PostFeedbackPossibilityDTO;
import com.filip.versu.model.view.abs.AbsPostViewModel;
import com.filip.versu.view.viewmodel.AbsPostsFeedViewModel;

public interface IPostsFeedViewModel<K extends AbsPostViewModel>
        extends IRefreshablePageableViewModel<K>,
                IDisplayingUserProfile,
                IDisplayLocationProfile,
                IDisplayingUserFollowers,
                ICreateDeleteController<PostDTO>,
                ICreateDeleteController.ICreateDeleteControllerCallback<PostDTO> {


    void requestCommentsOfPost(PostDTO postDTO);

    void requestPostFeedbackOfPossibility(PostFeedbackPossibilityDTO possibilityDTO);

    void requestPostsByFeedbackPossibilities(String[] feedbackPossibilities);

    /**
     * if possibilityDTO.post.chosenPossibility == @possibilityDTO -> set chosen possibility to null
     * @param possibilityDTO
     */
    void displayUpdateChosenStateDialog(PostFeedbackPossibilityDTO possibilityDTO);

    void reportPost(PostDTO postDTO);

    void sharePostWithLink(PostDTO postDTO);

    interface IPostsFeedViewCallback<K extends AbsPostViewModel>
            extends IRefreshablePageableViewCallback<K>,
                    IDisplayUserProfileCallback,
                    IDisplayLocationProfileCallback,
                    IDisplayingUserFollowersCallback,
                    ICreateDeleteController.ICreateDeleteViewCallback<PostDTO> {

        void loadCommentsByPost(PostDTO postDTO, AbsPostsFeedViewModel.FeedbackActionClickOrigin feedbackActionClickOrigin);

        void loadPostFeedbackByPost(PostFeedbackPossibilityDTO possibilityDTO, AbsPostsFeedViewModel.FeedbackActionClickOrigin feedbackActionClickOrigin);

        void loadPostsByFeedbackPossibilities(String[] feedbackPossibilities);

        void postReported(PostDTO postDTO);

        void sharePostWithLink(PostDTO postDTO);

    }

}
