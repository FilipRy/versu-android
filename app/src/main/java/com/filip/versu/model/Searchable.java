package com.filip.versu.model;


import com.filip.versu.model.dto.abs.BaseDTO;

/**
 * Entities which implement this interface can be searched in SearchHistoryFragment
 */
public interface Searchable extends BaseDTO<Long> {

    public String getEntryName();

}
