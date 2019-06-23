package com.filip.versu.service.impl;

import android.content.Context;
import android.util.Log;

import com.filip.versu.R;
import com.filip.versu.exception.ExceptionMapper;
import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.Location;
import com.filip.versu.model.dto.CommentDTO;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.dto.PostFeedbackVoteDTO;
import com.filip.versu.model.dto.PostFeedbackPossibilityDTO;
import com.filip.versu.model.dto.PostPhotoDTO;
import com.filip.versu.model.dto.URLWrapperDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.model.dto.UserProfileDTO;
import com.filip.versu.service.IPostService;
import com.filip.versu.service.IUserSession;
import com.filip.versu.service.factory.RestTemplateFactory;
import com.filip.versu.service.helper.ConfigurationReader;
import com.filip.versu.service.helper.Helper;
import com.filip.versu.service.helper.Page;
import com.filip.versu.service.helper.SpringPage;
import com.filip.versu.service.helper.SpringPage.PostSpringPage;
import com.filip.versu.service.impl.abs.AbsGeneralService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


public class PostService extends AbsGeneralService<PostDTO, Long> implements IPostService {

    public static final String TAG = PostService.class.getSimpleName();

    private static IUserSession userSession = UserSession.instance();
    private static IPostService shoppingItemService;

    private static final String BUCKET_URL = "https://s3-eu-west-1.amazonaws.com/versu-app/";

    public static final String LOCATION_UNKNOWN_ERROR = "LOCATION_UNKNOWN_ERROR";

    @Override
    public PostDTO create(PostDTO create) throws ServiceException {
        try {
            URLWrapperDTO urlWrapperDTO = new URLWrapperDTO();

            int index = 0;
            for (PostPhotoDTO photoDTO : create.photos) {
                final String objectName = userSession.getLogedInUser().getId() + "_" + System.currentTimeMillis() + "_" + index++;

                URLWrapperDTO.PhotoObject photoObject = new URLWrapperDTO.PhotoObject();
                photoObject.key = objectName;
                photoObject.contentType = "image/jpeg";
                urlWrapperDTO.objectKeys.add(photoObject);
            }

            final String requestPreSignedURL = getServiceEndpointURL() + "/generatePreSignedURL";

            HttpHeaders httpHeaders = RestTemplateFactory.createRequestHeadersWithUserAccessToken();
            HttpEntity<URLWrapperDTO> httpEntity = new HttpEntity<>(urlWrapperDTO, httpHeaders);
            ResponseEntity<URLWrapperDTO> responseEntity = (ResponseEntity<URLWrapperDTO>) sendRequestToBackend(requestPreSignedURL, HttpMethod.POST, httpEntity, URLWrapperDTO.class);

            URLWrapperDTO signedURLs = responseEntity.getBody();

            Log.i(TAG, "Persisting photo to Server storage");

            for (int i = 0; i < signedURLs.urls.size(); i++) {
                String signedURL = signedURLs.urls.get(i);
                PostPhotoDTO photoDTO = create.photos.get(i);

                URL url = new URL(signedURL);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", signedURLs.objectKeys.get(i).contentType);

                BitmapCompressionService.TwoDimensional reqDimens = new BitmapCompressionService.TwoDimensional(
                        BitmapCompressionService.NEW_POST_PHOTO_REQ_WIDTH,
                        BitmapCompressionService.NEW_POST_PHOTO_REQ_HEIGHT);
                BitmapCompressionService.compressBitmapToOutputStream(photoDTO, reqDimens,
                        connection.getOutputStream());

                connection.getOutputStream().close();

                int serverResponseCode = connection.getResponseCode();
                Log.i(TAG, "Persist photo response code: " + serverResponseCode);
                Log.i(TAG, "Persist photo response message: " + connection.getResponseMessage());

                File f = new File(photoDTO.path);//deleting temporary photo
                f.delete();
                if (serverResponseCode == 200) {
                    photoDTO.path = BUCKET_URL + signedURLs.objectKeys.get(i).key;
                } else {
                    throw new ExceptionMapper().createServiceExceptionFromMessage("Photo not persist to storage");
                }
            }

            return super.create(create);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ExceptionMapper().createServiceExceptionFromMessage(e.toString());
        }
    }

    @Override
    public String generatePreSignedURL(String objectNames) throws ServiceException {
        return null;
    }

