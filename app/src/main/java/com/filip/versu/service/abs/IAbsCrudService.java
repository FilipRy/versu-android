package com.filip.versu.service.abs;

import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.abs.BaseDTO;

import java.util.List;

public interface IAbsCrudService<K extends BaseDTO<L>, L> {

    public K create(K create) throws ServiceException;

    public K update(K update) throws ServiceException;

    public boolean delete(L id) throws ServiceException;

    public K createCopyForSerialization(K other);

    public List<K> createCopiesForSerialization(List<K> items);

}
