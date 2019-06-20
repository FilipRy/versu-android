package com.filip.versu.model.dto;


import android.content.SharedPreferences;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.filip.versu.model.dto.abs.AbsBaseEntityWithOwnerDTO;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceInfoDTO extends AbsBaseEntityWithOwnerDTO<Long> implements Serializable {

    public static final String PREF_KEY_ID = "id";
    public static final String PREF_KEY_DEVICE_ID = "device_id";
    public static final String PREF_KEY_DEVICE_INFO = "device_info";
    public static final String PREF_KEY_HAS_NEW_TOKEN = "has_old_token";

    public String deviceRegistrationID;
    public String deviceInformation;
    public long registrationTime;

    public DeviceInfoDTO() {

    }


    public SharedPreferences.Editor sharedPreferencesEditorAdapter(SharedPreferences.Editor editor) {
        editor.putLong(PREF_KEY_ID, super.getId());
        editor.putString(PREF_KEY_DEVICE_ID, deviceRegistrationID);
        editor.putString(PREF_KEY_DEVICE_INFO, deviceInformation);
//        editor.putBoolean(PREF_KEY_HAS_NEW_TOKEN, tokenUpToDate);

        return editor;
    }

    public static DeviceInfoDTO retrieveFromSharedPreferences(SharedPreferences sharedPreferences) {

        Long deviceId = sharedPreferences.getLong(PREF_KEY_ID, -1l);
        if(deviceId == -1l) {
            return null;
        }

        String deviceRegistrationID = sharedPreferences.getString(PREF_KEY_DEVICE_ID, "");
        if(deviceRegistrationID.equals("")) {
            return null;
        }

        boolean hasNewToken = sharedPreferences.getBoolean(PREF_KEY_HAS_NEW_TOKEN, false);

        DeviceInfoDTO deviceInfoDTO = new DeviceInfoDTO();
        deviceInfoDTO.setId(deviceId);
        deviceInfoDTO.deviceRegistrationID = deviceRegistrationID;
//        DeviceInfoDTO.tokenUpToDate = hasNewToken;

        return deviceInfoDTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DeviceInfoDTO that = (DeviceInfoDTO) o;

        if (registrationTime != that.registrationTime) return false;
        if (deviceRegistrationID != null ? !deviceRegistrationID.equals(that.deviceRegistrationID) : that.deviceRegistrationID != null)
            return false;
        return deviceInformation != null ? deviceInformation.equals(that.deviceInformation) : that.deviceInformation == null;

    }

}
