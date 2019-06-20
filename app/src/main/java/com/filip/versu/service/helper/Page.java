package com.filip.versu.service.helper;

/**
 * This is a helper class for storing information about the Page which should be loaded at the request
 */
public class Page<K> {

    private int pageSize;

    private int pageNr;

    /**
     * The ID of last loaded item. Special value null says that there is no item loaded yet.
     */
    private K lastLoadedID;

    public Page() {

    }

    public Page(int pageSize, K lastLoadedID) {
        this.pageSize = pageSize;
        this.lastLoadedID = lastLoadedID;
    }

    public int getPageNr() {
        return pageNr;
    }

    public void setPageNr(int pageNr) {
        this.pageNr = pageNr;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public K getLastLoadedID() {
        return lastLoadedID;
    }

    public void setLastLoadedID(K lastLoadedID) {
        this.lastLoadedID = lastLoadedID;
    }
}
