package com.filip.versu.exception;

import com.filip.versu.service.impl.PostService;

/**
 * Created by Filip on 4/14/2016.
 */
public class ExceptionMapper {

    public ServiceException createServiceExceptionFromMessage(String message) {
        message = message.toUpperCase();

        if ((message.contains("CONNECTEXCEPTION") && message.contains("FAILED TO CONNECT")) || message.contains(("Unable to resolve host").toUpperCase())) {
            return new ServiceException("Cannot reach Versu servers");
        } else if (message.contains("400")) {
            return new ServiceException("Authentication failed");
        } else if (message.contains("401")) {
            return new ServiceException("You are not authorized to perform this action");
        } else if (message.contains("404") && message.contains("NOT FOUND")) {
            return new ServiceException("Refresh the feed and try again...");
        } else if (message.contains("409") && message.contains("CONFLICT")) {

            //backend returns only status code + status name, no description.
//            if(message.contains("USERNAME_TAKEN")) {
//                return new ServiceException("Your chosen username is already taken");
//            } else if (message.contains("EMAIL_TAKEN")) {
//                return new ServiceException("Your chosen email is already associated with some account");
//            }

            return new ServiceException("Something went wrong refresh the feed a try again");
        } else if (message.contains("403") && message.contains("FORBIDDEN")) {
            return new ServiceException("You are not allowed to perform this action");
        } else if (message.contains("412") && message.contains("PRECONDITION FAILED")) {
            return new ServiceException("Something went wrong refresh the feed a try again");
        } else if (message.contains(PostService.LOCATION_UNKNOWN_ERROR)) {
            return new ServiceException("Your location is not available! Turn on location and restart app.");
        } else {
            return new ServiceException("Error occured, try again!");
        }
    }

}
