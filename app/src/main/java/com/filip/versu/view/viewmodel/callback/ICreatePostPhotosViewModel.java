package com.filip.versu.view.viewmodel.callback;

import android.content.Context;

import com.filip.versu.model.DevicePhoto;
import com.filip.versu.model.dto.PostDTO;

import java.util.List;


public interface ICreatePostPhotosViewModel extends IPegContentViewModel {

    public void addPhotoToPost(DevicePhoto devicePhoto);

    public void removePhotoFromPost(DevicePhoto devicePhoto);

    public void requestLoadingAllDeviceImages(Context context);

    public void setDependencies(PostDTO postDTO);

    public void  addPhotoToGallery(DevicePhoto devicePhoto);

    /**
     * Add the photo to concrete position
     * @param devicePhoto
     * @param position -1 denotes last position
     */
    public void addPhotoToGallery(DevicePhoto devicePhoto, int position);

    public interface ICreatePostPhotosViewModelCallback extends IPegContentViewCallback {

        public void addPhotoToPost(DevicePhoto devicePhoto);

        public void removePhotoFromPost(DevicePhoto devicePhoto);

        public void addItemToView(DevicePhoto devicePhoto);

        /**
         *
         * @param devicePhoto
         * @param position -1 denotes last position
         */
        public void addItemToViewPosition(DevicePhoto devicePhoto, int position);

        public void setDevicePhotosViewItems(List<DevicePhoto> devicePhotos);

        void showLoadingPhotosProgress(boolean show);


        void cropDevicePhoto(DevicePhoto devicePhoto);

        /**
         * This method is invoked by CreatePostActivity when this fragment is going off the screen to save the bitmaps from postPhotoRecyclerView's imageView.
         *
         */
//        public void saveChosenBitmaps();

    }

}
