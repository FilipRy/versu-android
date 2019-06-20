package com.filip.versu.controller;

import com.filip.versu.model.dto.abs.BaseDTO;

import java.util.List;


public interface ICreateDeleteController<T extends BaseDTO<Long>> {


    /**
     * Creates new item (in server + in cache).
     *
     * @param item
     * @param lastLoadedItems - list of lastLoadedContent items == in lastLoadedItems @item should be added after it is created at backend, can be null
     */
    public void executeCreateItemTask(T item, List<T> lastLoadedItems);

    /**
     * Deletes the item from caches + from server.
     *
     * @param item
     * @param lastLoadedItems - list of lastLoadedContent items == in lastLoadedItems @item should be removed after it is deleted at backend, can be null
     */
    public void executeDeleteItemTask(T item, int position, List<T> lastLoadedItems);


    /**
     * This is a callback for notifing about results of create-delete backend operations.
     * @param <T>
     */
    public interface ICreateDeleteControllerCallback<T extends BaseDTO<Long>> extends ICreateDeleteViewCallback<T> {

        void onCreateOperationSuccess(T createdItem);

        void onDeleteOperationSuccess(T deletedItem);

        /**
         * Callback method for incrementing view model + global version code of some type.
         * @param updatedItem Necessary if one ViewModel implments multiple different ICreateDeleteControllers -> they are different only in @updatedItem type.
         */
        void updateViewModelVersionNumber(T updatedItem);

        void displayCreateDeleteControllerError(String errorMsg);

    }

    /**
     * Callback to fragment.
     * @param <T>
     */
    interface ICreateDeleteViewCallback<T extends BaseDTO<Long>> {

        public void addItemToViewAdapter(T item);

        public void addItemToViewAdapter(T item, int position);

        public void removeItemFromViewAdapter(T item);

    }

}
