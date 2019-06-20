package com.filip.versu.view.viewmodel;

import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.view.viewmodel.callback.ICreatePostDescriptionViewModel;
import com.filip.versu.view.viewmodel.callback.ICreatePostDescriptionViewModel.ICreatePostDescriptionViewModelCallback;

import eu.inloop.viewmodel.AbstractViewModel;


public class CreatePostDescriptionViewModel
                extends AbstractViewModel<ICreatePostDescriptionViewModelCallback>
                implements ICreatePostDescriptionViewModel {


    private PostDTO postDTO;

    @Override
    public void setPostDescription(String description) {
        postDTO.description = description;
    }

    @Override
    public void setDependencies(PostDTO postDTO) {
        this.postDTO = postDTO;
    }
}
