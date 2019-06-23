package com.filip.versu.service.impl;

import android.util.Log;

import com.filip.versu.exception.ServiceExceptionMapper;
import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.FollowingDTO;
import com.filip.versu.service.IFollowingService;
import com.filip.versu.service.helper.ConfigurationReader;
import com.filip.versu.service.helper.Page;
import com.filip.versu.service.helper.SpringPage.FollowingSpringPage;
import com.filip.versu.service.impl.abs.AbsGeneralService;

import org.springframework.http.HttpMethod;

import java.util.List;

public class FollowingService extends AbsGeneralService<FollowingDTO, Long> implements IFollowingService {

    private static IFollowingService followingService;

    @Override
    public List<FollowingDTO> listFollowings(Long id, Page<Long> page) throws ServiceException{
        try {
            Log.d(FollowingService.class.getName(), "Retrieving followings of user " + id + " from backend");
            String url = getServiceEndpointURL() + "/following/" + id;
            if (page.getLastLoadedID() != null) {
                url = url + "?lastId=" + page.getLastLoadedID();
            }
            FollowingSpringPage followingDTOPage = (FollowingSpringPage) retrieveObject(url, HttpMethod.GET, FollowingSpringPage.class);
            Log.d(FollowingService.class.getName(), "Retrieve followings of user " + id + " returned " + followingDTOPage.getContent().size() + " followings");
            return followingDTOPage.getContent();
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ServiceExceptionMapper().createServiceExceptionFromException(e);
        }
    }

    @Override
    public List<FollowingDTO> listFollowers(Long id, Page<Long> page) throws ServiceException {
        try {
            Log.d(FollowingService.class.getName(), "Retrieving FOLLOWERS of user " + id + " from backend");
            String url = getServiceEndpointURL() + "/followers/" + id;
            if (page.getLastLoadedID() != null) {
                url = url + "?lastId=" + page.getLastLoadedID();
            }
            FollowingSpringPage followingDTOPage = (FollowingSpringPage) retrieveObject(url, HttpMethod.GET, FollowingSpringPage.class);
            Log.d(FollowingService.class.getName(), "Retrieve FOLLOWERS of user " + id + " returned " + followingDTOPage.getContent().size() + " FOLLOWERS");
            return followingDTOPage.getContent();
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ServiceExceptionMapper().createServiceExceptionFromException(e);
        }
    }

    @Override
    public FollowingDTO update(FollowingDTO update) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<FollowingDTO> getDTOClass() {
        return FollowingDTO.class;
    }


    public static IFollowingService instance() {
        if(followingService == null) {
            followingService = new FollowingService();
        }
        return followingService;
    }

    @Override
    protected String getServiceEndpointURL() {
        String backendUrl = ConfigurationReader.getConfigValue("backend.url");
        return backendUrl + "api/v1/following";
    }
}
