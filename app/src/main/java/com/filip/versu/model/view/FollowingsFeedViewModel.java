package com.filip.versu.model.view;


import com.filip.versu.model.dto.FollowingDTO;
import com.filip.versu.model.view.abs.AbsBaseFeedViewModel;

public class FollowingsFeedViewModel extends AbsBaseFeedViewModel<FollowingDTO> {

    @Override
    public FollowingsFeedViewModel removeReferencesBeforeSerialization() {
        return this;
    }

    @Override
    public void fixReferences() {
    }
}
