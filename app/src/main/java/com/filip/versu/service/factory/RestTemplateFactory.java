package com.filip.versu.service.factory;

import android.util.Base64;

import com.filip.versu.exception.NoAccessTokenException;
import com.filip.versu.service.helper.ConfigurationReader;
import com.filip.versu.service.impl.UserSession;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;


public class RestTemplateFactory {

    private static RestTemplate restTemplate;


    public static RestTemplate createRestTemplate() {
        if (restTemplate != null) {
            return restTemplate;
        }
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

        return restTemplate;
    }


    public static HttpHeaders createRequestHeadersWithUserAccessToken() throws NoAccessTokenException {
        String accessToken = UserSession.instance().getAccessTokenOfLoggedInUser();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Authorization", "Bearer " + accessToken);
        return requestHeaders;
    }

    public static HttpHeaders createRequestHeadersWithClientAccessToken() {
        String clientId = ConfigurationReader.getConfigValue("client.id");
        String clientSecret = ConfigurationReader.getConfigValue("client.secret");

        String toEncode = clientId + ":" + clientSecret;

        try {
            byte[] bytesToEncode = toEncode.getBytes("UTF-8");
            String encodedAuth = Base64.encodeToString(bytesToEncode, Base64.DEFAULT);

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.set("Authorization", "Basic " + encodedAuth);
            requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            return requestHeaders;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
