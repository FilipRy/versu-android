package com.filip.versu.service.impl;

import com.filip.versu.model.dto.DeviceInfoDTO;
import com.filip.versu.service.IDeviceInfoService;
import com.filip.versu.service.helper.ConfigurationReader;
import com.filip.versu.service.impl.abs.AbsGeneralService;


public class DeviceInfoService extends AbsGeneralService<DeviceInfoDTO, Long> implements IDeviceInfoService {

    private static DeviceInfoService deviceInfoService;

    public static IDeviceInfoService instance() {
        if(deviceInfoService ==  null) {
            deviceInfoService = new DeviceInfoService();
        }
        return deviceInfoService;
    }

    @Override
    public Class<DeviceInfoDTO> getDTOClass() {
        return DeviceInfoDTO.class;
    }

    @Override
    protected String getServiceEndpointURL() {
        String backendUrl = ConfigurationReader.getConfigValue("backend.url");
        return backendUrl + "api/v1/deviceinfo";
    }
}
