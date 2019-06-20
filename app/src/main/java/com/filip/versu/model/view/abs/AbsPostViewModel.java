package com.filip.versu.model.view.abs;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.view.PostFeedViewModel;
import com.filip.versu.model.view.UserProfileFeedViewModel;
import com.filip.versu.service.impl.PostService;


@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSubTypes({@Type(value = UserProfileFeedViewModel.class, name = "user"),
        @Type(value = PostFeedViewModel.class, name = "shopping")})
public abstract class AbsPostViewModel extends AbsBaseFeedViewModel<PostDTO> {


    public AbsPostViewModel() {
    }

    public AbsPostViewModel(AbsPostViewModel other) {
        super(other);
        feedItems = other.feedItems;
    }

    @Override
    public void fixReferences() {
        PostService.fixReferences(feedItems);
    }

}
