package com.filip.versu.service.impl;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filip.versu.R;
import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.DeviceInfoDTO;
import com.filip.versu.model.dto.NotificationDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.service.IDeviceInfoService;
import com.filip.versu.service.IUserSession;
import com.filip.versu.view.PostDetailActivity;
import com.filip.versu.view.UserProfileActivity;
import com.filip.versu.view.fragment.PostsTimelineFeedFragment;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private IDeviceInfoService deviceInfoService = DeviceInfoService.instance();


    @Override
    public void onNewToken(String token) {

        Log.d(TAG, "Device's token: " + token);
        if(token == null) {
            return;
        }

        try {
            sendRegistrationToServer(token, getApplicationContext());
        } catch (ServiceException e) {
            Log.d(TAG, "Failed to update token on app server!");
        }

    }

    public void sendRegistrationToServer(String token, Context context) throws ServiceException {

        Log.i(TAG, "Sending device's registration token: " + token + " to versu servers");

        // we have to re-init here, because MyFirebaseMessagingService is a service running independent of the app
        UserSession.init(context);
        IUserSession userSession = UserSession.instance();

        DeviceInfoDTO deviceInfoDTO = userSession.getDeviceInfo();

        //if user is not logged in yet.
        if(!userSession.isUserSessionValid()) {
            return;
        }

        if(deviceInfoDTO == null) {
            deviceInfoDTO = new DeviceInfoDTO();
        }

        deviceInfoDTO.deviceRegistrationID = token;
        deviceInfoDTO.owner = userSession.getLogedInUser();


        if(deviceInfoDTO.getId() == null) {
            deviceInfoDTO = deviceInfoService.create(deviceInfoDTO);

        } else {
            deviceInfoDTO = deviceInfoService.update(deviceInfoDTO);
        }

        userSession.persistDeviceInfo(deviceInfoDTO);
        Log.d(TAG, "Token successfully updated!");
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.

        NotificationDTO notificationDTO = createNotificationFromDataPayload(remoteMessage.getData());

        if (notificationDTO == null) {
            return;
        }

        // we have to re-init here, because MyFirebaseMessagingService is a service running independent of the app
        UserSession.init(getApplicationContext());
        IUserSession userSession = UserSession.instance();


        //if user is not logged in.
        if(!userSession.isUserSessionValid()) {
            return;
        }



        NotificationCompat.Builder mBuilder = createBuilderForNotification(notificationDTO);

        Intent resultIntent = createIntentFromNotification(notificationDTO);

        if(resultIntent == null) {
            return;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int mId = createNotificationId(notificationDTO);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());

    }

    private NotificationCompat.Builder createBuilderForNotification(NotificationDTO notificationDTO) {

        String title = "";
        String text = "";

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (notificationDTO.type == NotificationDTO.NotificationType.post_feedback) {
            title = getString(R.string.notification_new_vote_title);
            if (notificationDTO.count > 1) {
                text = notificationDTO.userDTO.username + " " + getString(R.string.and) + " " + (notificationDTO.count - 1) + " " + getString(R.string.voted_on_your_post);
            } else {
                text = notificationDTO.userDTO.username + " " + getString(R.string.voted_on_your_post);
            }

        } else if (notificationDTO.type == NotificationDTO.NotificationType.post) {
            title = getString(R.string.notification_new_post_title);
            text = notificationDTO.userDTO.username + " " + getString(R.string.need_your_help);
        } else if (notificationDTO.type == NotificationDTO.NotificationType.comment) {
            title = getString(R.string.notification_new_comment_title);

            if (notificationDTO.count > 1) {
                text = notificationDTO.userDTO.username + " " + getString(R.string.and) + " " + (notificationDTO.count - 1) + " " + getString(R.string.commented_on_your_post);
            } else {
                text = notificationDTO.userDTO.username + " " + getString(R.string.commented_on_your_post);
            }


        } else if (notificationDTO.type == NotificationDTO.NotificationType.following) {
            title = getString(R.string.notification_new_follower_title);
            text = notificationDTO.userDTO.username + " " + getString(R.string.started_following_you);
        }

        return new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_app_logo)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setSound(defaultSoundUri)
                .setContentText(text);

    }

    private Intent createIntentFromNotification(NotificationDTO notificationDTO) {
        // Creates an explicit intent for an Activity in your app

        if(notificationDTO.type == NotificationDTO.NotificationType.post_feedback ||
                notificationDTO.type == NotificationDTO.NotificationType.post ||
                notificationDTO.type == NotificationDTO.NotificationType.comment) {

            Intent resultIntent = new Intent(this, PostDetailActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            resultIntent.putExtra(PostDetailActivity.POST_FEED_TYPE_KEY, PostDetailActivity.PostFeedType.DETAIL);
            resultIntent.putExtra(PostsTimelineFeedFragment.SHOPPING_ITEM_ID, notificationDTO.contentEntityID);
            resultIntent.putExtra(PostDetailActivity.DETAIL_TYPE_KEY, notificationDTO.type);

            return resultIntent;

        }
        else if (notificationDTO.type == NotificationDTO.NotificationType.following) {
            Intent intent = new Intent(this, UserProfileActivity.class);
            intent.putExtra(UserProfileActivity.USER_KEY, notificationDTO.userDTO);

            return intent;
        }

        return null;
    }


    private int createNotificationId(NotificationDTO notificationDTO) {
        int log10 = (int) Math.log10(notificationDTO.contentEntityID);
        log10++;

        int typeOrdinal = notificationDTO.type.ordinal() + 1;

        int notificationTypeOrdinal = typeOrdinal * (int) Math.pow(10, log10);

        int mId = (int) (notificationTypeOrdinal + notificationDTO.contentEntityID); //TODO fixme, contentEntityID is of type Long

        //mId of notification is composed of: 1st digit is (index + 1) of notificationType enum
        //                                    2...n-th digit are contentEntityID digits
        return mId;
    }

    private NotificationDTO createNotificationFromDataPayload(Map<String, String> dataPayload) {
        NotificationDTO notificationDTO = new NotificationDTO();

        String userJSON = dataPayload.get("userDTO");

        if(userJSON == null || userJSON.isEmpty()) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        UserDTO userDTO;
        try {
            userDTO = mapper.readValue(userJSON, UserDTO.class);
            //TODO validate user, if he has all neccessary info
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
            return null;
        }

        String typePayload = dataPayload.get("type");
        String countPayload = dataPayload.get("count");
        String contentEntityIdPayload = dataPayload.get("contentEntityID");//this can be null, because sometimes (FollowingNotif) is created earlier as Following entity -> no id.
        String timestampPayload = dataPayload.get("creationTime");

        if(typePayload == null || countPayload == null || timestampPayload == null) {
            return null;
        }


        NotificationDTO.NotificationType type = null;
        int count = 0;
        Long timestamp = null;
        try {
            type = NotificationDTO.NotificationType.valueOf(typePayload);
            count = Integer.parseInt(countPayload);
            timestamp = Long.parseLong(timestampPayload);
        } catch (Exception e) {
            return null;
        }

        Long contentEntityId = null;
        try {
            contentEntityId = Long.parseLong(contentEntityIdPayload);
        } catch (NumberFormatException e) {
            contentEntityId = 0l;

        }
        notificationDTO.contentEntityID = contentEntityId;
        notificationDTO.userDTO = userDTO;
        notificationDTO.type = type;
        notificationDTO.count = count;
        notificationDTO.creationTime = timestamp;

        return notificationDTO;
    }

}
