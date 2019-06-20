package com.filip.versu.service;

import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.FollowingDTO;
import com.filip.versu.service.abs.IAbsCrudService;
import com.filip.versu.service.helper.Page;

import java.util.List;


public interface IFollowingService extends IAbsCrudService<FollowingDTO, Long> {

    List<FollowingDTO> listFollowings(Long id, Page<Long> page) throws ServiceException;

    List<FollowingDTO> listFollowers(Long id, Page<Long> page) throws ServiceException;

}
