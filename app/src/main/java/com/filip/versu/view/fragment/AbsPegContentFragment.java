package com.filip.versu.view.fragment;

import com.filip.versu.view.viewmodel.AbsPegContentViewModel;
import com.filip.versu.view.viewmodel.callback.IPegContentViewModel;

import eu.inloop.viewmodel.base.ViewModelBaseFragment;


public abstract class AbsPegContentFragment<C extends IPegContentViewModel.IPegContentViewCallback, T extends AbsPegContentViewModel<C>> extends ViewModelBaseFragment<C, T> implements IPegContentViewModel.IPegContentViewCallback {


    private int imageResource;
    private int imageBackground;
    private String imgURL;

    private String title;

    @Override
    public int getImageResource() {
        return imageResource;
    }

    @Override
    public int getImageBackground() {
        return imageBackground;
    }

    @Override
    public String getImageURL() {
        return imgURL;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public void setImageBackground(int imageBackground) {
        this.imageBackground = imageBackground;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
