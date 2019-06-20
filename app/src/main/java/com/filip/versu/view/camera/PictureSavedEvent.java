package com.filip.versu.view.camera;


import android.util.Log;

import com.filip.versu.model.DevicePhoto;

public class PictureSavedEvent {

    public Throwable exception;


    private DevicePhoto savedDevicePhoto;

    public PictureSavedEvent() {
    }

    public PictureSavedEvent(Throwable exception) {
        if (exception != null) {
            Log.e("CWAC-Cam2", "Exception in saving photo", exception);
        }
        this.exception = exception;
    }

    public PictureSavedEvent(String path) {
        super();
        savedDevicePhoto = new DevicePhoto();
        savedDevicePhoto.path = path;
    }

    public DevicePhoto getSavedDevicePhoto() {
        return savedDevicePhoto;
    }
}
