package com.filip.versu.service.impl;

import android.util.Log;

import com.filip.versu.exception.ServiceExceptionMapper;
import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.JWTWrapperDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.service.IUserService;
import com.filip.versu.service.factory.RestTemplateFactory;
import com.filip.versu.service.helper.ConfigurationReader;
import com.filip.versu.service.helper.Page;
import com.filip.versu.service.helper.SpringPage.UserSpringPage;
import com.filip.versu.service.impl.abs.AbsGeneralService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

public class UserService extends AbsGeneralService<UserDTO, Long> implements IUserService {

    private static IUserService userService;


    @Override
    public UserDTO create(UserDTO create) throws ServiceException {
        throw new UnsupportedOperationException("Create User operation is not supported, call signUp(userDTO) instead!");
    }

    @Override
    public UserDTO signUp(UserDTO userDTO) throws ServiceException {
        try {
            final String url = ConfigurationReader.getConfigValue("backend.url") + "signup";
            UserDTO copy = createCopyForSerialization(userDTO);

            HttpEntity<UserDTO> httpEntity = new HttpEntity<>(copy, null);
            ResponseEntity<UserDTO> responseEntity = (ResponseEntity<UserDTO>) sendRequestToBackend(url, HttpMethod.POST, httpEntity, getDTOClass());

            UserDTO item = responseEntity.getBody();

            return item;
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ServiceExceptionMapper().createServiceExceptionFromException(e);
        }
    }

    private UserDTO getUserAfterLogin(String username, JWTWrapperDTO jwtToken) throws ServiceException {

        final String url = getServiceEndpointURL() + "/findOneByName/" + username;

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Authorization", "Bearer " + jwtToken.access_token);

        HttpEntity<?> requestEntity = new HttpEntity<>(requestHeaders);
        ResponseEntity<?> responseEntity = sendRequestToBackend(url, HttpMethod.GET, requestEntity, UserDTO.class);

        return (UserDTO) responseEntity.getBody();
    }

    @Override
    public UserDTO obtainAccessToken(UserDTO userDTO) throws ServiceException {

        try {
            final String url = ConfigurationReader.getConfigValue("backend.url") + "oauth/token";

            HttpHeaders httpHeaders = RestTemplateFactory.createRequestHeadersWithClientAccessToken();

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("username", userDTO.username);
            map.add("password", userDTO.password);
            map.add("grant_type", "password");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, httpHeaders);

            ResponseEntity<JWTWrapperDTO> response = restTemplate.postForEntity(url, request, JWTWrapperDTO.class);

            JWTWrapperDTO jwtWrapperDTO = response.getBody();

            long currentTimestamp = System.currentTimeMillis();
            long expiresInLongMs = jwtWrapperDTO.expires_in * 1000l;

            jwtWrapperDTO.expirationTimestamp = currentTimestamp + expiresInLongMs;

            UserDTO loggedInUser = this.getUserAfterLogin(userDTO.username, jwtWrapperDTO);
            loggedInUser.jwtWrapper = jwtWrapperDTO;

            return loggedInUser;
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ServiceExceptionMapper().createServiceExceptionFromException(e);
        }
    }

    @Override
    public UserDTO getUser(Long id) throws ServiceException {
        try {
            final String url = getServiceEndpointURL() + "/" + id;
            UserDTO userDTO = (UserDTO) retrieveObject(url, HttpMethod.GET, UserDTO.class);
            return userDTO;
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ServiceExceptionMapper().createServiceExceptionFromException(e);
        }
    }

    @Override
    public List<UserDTO> findByName(String name, Page<Long> page) throws ServiceException {
        try {
            final String url = getServiceEndpointURL() + "/findByName/" + name;
            if (page.getLastLoadedID() != null) {
                return new ArrayList<>();
            }
            UserSpringPage userPage = (UserSpringPage) retrieveObject(url, HttpMethod.GET, UserSpringPage.class);
            return userPage.getContent();
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ServiceExceptionMapper().createServiceExceptionFromException(e);
        }
    }

    @Override
    public UserDTO findOneByName(String name) throws ServiceException {
        try {
            final String url = getServiceEndpointURL() + "/findOneByName/" + name;
            UserDTO userDTO = (UserDTO) retrieveObject(url, HttpMethod.GET, UserDTO.class);
            return userDTO;
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ServiceExceptionMapper().createServiceExceptionFromException(e);
        }
    }

    public static IUserService instance() {
        if (userService == null) {
            userService = new UserService();
        }
        return userService;
    }

    @Override
    public Class<UserDTO> getDTOClass() {
        return UserDTO.class;
    }

    @Override
    protected String getServiceEndpointURL() {
        String backendUrl = ConfigurationReader.getConfigValue("backend.url");
        return backendUrl + "api/v1/user";
    }
}
