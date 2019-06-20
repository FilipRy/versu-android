package com.filip.versu.model.view;

import com.filip.versu.model.Searchable;
import com.filip.versu.model.view.abs.AbsBaseFeedViewModel;


public class SearchHistoryFeedViewModel extends AbsBaseFeedViewModel<Searchable> {

    @Override
    public SearchHistoryFeedViewModel removeReferencesBeforeSerialization() {
        return this;
    }

    @Override
    public void fixReferences() {

    }
}
