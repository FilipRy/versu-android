package com.filip.versu.service.impl;

import android.util.Log;

import com.filip.versu.exception.ServiceExceptionMapper;
import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.NotificationDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.service.INotificationService;
import com.filip.versu.service.helper.ConfigurationReader;
import com.filip.versu.service.helper.Page;
import com.filip.versu.service.impl.abs.AbsGeneralService;

import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NotificationService extends AbsGeneralService<NotificationDTO, Long> implements INotificationService {


    private static INotificationService notificationService;

    @Override
    public List<NotificationDTO> findForUser(UserDTO userDTO, Page<Long> page) throws ServiceException {
        try {
            String url = getServiceEndpointURL() + "/user/" + userDTO.getId();
            if (page.getLastLoadedID() != null) {
                url = url + "?lastId=" + page.getLastLoadedID();
            }
            NotificationDTO[] notifications = (NotificationDTO[]) retrieveObject(url, HttpMethod.GET, NotificationDTO[].class);
            List<NotificationDTO> notificationDTOs = Arrays.asList(notifications);
            return new ArrayList<>(notificationDTOs);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ServiceExceptionMapper().createServiceExceptionFromException(e);
        }
    }

    @Override
    public boolean markAsSeen(NotificationDTO notificationDTO) throws ServiceException {
        try {
            final String url = getServiceEndpointURL() + "/" + notificationDTO.getId() + "/seen";
            Boolean wasUpdated = (Boolean) retrieveObject(url, HttpMethod.GET, Boolean.class);
            return wasUpdated;
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ServiceExceptionMapper().createServiceExceptionFromException(e);
        }
    }

    @Override
    public Class<NotificationDTO> getDTOClass() {
        return NotificationDTO.class;
    }

    @Override
    protected String getServiceEndpointURL() {
        String backendUrl = ConfigurationReader.getConfigValue("backend.url");
        return backendUrl + "api/v1/notification";
    }

    @Override
    public NotificationDTO create(NotificationDTO create) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public NotificationDTO update(NotificationDTO update) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(Long id) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    public static INotificationService instance(){
        if (notificationService == null) {
            notificationService = new NotificationService();
        }
        return notificationService;
    }

}
