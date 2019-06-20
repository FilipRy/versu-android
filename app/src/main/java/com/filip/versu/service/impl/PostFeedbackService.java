package com.filip.versu.service.impl;

import android.util.Log;

import com.filip.versu.exception.ExceptionMapper;
import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.dto.PostFeedbackPossibilityDTO;
import com.filip.versu.model.dto.PostFeedbackVoteDTO;
import com.filip.versu.service.IPostFeedbackService;
import com.filip.versu.service.helper.ConfigurationReader;
import com.filip.versu.service.helper.Page;
import com.filip.versu.service.helper.SpringPage;
import com.filip.versu.service.impl.abs.AbsGeneralService;

import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;


public class PostFeedbackService extends AbsGeneralService<PostFeedbackVoteDTO, Long> implements IPostFeedbackService {

    private static IPostFeedbackService postFeedbackService;

    public static IPostFeedbackService instance() {
        if(postFeedbackService == null) {
            postFeedbackService = new PostFeedbackService();
        }
        return postFeedbackService;
    }

    @Override
    public PostFeedbackVoteDTO create(PostFeedbackVoteDTO create) throws ServiceException {
        PostFeedbackVoteDTO created = super.create(create);
        create.setId(created.getId());//set only id to avoid losing references at post.myFeedbackAction
        return create;
    }

    @Override
    public List<PostFeedbackVoteDTO> findByUser(Long id, Page<Long> page) throws ServiceException {
        try {
            String url = getServiceEndpointURL() + "/findByUser/" + id;
            if (page.getLastLoadedID() != null) {
                url = url + "?lastId=" + page.getLastLoadedID();
            }
            SpringPage.PostFeedbackVoteSpringPage springPage = (SpringPage.PostFeedbackVoteSpringPage) retrieveObject(url, HttpMethod.GET, SpringPage.PostFeedbackVoteSpringPage.class);
            return fixReferences(springPage.getContent());
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ExceptionMapper().createServiceExceptionFromMessage(e.toString());
        }
    }

    @Override
    public List<PostDTO> findPostsByUser(Long id, Page<Long> page) throws ServiceException {
        List<PostFeedbackVoteDTO> retrievedItems = findByUser(id, page);
        if(retrievedItems != null) {
            List<PostDTO> postDTOs = new ArrayList<>();
            for(PostFeedbackVoteDTO item: retrievedItems) {
                item.feedbackPossibilityDTO.postDTO.myPostFeedback = item;

                postDTOs.add(item.feedbackPossibilityDTO.postDTO);
            }
            PostService.fixReferences(postDTOs);
            return postDTOs;
        }
        return new ArrayList<>();
    }

    @Override
    public List<PostFeedbackVoteDTO> findByFeedbackPossibility(Long id, Page<Long> page) throws ServiceException {
        try {
            String url = getServiceEndpointURL() + "/findByFeedbackPossibility/" + id;
            if (page.getLastLoadedID() != null) {
                url = url + "?lastId=" + page.getLastLoadedID();
            }
            SpringPage.PostFeedbackVoteSpringPage springPage = (SpringPage.PostFeedbackVoteSpringPage) retrieveObject(url, HttpMethod.GET, SpringPage.PostFeedbackVoteSpringPage.class);
            return fixReferences(springPage.getContent());
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            throw new ExceptionMapper().createServiceExceptionFromMessage(e.toString());
        }
    }

    @Override
    public PostFeedbackVoteDTO createCopyForSerialization(PostFeedbackVoteDTO other) {

        PostFeedbackVoteDTO voteCopy = new PostFeedbackVoteDTO(other, false);

        PostDTO postCopy = PostService.instance().createCopyForSerialization(voteCopy.feedbackPossibilityDTO.postDTO);

        voteCopy.feedbackPossibilityDTO.postDTO = postCopy;

        return voteCopy;
    }




    /**
     * This method receives the deserialized list of feedback actions and fixes the references between entities.
     * @param items
     * @return
     */
    @Override
    public List<PostFeedbackVoteDTO> fixReferences(List<PostFeedbackVoteDTO> items) {
        if(items == null) {
            return items;
        }

        for(PostFeedbackVoteDTO vote: items) {
            fixReferences(vote);
        }
        return items;
    }

    public PostFeedbackVoteDTO fixReferences(PostFeedbackVoteDTO vote) {
        PostDTO postDTO = vote.feedbackPossibilityDTO.postDTO;

        PostService.fixReferences(postDTO);

        for(PostFeedbackPossibilityDTO possibilityDTO: postDTO.postFeedbackPossibilities) {
            if(possibilityDTO.equals(vote.feedbackPossibilityDTO)) {
                vote.feedbackPossibilityDTO = possibilityDTO;
            }
        }
        return vote;
    }



    @Override
    public Class<PostFeedbackVoteDTO> getDTOClass() {
        return PostFeedbackVoteDTO.class;
    }

    @Override
    protected String getServiceEndpointURL() {
        String backendUrl = ConfigurationReader.getConfigValue("backend.url");
        return backendUrl + "api/v1/postfeedbackvote";
    }


}
