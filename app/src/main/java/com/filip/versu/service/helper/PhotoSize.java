package com.filip.versu.service.helper;


import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

import com.filip.versu.R;

public class PhotoSize {

    public static final int RESOLUTION_HEIGHT = 960;
    public static final int RESOLUTION_WIDTH = 480;

    private static Context appContext;
    private static Display display;

    public static void init(Context context) {
        appContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();

    }

    public static int getSingleItemPhotoWidth() {
        return getVotingItemPhotoWidth();
    }

    public static int getSingleItemPhotoHeight() {
        return getVotingItemPhotoHeight();
    }


    public static int getVotingItemPhotoWidth() {
        return appContext.getResources().getDimensionPixelSize(R.dimen.shopping_item_photo_width);
    }

    public static int getVotingItemPhotoHeight() {
        return appContext.getResources().getDimensionPixelSize(R.dimen.shopping_item_photo_height);
    }


}
