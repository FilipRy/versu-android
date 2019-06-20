package com.filip.versu.view.viewmodel.callback;

import com.filip.versu.model.dto.PostDTO;

import eu.inloop.viewmodel.IView;


public interface IPostShareViewModel {

    public void generateNewLink(PostDTO postDTO);

    public void removeLink(PostDTO postDTO);

    public interface IPostShareViewModelCallback extends IView {

        public void newLinkGenerated();

        public void linkRemoved();

        public void displayProgress(boolean display);

        public void displayMessage(int messageResId);
        public void displayMessage(String message);

    }

}
