package com.filip.versu.model;

import com.filip.versu.service.impl.PostService;

/**
 * This class represents search of "VS" hashtags.
 */
public class HashtagSearch implements Searchable {

    public String possibilityA;
    public String possibilityB;

    @Override
    public String getEntryName() {
        return possibilityA + PostService.POSSIBILITIES_SEPARATOR + possibilityB;
    }

    @Override
    public Long getId() {
        return Long.valueOf(possibilityA.hashCode() + possibilityB.hashCode());
    }

    @Override
    public void setId(Long id) {

    }
}
