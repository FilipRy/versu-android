package com.filip.versu.controller.impl;

import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.service.IPostService;
import com.filip.versu.service.impl.PostService;


public class PostCreateDeleteController extends AbsCreateDeleteController<PostDTO> {


    private IPostService shoppingItemService = PostService.instance();

    /**
     * @param createDeleteControllerCallback
     */
    public PostCreateDeleteController(ICreateDeleteControllerCallback<PostDTO> createDeleteControllerCallback) {
        super(createDeleteControllerCallback);
    }

    @Override
    public PostDTO createCreateItemRequest(PostDTO item) throws ServiceException {
        return shoppingItemService.create(item);
    }

    @Override
    public boolean createDeleteItemRequest(PostDTO item) throws ServiceException {
        return shoppingItemService.delete(item.getId());
    }
}
