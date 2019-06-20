package com.filip.versu.service;

import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.CommentDTO;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.service.abs.IAbsCrudService;
import com.filip.versu.service.helper.Page;

import java.util.List;


public interface ICommentService extends IAbsCrudService<CommentDTO, Long> {

    public List<CommentDTO> findByUser(Long id, Page<Long> page) throws ServiceException;

    /**
     * This method retrieves only the shopping items from returned feedback actions by user.
     * @param id
     * @param page
     * @return
     * @throws ServiceException
     */
    public List<PostDTO> findByUserShoppingItem(Long id, Page<Long> page) throws ServiceException;

    public List<CommentDTO> findByPost(Long id, Page<Long> page) throws ServiceException;

    public List<CommentDTO> fixReferences(List<CommentDTO> items);

}
