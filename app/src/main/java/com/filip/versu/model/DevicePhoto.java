package com.filip.versu.model;


import android.graphics.Bitmap;
import android.widget.ImageView;

import com.filip.versu.model.dto.abs.BaseDTO;

public class DevicePhoto implements BaseDTO<Long> {

    /**
     * Local path of device photo.
     */
    public String path;

    /**
     * Used for post photos.
     */
    public transient Bitmap bitmap;

    public transient ImageView blurImageView;
    public transient ImageView photoImageView;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DevicePhoto that = (DevicePhoto) o;

        return !(path != null ? !path.equals(that.path) : that.path != null);

    }

    @Override
    public Long getId() {
        return Long.valueOf(path.hashCode());
    }

    @Override
    public void setId(Long id) {

    }
}
