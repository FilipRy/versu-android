package com.filip.versu.view.viewmodel.callback;


import com.filip.versu.model.Location;
import com.filip.versu.model.dto.PostFeedbackPossibilityDTO;
import com.filip.versu.model.view.abs.AbsPostViewModel;
import com.filip.versu.view.viewmodel.PostsTimelineFeedViewModel;

public interface IPostsTimelineFeedViewModel<K extends AbsPostViewModel> extends IPostsFeedViewModel<K> {

    public void votingForFeedback(PostFeedbackPossibilityDTO possibility);

    public void setDependencies(PostsTimelineFeedViewModel.NewsfeedType newsfeedType, Long shoppingItemID, String[] feedbackPossiblities, Location location);

    public interface IPostsTimelineFeedViewModelCallback<K extends AbsPostViewModel> extends IPostsFeedViewCallback<K> {


    }

}
