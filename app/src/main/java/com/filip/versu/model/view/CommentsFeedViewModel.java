package com.filip.versu.model.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.filip.versu.model.dto.CommentDTO;
import com.filip.versu.model.view.abs.AbsFeedbackPostViewModel;
import com.filip.versu.service.ICommentService;
import com.filip.versu.service.impl.CommentService;


public class CommentsFeedViewModel extends AbsFeedbackPostViewModel<CommentDTO> {

    @JsonIgnore
    private ICommentService commentService = CommentService.instance();

    public CommentsFeedViewModel(CommentsFeedViewModel other) {
        super(other);
    }

    public CommentsFeedViewModel() {
    }

    @Override
    public CommentsFeedViewModel removeReferencesBeforeSerialization() {
        CommentsFeedViewModel copy = new CommentsFeedViewModel(this);
        copy.feedItems = commentService.createCopiesForSerialization(feedItems);
        return copy;
    }

    @Override
    public void fixReferences() {
        commentService.fixReferences(feedItems);
    }
}
