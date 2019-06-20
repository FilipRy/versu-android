package com.filip.versu.model.view.abs;

import com.filip.versu.model.dto.abs.BaseDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an abstract view model for all feeds.
 * @param <K>
 */
public abstract class AbsBaseFeedViewModel<K extends BaseDTO<Long>> extends AbsBaseViewModel {

    public List<K> feedItems = new ArrayList<>();

    public AbsBaseFeedViewModel() {
        super();
    }

    public AbsBaseFeedViewModel(AbsBaseViewModel other) {
        super(other);
    }

    @Override
    public int getSize() {
        return feedItems.size();
    }

    @Override
    public List getPageableContent() {
        return feedItems;
    }


}
