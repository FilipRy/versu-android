package com.filip.versu.controller.impl;

import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.FollowingDTO;
import com.filip.versu.service.IFollowingService;
import com.filip.versu.service.impl.FollowingService;


public class FollowingCreateDeleteController extends AbsCreateDeleteController<FollowingDTO> {

    private IFollowingService followingService = FollowingService.instance();

    /**
     * @param createDeleteControllerCallback
     */
    public FollowingCreateDeleteController(ICreateDeleteControllerCallback<FollowingDTO> createDeleteControllerCallback) {
        super(createDeleteControllerCallback);
    }

    @Override
    public FollowingDTO createCreateItemRequest(FollowingDTO item) throws ServiceException {
        return followingService.create(item);
    }

    @Override
    public boolean createDeleteItemRequest(FollowingDTO item) throws ServiceException {
        return followingService.delete(item.getId());
    }
}
