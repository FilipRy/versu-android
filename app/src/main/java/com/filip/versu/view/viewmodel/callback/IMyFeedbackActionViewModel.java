package com.filip.versu.view.viewmodel.callback;

import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.view.PostFeedViewModel;
import com.filip.versu.view.viewmodel.MyFeedbackActionsViewModel;


public interface IMyFeedbackActionViewModel extends IPostsFeedViewModel<PostFeedViewModel> {

    public void setDependencies(MyFeedbackActionsViewModel.MyFeedbackActionFragmentType myFeedbackActionFragmentType);

    public interface IMyFeedbackActionViewModelCallback extends IPostsTimelineFeedViewModel.IPostsTimelineFeedViewModelCallback<PostFeedViewModel> {


        public void addItemToViewAdapter(PostDTO item);

        public void addItemToViewAdapter(PostDTO item, int position);

        public void removeItemFromViewAdapter(PostDTO item);


    }

}
