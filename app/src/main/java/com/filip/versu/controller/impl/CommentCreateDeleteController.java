package com.filip.versu.controller.impl;

import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.CommentDTO;
import com.filip.versu.service.ICommentService;
import com.filip.versu.service.impl.CommentService;


public class CommentCreateDeleteController extends AbsCreateDeleteController<CommentDTO> {

    private ICommentService commentService = CommentService.instance();

    /**
     * @param createDeleteControllerCallback
     */
    public CommentCreateDeleteController(ICreateDeleteControllerCallback<CommentDTO> createDeleteControllerCallback) {
        super(createDeleteControllerCallback);
    }

    @Override
    public CommentDTO createCreateItemRequest(CommentDTO item) throws ServiceException {
        return commentService.create(item);
    }

    @Override
    public boolean createDeleteItemRequest(CommentDTO item) throws ServiceException {
        return commentService.delete(item.getId());
    }
}
