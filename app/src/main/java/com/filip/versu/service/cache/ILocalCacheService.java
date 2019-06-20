package com.filip.versu.service.cache;

import com.filip.versu.model.view.abs.AbsBaseViewModel;


public interface ILocalCacheService<K extends AbsBaseViewModel> {


    public void put(K object, String key);

    public K get(String key);
}