    @Override
    public List<PostDTO> findActiveForUserByTime(Long userID, Page<Long> page) throws ServiceException {
        try {
            String url = getServiceEndpointURL() + "/findForUser/" + userID + "/time";
            if (page.getLastLoadedID() != null) {
                url = url + "?lastId=" + page.getLastLoadedID();
            }
            PostSpringPage shoppingItemPage = (PostSpringPage) retrieveObject(url, HttpMethod.GET, PostSpringPage.class);
            fixReferences(shoppingItemPage.getContent());
            return shoppingItemPage.getContent();
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ExceptionMapper().createServiceExceptionFromMessage(e.toString());
        }
    }

    @Override
    public List<PostDTO> findNewsfeedByLocation(Long userID, Location location, Page<Long> page) throws ServiceException {
        try {
            if (location.isUnknown()) {
                throw new ServiceException(LOCATION_UNKNOWN_ERROR);
            }

            final String url = getServiceEndpointURL() + "/findForUser/" + userID + "/location?lat=" + location.latitude + "&lng=" + location.longitude;
            SpringPage.PostSpringPage postPage = (PostSpringPage) retrieveObject(url, HttpMethod.GET, SpringPage.PostSpringPage.class);
            fixReferences(postPage.getContent());
            return postPage.getContent();
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ExceptionMapper().createServiceExceptionFromMessage(e.toString());
        }
    }

    @Override
    public List<PostDTO> findByPossibilityNames(String[] possibilityName, Page<Long> page) throws ServiceException {
        try {
            String url = getServiceEndpointURL() + "/findByPossibilityNames?possA=" + possibilityName[0];
            if (possibilityName.length > 1) {
                if (possibilityName[1] != null) {
                    url = url + "&possB=" + possibilityName[1];
                }
            }

            if (page.getPageNr() > 0) {
                url = url + "&page=" + page.getPageNr() + "&limit"+page.getPageSize();
            }

            PostSpringPage postSpringPage = (PostSpringPage) retrieveObject(url, HttpMethod.GET, SpringPage.PostSpringPage.class);
            fixReferences(postSpringPage.getContent());
            return postSpringPage.getContent();
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ExceptionMapper().createServiceExceptionFromMessage(e.toString());
        }
    }

    @Override
    public List<PostDTO> findByLocation(Location location, Page<Long> page) throws ServiceException {
        try {
            String url = getServiceEndpointURL() + "/findByLocationGoogleId/" + location.googleID;
            if (page.getLastLoadedID() != null) {
                url = url + "?lastId=" + page.getLastLoadedID();
            }
            PostSpringPage postSpringPage = (PostSpringPage) retrieveObject(url, HttpMethod.GET, SpringPage.PostSpringPage.class);
            fixReferences(postSpringPage.getContent());
            return postSpringPage.getContent();
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ExceptionMapper().createServiceExceptionFromMessage(e.toString());
        }
    }

    @Override
    public List<String[]> findVSPossibilities(String pattern) throws ServiceException {
        try {

            String url = getServiceEndpointURL() + "/findVSPossibilities/" + pattern;
            SpringPage.StringArrayList vsPossibilities = (SpringPage.StringArrayList) retrieveObject(url, HttpMethod.GET, SpringPage.StringArrayList.class);

            return vsPossibilities;
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ExceptionMapper().createServiceExceptionFromMessage(e.toString());
        }
    }

    @Override
    public PostDTO getShoppingItem(Long shoppingItemID) throws ServiceException {
        try {
            final String url = getServiceEndpointURL() + "/" + shoppingItemID;
            PostDTO postDTO = (PostDTO) retrieveObject(url, HttpMethod.GET, PostDTO.class);
            fixReferences(postDTO);
            return postDTO;
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ExceptionMapper().createServiceExceptionFromMessage(e.toString());
        }
    }

    @Override
    public UserProfileDTO getUserProfile(Long id, Page<Long> page) throws ServiceException {
        try {
            String url = getServiceEndpointURL() + "/userProfile/" + id;

            if (page.getLastLoadedID() != null) {
                url = url + "?lastId=" + page.getLastLoadedID();
            }

            UserProfileDTO userProfileDTO = (UserProfileDTO) retrieveObject(url, HttpMethod.GET, UserProfileDTO.class);

            if (userSession.getLogedInUser().getId().equals(id)) {//if retrieving my profile
                userSession.mergeWithLogedInUserData(userProfileDTO.userCard.userDTO);
            }

            fixReferences(userProfileDTO.userShoppingItems.getContent());

            return userProfileDTO;
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ExceptionMapper().createServiceExceptionFromMessage(e.toString());
        }
    }

