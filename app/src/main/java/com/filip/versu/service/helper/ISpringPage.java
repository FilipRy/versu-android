package com.filip.versu.service.helper;

import java.io.Serializable;
import java.util.List;


public interface ISpringPage<T> extends Serializable {


    int getNumber();

    int getSize();

    int getNumberOfElements();

    List<T> getContent();

    boolean hasContent();

    boolean isFirst();

    boolean isLast();

    boolean hasNext();

    boolean hasPrevious();

    int getTotalPages();

    long getTotalElements();

}

