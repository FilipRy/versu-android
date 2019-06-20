package com.filip.versu.model.view.abs;


import com.filip.versu.model.dto.abs.BaseDTO;

//TODO this should be: T extends AbsFeedbackPostDTO
public abstract class AbsFeedbackPostViewModel<T extends BaseDTO<Long>> extends AbsBaseFeedViewModel<T> {


    public AbsFeedbackPostViewModel(AbsBaseViewModel other) {
        super(other);
    }

    public AbsFeedbackPostViewModel() {
    }

}