    @Override
    public List<UserDTO> listViewersOfShoppingItem(Long id, Page<Long> page) throws ServiceException {
        try {
            final String url = getServiceEndpointURL() + "/viewers/" + id;
            UserDTO[] userDTOs = (UserDTO[]) retrieveObject(url, HttpMethod.GET, UserDTO[].class);
            List<UserDTO> userDTOList = Arrays.asList(userDTOs);
            return new ArrayList<>(userDTOList);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ExceptionMapper().createServiceExceptionFromMessage(e.toString());
        }
    }


    public static void fixReferences(List<PostDTO> loadedItems) {
        for (PostDTO postDTO : loadedItems) {
            fixReferences(postDTO);
        }
    }

    public static void fixReferences(PostDTO postDTO) {
        for (PostPhotoDTO photoDTO : postDTO.photos) {
            photoDTO.post = postDTO;
        }

        for (PostFeedbackPossibilityDTO possibilityDTO : postDTO.postFeedbackPossibilities) {
            possibilityDTO.postDTO = postDTO;
        }

        for (CommentDTO comment : postDTO.comments) {
            comment.postDTO = postDTO;
        }

        if (postDTO.myPostFeedback != null) {

            PostFeedbackVoteDTO vote = postDTO.myPostFeedback;

            postDTO.myPostFeedback.feedbackPossibilityDTO.postDTO = postDTO;

            for (PostFeedbackPossibilityDTO possibilityDTO : postDTO.postFeedbackPossibilities) {
                if (possibilityDTO.equals(vote.feedbackPossibilityDTO)) {
                    vote.feedbackPossibilityDTO = possibilityDTO;
                }
            }
        }

        if(postDTO.chosenFeedbackPossibility != null) {
            for(PostFeedbackPossibilityDTO possibilityDTO: postDTO.postFeedbackPossibilities) {
                if(possibilityDTO.getId().equals(postDTO.chosenFeedbackPossibility.getId())) {//cannot compare possibility on equality, because chosenPossibility.post = null always
                    postDTO.chosenFeedbackPossibility = possibilityDTO;
                    break;
                }
            }
        }

    }

    @Override
    public PostDTO createCopyForSerialization(PostDTO other) {
        PostDTO copy = new PostDTO(other);

        for (PostPhotoDTO photo : copy.photos) {
            photo.post = null;//avoid cyclic dependency
        }

        for (PostFeedbackPossibilityDTO possibilityDTO : copy.postFeedbackPossibilities) {
            possibilityDTO.postDTO = null;//avoid cyclic dependency
        }

        for (CommentDTO comment : copy.comments) {
            comment.postDTO = null;//avoid cyclic dependency
        }

        if (copy.chosenFeedbackPossibility != null) {
            copy.chosenFeedbackPossibility.postDTO = null;
        }

        return copy;
    }


    public static String getFormattedAge(Context context, long publishTime) {
        long now = System.currentTimeMillis();
        long diff = now - publishTime;
        diff = diff / 1000;
        int minsAge = (int) diff / 60;
        int hoursAge = minsAge / 60;
        int daysAge = hoursAge / 24;

        if (daysAge > 0) {
            if (daysAge > 7) {
                Calendar cal = Calendar.getInstance();
                int currentYear = cal.get(Calendar.YEAR);
                cal.setTimeInMillis(publishTime);

                int month = cal.get(Calendar.MONTH);
                String monthName = Helper.convertToMonthName(month, context);

                int day = cal.get(Calendar.DAY_OF_MONTH);

                int creationYear = cal.get(Calendar.YEAR);
                String output = day + " " + monthName;
                if (creationYear != currentYear) {
                    output = output + " " + creationYear;
                }
                return output;
            }
            String ago = context.getString(R.string.ago);
            String day;
            if (daysAge == 1) {
                day = context.getString(R.string.day);
            } else {
                day = context.getString(R.string.days);
            }
            return daysAge + " " + day + " " + ago;
        }

        String ago = context.getString(R.string.ago);

        if (hoursAge > 0) {
            String hour;
            if (hoursAge == 1) {
                hour = context.getString(R.string.hour);
            } else {
                hour = context.getString(R.string.hours);
            }
            return hoursAge + " " + hour + " " + ago;
        }

        String minute;
        if (minsAge == 1) {
            minute = context.getString(R.string.minute);
        } else {
            minute = context.getString(R.string.minutes);
        }
        return minsAge + " " + minute + " " + ago;

    }

    public static IPostService instance() {
        if (shoppingItemService == null) {
            shoppingItemService = new PostService();
        }
        return shoppingItemService;
    }

    @Override
    public Class<PostDTO> getDTOClass() {
        return PostDTO.class;
    }

    @Override
    protected String getServiceEndpointURL() {
        String backendUrl = ConfigurationReader.getConfigValue("backend.url");
        return backendUrl + "api/v1/post";
    }
}
