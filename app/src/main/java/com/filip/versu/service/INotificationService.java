package com.filip.versu.service;


import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.NotificationDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.service.abs.IAbsCrudService;
import com.filip.versu.service.helper.Page;

import java.util.List;

public interface INotificationService extends IAbsCrudService<NotificationDTO, Long> {

    public List<NotificationDTO> findForUser(UserDTO userDTO,  Page<Long> page) throws ServiceException;

    public boolean markAsSeen(NotificationDTO notificationDTO) throws ServiceException;

}
