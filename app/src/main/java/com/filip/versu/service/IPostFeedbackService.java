package com.filip.versu.service;

import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.dto.PostFeedbackVoteDTO;
import com.filip.versu.service.abs.IAbsCrudService;
import com.filip.versu.service.helper.Page;

import java.util.List;


public interface IPostFeedbackService extends IAbsCrudService<PostFeedbackVoteDTO, Long> {

    public List<PostFeedbackVoteDTO> findByUser(Long id, Page<Long> page) throws ServiceException;

    public List<PostFeedbackVoteDTO> findByFeedbackPossibility(Long id, Page<Long> page) throws ServiceException;

    public List<PostDTO> findPostsByUser(Long id, Page<Long> page) throws ServiceException;


    List<PostFeedbackVoteDTO> fixReferences(List<PostFeedbackVoteDTO> items);
}
