package com.filip.versu.view.viewmodel.callback;


import android.content.Context;

import com.filip.versu.model.DevicePhoto;

import java.util.List;

public interface IPhotoSelectorViewModel extends IPegContentViewModel {

    public void requestLoadingAllDeviceImages(Context context);

    public void setPostDescription(String description);

    public interface IPhotoSelectorViewModelCallback extends IPegContentViewCallback {

        public void addItemToView(DevicePhoto devicePhoto);

        public void setDevicePhotosViewItems(List<DevicePhoto> devicePhotos);

        void showLoadingPhotosProgress(boolean show);
    }

}
