package com.filip.versu.service;


import android.content.Context;

import com.filip.versu.exception.NoAccessTokenException;
import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.DeviceInfoDTO;
import com.filip.versu.model.dto.UserDTO;

public interface IUserSession {

    public static final String LOGGED_IN_USER_DATA = "loged_in_user_data";

    public static final String LOGGED_IN_USER_DEVICE_INFO = "loged_in_user_device_info";
    public static final String ADD_INFO = "add_info";

    public UserDTO attemptUserRegistration(UserDTO userDTO) throws ServiceException;

    public UserDTO attemptUserLogin(UserDTO userDTO) throws ServiceException;

    /**
     * Save the device info of current logged in user into local storage.
     * @param deviceInfoDTO
     * @return
     */
    public DeviceInfoDTO persistDeviceInfo(DeviceInfoDTO deviceInfoDTO);

    /**
     * This is a blocking method. Call it outside the UI thread.
     * @param context
     * @return
     * @throws ServiceException
     */
    public boolean logoutUser(Context context) throws ServiceException;


    public boolean needDisplayTutorial();

    public void newPostWasCreated();

    boolean isInitialized();

    public boolean isUserSessionValid();

    /**
     * This method is used for external checking on changes in user information (if I refresh myProfile this method is invoked).
     * @param userDTO
     */
    public void mergeWithLogedInUserData(UserDTO userDTO);

    public UserDTO getLogedInUser();

    /**
     * Returns device information of currently logged in user. Returns null if the device is not registered.
     * @return
     */
    public DeviceInfoDTO getDeviceInfo();

    public String getAccessTokenOfLoggedInUser() throws NoAccessTokenException;

}
