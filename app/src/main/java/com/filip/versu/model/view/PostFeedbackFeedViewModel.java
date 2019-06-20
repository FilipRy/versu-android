package com.filip.versu.model.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.filip.versu.model.dto.PostFeedbackVoteDTO;
import com.filip.versu.model.view.abs.AbsBaseViewModel;
import com.filip.versu.model.view.abs.AbsFeedbackPostViewModel;
import com.filip.versu.service.IPostFeedbackService;
import com.filip.versu.service.impl.PostFeedbackService;


public class PostFeedbackFeedViewModel extends AbsFeedbackPostViewModel<PostFeedbackVoteDTO> {

    @JsonIgnore
    private IPostFeedbackService postFeedbackService = PostFeedbackService.instance();

    public PostFeedbackFeedViewModel(PostFeedbackFeedViewModel other) {
        super(other);
    }

    public PostFeedbackFeedViewModel() {
    }

    @Override
    public AbsBaseViewModel removeReferencesBeforeSerialization() {
        PostFeedbackFeedViewModel copy = new PostFeedbackFeedViewModel(this);
        copy.feedItems = postFeedbackService.createCopiesForSerialization(feedItems);
        return copy;
    }

    @Override
    public void fixReferences() {
        postFeedbackService.fixReferences(feedItems);
    }
}
