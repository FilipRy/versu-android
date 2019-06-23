package com.filip.versu.service.impl;

import android.content.Context;
import android.util.Log;

import com.filip.versu.R;
import com.filip.versu.exception.ServiceExceptionMapper;
import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.CommentDTO;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.service.ICommentService;
import com.filip.versu.service.helper.ConfigurationReader;
import com.filip.versu.service.helper.Helper;
import com.filip.versu.service.helper.Page;
import com.filip.versu.service.helper.SpringPage.CommentSpringPage;
import com.filip.versu.service.impl.abs.AbsGeneralService;

import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CommentService extends AbsGeneralService<CommentDTO, Long> implements ICommentService {

    private static ICommentService commentService;

    @Override
    public CommentDTO create(CommentDTO create) throws ServiceException {
        CommentDTO copy = createCopyForSerialization(create);
        CommentDTO created = super.create(copy);
        create.setId(created.getId());//set only id to avoid losing references at post.myFeedbackAction
        return create;
    }

    @Override
    public CommentDTO update(CommentDTO update) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<CommentDTO> findByUser(Long id, Page<Long> page) throws ServiceException {
        try {
            String url = getServiceEndpointURL() + "/findByUser/" + id;
            if (page.getLastLoadedID() != null) {
                url = url + "?lastId=" + page.getLastLoadedID();
            }
            CommentSpringPage springPage = (CommentSpringPage) retrieveObject(url, HttpMethod.GET, getSpringPageClass());
            return fixReferences(springPage.getContent());
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ServiceExceptionMapper().createServiceExceptionFromException(e);
        }
    }

    @Override
    public List<CommentDTO> findByPost(Long id, Page<Long> page) throws ServiceException {
        try {
            String url = getServiceEndpointURL() + "/findByPost/" + id;
            if (page.getLastLoadedID() != null) {
                url = url + "?lastId=" + page.getLastLoadedID();
            }
            CommentSpringPage springPage = (CommentSpringPage) retrieveObject(url, HttpMethod.GET, getSpringPageClass());
            return fixReferences(springPage.getContent());
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ServiceExceptionMapper().createServiceExceptionFromException(e);
        }
    }

    @Override
    public List<PostDTO> findByUserShoppingItem(Long id, Page<Long> page) throws ServiceException {
        List<CommentDTO> retrievedItems = findByUser(id, page);
        if (retrievedItems != null) {
            List<PostDTO> postDTOs = new ArrayList<>();
            for (CommentDTO item : retrievedItems) {
                setMyFeedbackActionOnPost(item, item.postDTO);
                postDTOs.add(item.postDTO);
            }
            PostService.fixReferences(postDTOs);
            return postDTOs;
        }
        return new ArrayList<>();
    }

    /**
     * This method receives the deserialized list of feedback actions and fixes the references between entities.
     *
     * @param items
     * @return
     */
    @Override
    public List<CommentDTO> fixReferences(List<CommentDTO> items) {
        if (items == null) {
            return items;
        }

        for (CommentDTO feedbackAction : items) {
            PostDTO postDTO = feedbackAction.postDTO;

            PostService.fixReferences(postDTO);
        }
        return items;
    }


    @Override
    public CommentDTO createCopyForSerialization(CommentDTO other) {
        CommentDTO copy = createCopy(other, false);

        PostDTO shoppingItemCopy = PostService.instance().createCopyForSerialization(copy.postDTO);

        copy.postDTO = shoppingItemCopy;

        return copy;
    }

    protected CommentDTO createCopy(CommentDTO other, boolean creatingFromShoppingItem) {
        return new CommentDTO(other, false);
    }

    protected void setMyFeedbackActionOnPost(CommentDTO myFeedbackAction, PostDTO postDTO) {

    }

    protected Class<CommentSpringPage> getSpringPageClass() {
        return CommentSpringPage.class;
    }

    @Override
    public Class<CommentDTO> getDTOClass() {
        return CommentDTO.class;
    }

    @Override
    protected String getServiceEndpointURL() {
        String backendUrl = ConfigurationReader.getConfigValue("backend.url");
        return backendUrl + "api/v1/comment";
    }


    public static String getFormattedDate(Context context, long timestamp) {
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        cal.setTimeInMillis(timestamp);

        int month = cal.get(Calendar.MONTH);
        String monthName = Helper.convertToMonthName(month, context);

        int day = cal.get(Calendar.DAY_OF_MONTH);

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        int creationYear = cal.get(Calendar.YEAR);
        String output = day + " " + monthName;
        if (creationYear != currentYear) {
            output = output + " " + creationYear;
        }

        String strHour = Integer.toString(hour);
        String strMinute = Integer.toString(minute);

        if (hour < 10) {
            strHour = "0" + strHour;
        }
        if (minute < 10) {
            strMinute = "0" + strMinute;
        }

        output = output + " " + context.getString(R.string.at) + " " + strHour + ":" + strMinute;

        return output;
    }

    public static ICommentService instance() {
        if (commentService == null) {
            commentService = new CommentService();
        }
        return commentService;
    }

}
