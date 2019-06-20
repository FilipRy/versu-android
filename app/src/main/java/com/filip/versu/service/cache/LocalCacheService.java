package com.filip.versu.service.cache;

import android.content.Context;

import com.filip.versu.model.view.abs.AbsBaseViewModel;
import com.filip.versu.service.helper.Helper;
import com.vincentbrison.openlibraries.android.dualcache.Builder;
import com.vincentbrison.openlibraries.android.dualcache.CacheSerializer;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;
import com.vincentbrison.openlibraries.android.dualcache.JsonSerializer;


public class LocalCacheService<K extends AbsBaseViewModel> implements ILocalCacheService<K> {


    private DualCache<K> localCacheInstance;


    private String filename;

    /**
     *
     * @param clazz
     * @param filename
     * @param cacheTTL - if <=0 -> live forever
     * @param sizeInRam
     * @param sizeOnDisk
     * @param context
     */
    public LocalCacheService(Class<K> clazz, String filename, int cacheTTL, int sizeInRam, int sizeOnDisk, Context context) {

        this.filename = filename;

        CacheSerializer<K> jsonSerializer = new JsonSerializer<>(clazz);

        localCacheInstance = new Builder<>(filename, Helper.APP_VERSION, clazz).
                useSerializerInRam(sizeInRam, jsonSerializer).
                useSerializerInDisk(sizeOnDisk, true, jsonSerializer, context).
                build();

    }


    @Override
    public void put(K object, String key) {
        localCacheInstance.put(key, object);
    }

    @Override
    public K get(String key) {
        return localCacheInstance.get(key);
    }
}
