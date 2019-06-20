package com.filip.versu.view.viewmodel;

import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.view.viewmodel.callback.ICreatePostPossibilitiesViewModel;

import eu.inloop.viewmodel.AbstractViewModel;


public class CreatePostPossibilitiesViewModel
            extends AbstractViewModel<ICreatePostPossibilitiesViewModel.ICreatePostPossibilitiesViewModelCallback>
            implements ICreatePostPossibilitiesViewModel {

    private PostDTO postDTO;

    @Override
    public void setDependencies(PostDTO postDTO) {
        this.postDTO = postDTO;
    }
}
