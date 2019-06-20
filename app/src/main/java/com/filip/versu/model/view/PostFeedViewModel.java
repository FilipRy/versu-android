package com.filip.versu.model.view;

import com.filip.versu.model.view.abs.AbsPostViewModel;
import com.filip.versu.service.impl.PostService;


public class PostFeedViewModel extends AbsPostViewModel {

    public PostFeedViewModel(PostFeedViewModel other) {
        super(other);
    }

    public PostFeedViewModel() {

    }


    @Override
    public PostFeedViewModel removeReferencesBeforeSerialization() {
        PostFeedViewModel copy = new PostFeedViewModel(this);
        copy.feedItems = PostService.instance().createCopiesForSerialization(feedItems);
        return copy;
    }
}
