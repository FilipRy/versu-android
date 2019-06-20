package com.filip.versu.service;


import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.service.abs.IAbsCrudService;
import com.filip.versu.service.helper.Page;

import java.util.List;

public interface IUserService extends IAbsCrudService<UserDTO, Long> {


    public UserDTO signUp(UserDTO userDTO) throws ServiceException;

    public UserDTO obtainAccessToken(UserDTO userDTO) throws ServiceException;

    public UserDTO getUser(Long id) throws ServiceException;

    public UserDTO findOneByName(String name) throws ServiceException;

    public List<UserDTO> findByName(String name, Page<Long> page) throws ServiceException;

}
