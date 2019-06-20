package com.filip.versu.view.viewmodel.callback;

import com.filip.versu.model.dto.PostDTO;

import eu.inloop.viewmodel.IView;


public interface ICreatePostPossibilitiesViewModel {


    public void setDependencies(PostDTO postDTO);

    public interface ICreatePostPossibilitiesViewModelCallback extends IView {

    }

}
