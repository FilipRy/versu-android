package com.filip.versu.service;


import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.Location;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.model.dto.UserProfileDTO;
import com.filip.versu.service.abs.IAbsCrudService;
import com.filip.versu.service.helper.Page;

import java.util.List;

public interface IPostService extends IAbsCrudService<PostDTO, Long> {

    public static final String POSSIBILITIES_SEPARATOR = "VS";

    UserProfileDTO getUserProfile(Long id, Page<Long> page) throws ServiceException;

    List<UserDTO> listViewersOfShoppingItem(Long id, Page<Long> page) throws ServiceException;

    /**
     * Request the list of signed URL to be generated for object names in @objectNames.
     * @param objectNames - contains the list of object names separated by a space (" ").
     * @return
     * @throws ServiceException
     */
    String generatePreSignedURL(String objectNames) throws ServiceException;

    /**
     * This method retrieves the most recent items which are visible for user @userID.
     * @param userID
     * @return
     */
    public List<PostDTO> findActiveForUserByTime(Long userID, Page<Long> page) throws ServiceException;

    /**
     * This method retrieves the nearest items which are visible for user @userID.
     * @param userID
     * @return
     */
    public List<PostDTO> findNewsfeedByLocation(Long userID, Location myLocation, Page<Long> page) throws ServiceException;

    public List<PostDTO> findByPossibilityNames(String[] possibilitiesNames, Page<Long> page) throws ServiceException;

    public List<PostDTO> findByLocation(Location location, Page<Long> page) throws ServiceException;

    public List<String[]> findVSPossibilities(String pattern) throws ServiceException;

    public PostDTO getShoppingItem(Long shoppingItemID) throws ServiceException;


}
