package com.filip.versu.service.impl.abs;

import android.util.Log;

import com.filip.versu.exception.ServiceExceptionMapper;
import com.filip.versu.exception.NoAccessTokenException;
import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.abs.BaseDTO;
import com.filip.versu.service.abs.IAbsCrudService;
import com.filip.versu.service.factory.RestTemplateFactory;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


public abstract class AbsGeneralService<K extends BaseDTO<L>, L> implements IAbsCrudService<K, L> {

    public static final String TAG = AbsGeneralService.class.getSimpleName();

    protected RestTemplate restTemplate = RestTemplateFactory.createRestTemplate();


    @Override
    public K create(K create) throws ServiceException {
        try {
            final String url = getServiceEndpointURL();
            K copy = createCopyForSerialization(create);
            K item = putOrPostObject(url, HttpMethod.POST, copy);
            return item;
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.i(TAG, e.getMessage());
            }
            throw new ServiceExceptionMapper().createServiceExceptionFromException(e);
        }
    }

    @Override
    public K update(K update) throws ServiceException {
        try {
            final String url = getServiceEndpointURL();
            K item = putOrPostObject(url, HttpMethod.PUT, update);
            return item;
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ServiceExceptionMapper().createServiceExceptionFromException(e);
        }
    }

    @Override
    public boolean delete(L id) throws ServiceException {
        try {
            final String url = getServiceEndpointURL() + "/" + id;
            K item = (K) retrieveObject(url, HttpMethod.DELETE, getDTOClass());
            return true;
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ServiceExceptionMapper().createServiceExceptionFromException(e);
        }
    }

    /**
     * created a copy of @other without any circular dependencies
     * @param other
     * @return
     */
    public K createCopyForSerialization(K other) {
        return other;
    }


    @Override
    public List<K> createCopiesForSerialization(List<K> others) {
        if(others == null) {
            return others;
        }
        List<K> itemsForSerialization = new ArrayList<>();
        for(K item: others) {
            K copy = createCopyForSerialization(item);
            itemsForSerialization.add(copy);
        }
        return itemsForSerialization;
    }

    /**
     * this method is used to get class of some DTO, which is used by deserialization of json response.
     * @return
     */
    public abstract Class<K> getDTOClass();

    protected ResponseEntity<?> sendRequestToBackend(String url, HttpMethod httpMethod, HttpEntity<?> requestEntity, Class responseClass) {
        return restTemplate.exchange(url, httpMethod, requestEntity, responseClass);
    }

    protected Object retrieveObject(String url, HttpMethod httpMethod, Class responseClass) throws NoAccessTokenException {
        HttpHeaders httpHeaders = RestTemplateFactory.createRequestHeadersWithUserAccessToken();
        HttpEntity<?> requestEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<?> responseEntity = sendRequestToBackend(url, httpMethod, requestEntity, responseClass);
        return responseEntity.getBody();
    }

    protected K putOrPostObject(String url, HttpMethod httpMethod, K requestEntity) throws NoAccessTokenException {
        HttpHeaders httpHeaders = RestTemplateFactory.createRequestHeadersWithUserAccessToken();
        HttpEntity<K> httpEntity = new HttpEntity<>(requestEntity, httpHeaders);
        ResponseEntity<K> responseEntity = (ResponseEntity<K>) sendRequestToBackend(url, httpMethod, httpEntity, getDTOClass());
        return responseEntity.getBody();
    }


    protected abstract String getServiceEndpointURL();


}
