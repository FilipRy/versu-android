package com.filip.versu.model.view;

import com.filip.versu.model.dto.NotificationDTO;
import com.filip.versu.model.view.abs.AbsBaseFeedViewModel;


public class NotificationFeedViewModel extends AbsBaseFeedViewModel<NotificationDTO> {

    @Override
    public NotificationFeedViewModel removeReferencesBeforeSerialization() {
        return this;
    }

    @Override
    public void fixReferences() {

    }
}
