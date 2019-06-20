package com.filip.versu.controller;

import com.filip.versu.model.dto.FollowingDTO;

import java.util.List;

/**
 * This interface is a workaround, s.t. CreateDeleteController<PostDTO> and
 * CreateDeleteController<FollowingDTO> (FollowingCreateDeleteController) can be both used in UserProfileViewModel.
 */
public interface IFollowingCreateDeleteController {

    /**
     * Creates new item (in server + in cache).
     *
     * @param item
     * @param lastLoadedItems - list of lastLoadedContent items == in lastLoadedItems @item should be added after it is created at backend, can be null
     */
    public void executeCreateItemTask(FollowingDTO item, List<FollowingDTO> lastLoadedItems);

    /**
     * Deletes the item from caches + from server.
     *
     * @param item
     * @param lastLoadedItems - list of lastLoadedContent items == in lastLoadedItems @item should be removed after it is deleted at backend, can be null
     */
    public void executeDeleteItemTask(FollowingDTO item, int position, List<FollowingDTO> lastLoadedItems);

    public interface IFollowingCreateDeleteControllerCallback extends IFollowingCreateDeleteViewCallback {

        void onCreateOperationSuccess(FollowingDTO createdItem);

        void onDeleteOperationSuccess(FollowingDTO deletedItem);

        /**
         * Callback method for incrementing view model + global version code of some type.
         * @param updatedItem Necessary if one ViewModel implments multiple different ICreateDeleteControllers -> they are different only in @updatedItem type.
         */
        void updateViewModelVersionNumber(FollowingDTO updatedItem);

        void displayCreateDeleteControllerError(String errorMsg);


    }

    public interface IFollowingCreateDeleteViewCallback {

        public void addItemToViewAdapter(FollowingDTO item);

        public void addItemToViewAdapter(FollowingDTO item, int position);

        public void removeItemFromViewAdapter(FollowingDTO item);

    }

}
