package com.filip.versu.model.dto.abs;


import java.io.Serializable;

public interface BaseDTO<K> extends Serializable {

    public K getId();

    public void setId(K id);
}
