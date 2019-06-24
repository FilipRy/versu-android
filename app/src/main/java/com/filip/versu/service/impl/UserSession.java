package com.filip.versu.service.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.filip.versu.exception.NoAccessTokenException;
import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.DeviceInfoDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.service.IDeviceInfoService;
import com.filip.versu.service.IUserService;
import com.filip.versu.service.IUserSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class UserSession implements IUserSession {

    public static final String TAG = UserSession.class.getSimpleName();

    private IUserService userService = UserService.instance();

    private IDeviceInfoService deviceInfoService = DeviceInfoService.instance();

    private static IUserSession userSession;

    private Context context;
    /**
     * Contains information about currently logged in user.
     */
    private UserDTO loggedInUser;

    /**
     * Contains device information about currently logged in user.
     */
    private DeviceInfoDTO loggedInUserDevice;

    private boolean isInit;


    /**
     * This method has to be invoked on app start.
     *
     * @param context
     */
    public static void init(Context context) {
        ((UserSession) instance()).setContext(context);
        ((UserSession) instance()).populateSessionData();
        ((UserSession) instance()).isInit = true;
    }

    @Override
    public boolean isInitialized() {
        return this.isInit;
    }

    private UserSession() {
        this.isInit = false;
    }


    private void setContext(Context context) {
        this.context = context;
    }


    /**
     * Populates the data for currently logged in user. (retrieves user + device data from shared preferences).
     */
    private void populateSessionData() {

        Log.i(TAG, "Populating session of currently logged in user (if any)");

        retrieveLoggedInUser();

        if (!this.isUserSessionValid()) {
            Log.i(TAG, "No user is logged in or user's session is not valid");
            return;
        }

        Log.i(TAG, "User " + this.loggedInUser.username + " is logged in!");

        retrieveDeviceInfoOfLoggedInUser();
        retrieveAddInfo();
    }

    @Override
    public boolean isUserSessionValid() {

        try {
            if (this.loggedInUser == null || this.loggedInUser.getId() == null || this.getAccessTokenOfLoggedInUser() == null) {
                Log.i(UserSession.TAG, "No user is logged in (no user data stored in cache)");
                return false;
            }
        } catch (NoAccessTokenException e) {
            return false;
        }

        Log.i(UserSession.TAG, "User access token is valid till: " + this.loggedInUser.jwtWrapper.expirationTimestamp);
        Log.i(UserSession.TAG, "Current timestamp in ms: " + System.currentTimeMillis());

        return this.loggedInUser.jwtWrapper.expirationTimestamp > System.currentTimeMillis();
    }

    /**
     * Retrieves the information about user, who is currently logged in.
     *
     * @return
     */
    private UserDTO retrieveLoggedInUser() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGGED_IN_USER_DATA, Context.MODE_PRIVATE);
        loggedInUser = UserDTO.retrieveFromSharedPreferences(sharedPreferences);
        return loggedInUser;
    }

    private DeviceInfoDTO retrieveDeviceInfoOfLoggedInUser() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGGED_IN_USER_DEVICE_INFO, Context.MODE_PRIVATE);
        loggedInUserDevice = DeviceInfoDTO.retrieveFromSharedPreferences(sharedPreferences);
        return loggedInUserDevice;
    }

    private void retrieveAddInfo() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ADD_INFO, Context.MODE_PRIVATE);
        postCreatedAtThisDeviceWithThisUser = sharedPreferences.getInt(POST_CREATE_AT_DEVICE_KEY, 0);
    }

    @Override
    public void mergeWithLogedInUserData(UserDTO userDTO) {
        boolean changesDetected = getLogedInUser().updateProperties(userDTO);
        if (changesDetected) {
            persistLoggedInUserLocally(getLogedInUser());
        }
    }

    @Override
    public UserDTO attemptUserRegistration(UserDTO user) throws ServiceException {
        UserDTO userDTO = userService.signUp(user);
        return attemptUserLogin(userDTO);
    }

    @Override
    public UserDTO attemptUserLogin(UserDTO userDTO) throws ServiceException {
        UserDTO loggedInUser = userService.obtainAccessToken(userDTO);

        persistLoggedInUserLocally(loggedInUser);
        populateSessionData();

        if (this.loggedInUserDevice == null) {

            Log.i(TAG, "Obtaining token of this device from firebase");

            int numCores = Runtime.getRuntime().availableProcessors();
            ThreadPoolExecutor executor = new ThreadPoolExecutor(numCores * 2, numCores *2, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(executor, new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "getInstanceId failed", task.getException());
                                return;
                            }

                            // Get new Instance ID token
                            String token = task.getResult().getToken();

                            Log.i(TAG, "Device token = " + token);

                            try {
                                new MyFirebaseMessagingService().sendRegistrationToServer(token, UserSession.this.context);
                            } catch (ServiceException e) {
                                Log.i(TAG,  "Failed to send registration to versu servers due the following error: " + e.getMessage());
                            }
                        }
                    });
        }

        return loggedInUser;
    }

    private UserDTO persistLoggedInUserLocally(UserDTO userDTO) {
        if (userDTO.getId() == null) {//TODO throw some exception
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGGED_IN_USER_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor = userDTO.sharedPreferencesEditorAdapter(editor);
        editor.apply();

        this.loggedInUser = userDTO;
        return userDTO;
    }

    @Override
    public DeviceInfoDTO persistDeviceInfo(DeviceInfoDTO deviceInfoDTO) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGGED_IN_USER_DEVICE_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor = deviceInfoDTO.sharedPreferencesEditorAdapter(editor);
        editor.apply();

        this.loggedInUserDevice = deviceInfoDTO;
        return deviceInfoDTO;
    }


    @Override
    public boolean logoutUser(Context context) throws ServiceException {

        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGGED_IN_USER_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().commit();

        SharedPreferences deviceSharedPrefs = context.getSharedPreferences(LOGGED_IN_USER_DEVICE_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor deviceSharedPrefsEditor = deviceSharedPrefs.edit();
        deviceSharedPrefsEditor.clear().commit();


        SharedPreferences sharedPreferencesAddInfo = context.getSharedPreferences(ADD_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferencesAddInfo.edit();
        sharedPreferencesEditor.clear().commit();

        if (loggedInUserDevice != null && loggedInUserDevice.getId() != null) {
            deviceInfoService.delete(loggedInUserDevice.getId());
        }

        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (IOException e) {

        }

        //TODO clear cache

        return true;
    }

    @Override
    public UserDTO getLogedInUser() {
        if (loggedInUser != null) {
            return loggedInUser;
        }
        return retrieveLoggedInUser();
    }

    @Override
    public DeviceInfoDTO getDeviceInfo() {
        if (loggedInUserDevice != null) {
            return loggedInUserDevice;
        }
        return retrieveDeviceInfoOfLoggedInUser();
    }

    @Override
    public String getAccessTokenOfLoggedInUser() throws NoAccessTokenException {
        if (this.loggedInUser == null) {
            throw new NoAccessTokenException("No user is logged in");
        } else if (this.loggedInUser.jwtWrapper == null) {
            throw new NoAccessTokenException("No access token is associated with you, log out and in again pls");
        }
        return this.loggedInUser.jwtWrapper.access_token;
    }

    public static final String POST_CREATE_AT_DEVICE_KEY = "POST_CREATE_AT_DEVICE_KEY";
    private static final int postCreatedForTutorialThreshold = 3;


    private int postCreatedAtThisDeviceWithThisUser = 0;

    @Override
    public boolean needDisplayTutorial() {
        return postCreatedAtThisDeviceWithThisUser < postCreatedForTutorialThreshold;
    }

    @Override
    public void newPostWasCreated() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ADD_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        postCreatedAtThisDeviceWithThisUser++;
        editor.putInt(POST_CREATE_AT_DEVICE_KEY, postCreatedAtThisDeviceWithThisUser);
        editor.apply();

    }

    public static IUserSession instance() {
        if (userSession == null) {
            userSession = new UserSession();
        }
        return userSession;
    }
}
