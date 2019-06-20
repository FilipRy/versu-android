package com.filip.versu.view.viewmodel.callback;

import com.filip.versu.model.dto.PostDTO;

import eu.inloop.viewmodel.IView;

/**
 * Created by Filip on 7/19/2016.
 */
public interface ICreatePostDescriptionViewModel {

    public void setPostDescription(String description);

    public void setDependencies(PostDTO postDTO);

    public interface ICreatePostDescriptionViewModelCallback extends IView {

    }

}
