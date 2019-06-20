package com.filip.versu.service.helper;


import android.util.Log;

public class GlobalModelVersion {

    public static final String TAG = GlobalModelVersion.class.getSimpleName();

    /**
     * This is the timestamp when last operation on shopping item was performed (like/dislike/comment ...) in this app.
     */
    private static long SHOPPING_ITEM_TIMESTAMP = 0l;
    /**
     * Timestamp when last operation on followings (create | delete following) was performed in this app.
     */
    private static long FOLLOWING_TIMESTAMP = 0l;


    private static long NOTIFICATION_TIMESTAMP = 0l;

    public static void refreshGlobalShoppingItemTimestamp() {
        Log.d(TAG, "Refreshing global version timestamp of shopping item");
        SHOPPING_ITEM_TIMESTAMP = System.currentTimeMillis();
    }

    public static void refreshGlobalFollowingTimestamp() {
        Log.d(TAG, "Refreshing global version timestamp of followings");
        FOLLOWING_TIMESTAMP = System.currentTimeMillis();
    }

    public static void refreshGlobalNotificationTimestamp() {
        Log.d(TAG, "Refreshing global version timestamp of notifications");
        NOTIFICATION_TIMESTAMP = System.currentTimeMillis();
    }

    public static long getGlobalShoppingItemTimestamp() {
        return SHOPPING_ITEM_TIMESTAMP;
    }

    public static long getGlobalFollowingTimestamp() {
        return FOLLOWING_TIMESTAMP;
    }

    public static long getGlobalNotificationTimestamp() {
        return NOTIFICATION_TIMESTAMP;
    }

}
