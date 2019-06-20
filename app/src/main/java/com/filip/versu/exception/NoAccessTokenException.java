package com.filip.versu.exception;

/**
 * Created by Filip on 5/2/2016.
 */
public class NoAccessTokenException extends ServiceException {

    public NoAccessTokenException(String detailMessage) {
        super(detailMessage);
    }
}
