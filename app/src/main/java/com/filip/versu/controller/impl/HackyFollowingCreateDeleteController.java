package com.filip.versu.controller.impl;

import android.os.AsyncTask;
import android.util.Log;

import com.filip.versu.controller.IFollowingCreateDeleteController.IFollowingCreateDeleteControllerCallback;
import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.FollowingDTO;
import com.filip.versu.service.IFollowingService;
import com.filip.versu.service.impl.FollowingService;
import com.filip.versu.view.viewmodel.AbsCreateItemTask;

import java.util.List;


/**
 * TODO find better solution this is for IFollowingCreateDeleteController
 */
public class HackyFollowingCreateDeleteController {

    public static final String TAG = HackyFollowingCreateDeleteController.class.getSimpleName();

    private IFollowingService followingService = FollowingService.instance();


    private AbsCreateItemTask createItemTask;
    protected AbsDeleteItemTask deleteItemTask;

    protected IFollowingCreateDeleteControllerCallback createDeleteControllerCallback;


    protected List<FollowingDTO> lastLoadedItems;

    /**
     * @param createDeleteControllerCallback
     */
    public HackyFollowingCreateDeleteController(IFollowingCreateDeleteControllerCallback createDeleteControllerCallback) {
        this.createDeleteControllerCallback = createDeleteControllerCallback;
    }


    public FollowingDTO createCreateItemRequest(FollowingDTO item) throws ServiceException {
        return followingService.create(item);
    }


    public boolean createDeleteItemRequest(FollowingDTO item) throws ServiceException {
        return followingService.delete(item.getId());
    }

    /**
     * This operation add the item only to memory-cache and to view.
     *
     * @param item
     */
    public void addItem(FollowingDTO item) {
        if (lastLoadedItems != null) {
            lastLoadedItems.add(item);
        }
        createDeleteControllerCallback.addItemToViewAdapter(item);
    }

    protected void addItemToPosition(FollowingDTO item, int position) {
        if (lastLoadedItems != null) {
            lastLoadedItems.add(position, item);
        }
        createDeleteControllerCallback.addItemToViewAdapter(item, position);
    }

    /**
     * Removes the item only from memory-cache and view.
     *
     * @param item
     */
    public void removeItem(FollowingDTO item) {
        if (lastLoadedItems != null) {
            lastLoadedItems.remove(item);
        }
        createDeleteControllerCallback.removeItemFromViewAdapter(item);
    }


    public void executeCreateItemTask(FollowingDTO item, List<FollowingDTO> lastLoadedItems) {
        this.lastLoadedItems = lastLoadedItems;
        createItemTask = new CreateItemTask(item);
        createItemTask.execute();
    }

    public void executeDeleteItemTask(FollowingDTO item, int position, List<FollowingDTO> lastLoadedItems) {
        if (item.getId() == null) {
            //TODO show error, item cannot be deleted now.
        }
        this.lastLoadedItems = lastLoadedItems;
        deleteItemTask = new DeleteItemTask(item, position);
        deleteItemTask.execute((Void) null);
    }


    public void onCreateOperationSuccess(FollowingDTO createdItem) {
        createDeleteControllerCallback.updateViewModelVersionNumber(createdItem);
        createDeleteControllerCallback.onCreateOperationSuccess(createdItem);
    }


    public void onDeleteOperationSuccess(FollowingDTO deletedItem) {
        createDeleteControllerCallback.updateViewModelVersionNumber(deletedItem);
        createDeleteControllerCallback.onDeleteOperationSuccess(deletedItem);
    }


    protected class CreateItemTask extends AbsCreateItemTask<FollowingDTO> {

        public CreateItemTask(FollowingDTO item) {
            super(item);
        }

        @Override
        protected void updateViewBeforeCreateTask() {
            //TODO createDeleteControllerCallback.hideErrorMessage();
            addItem(item);
        }

        @Override
        protected void rollbackCreateOperation() {
            removeItem(item);
        }

        @Override
        protected void onCreateOperationSuccess(FollowingDTO i) {
            HackyFollowingCreateDeleteController.this.onCreateOperationSuccess(i);
        }

        @Override
        protected FollowingDTO sendCreateItemRequest(FollowingDTO item) throws ServiceException {
            return createCreateItemRequest(item);
        }

        @Override
        protected void displayErrorMessage(String errorMsg) {
            createDeleteControllerCallback.displayCreateDeleteControllerError(errorMsg);
        }
    }


    private abstract class AbsDeleteItemTask extends AsyncTask<Void, Void, Boolean> {

        protected FollowingDTO item;
        /**
         * This is a position of item in the RecyclerView.
         */
        protected int position;

        private String errorMsg;

        public AbsDeleteItemTask(FollowingDTO item, int position) {
            this.item = item;
            this.position = position;
            updateViewBeforeDeleteTask();
        }

        protected abstract void updateViewBeforeDeleteTask();

        protected abstract void rollbackDeleteOperation();

        protected abstract void onDeleteOperationSuccess();

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return createDeleteItemRequest(item);
            } catch (ServiceException e) {
                Log.i(TAG, e.getMessage());
                errorMsg = e.getMessage();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                onDeleteOperationSuccess();
            } else {
                /**
                 * reverting state = adding the deleted item back to recycler view
                 */
                rollbackDeleteOperation();
            }
            deleteItemTask = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            deleteItemTask = null;
        }
    }

    private class DeleteItemTask extends AbsDeleteItemTask {

        public DeleteItemTask(FollowingDTO item, int position) {
            super(item, position);
        }

        @Override
        protected void updateViewBeforeDeleteTask() {
            removeItem(item);
        }

        @Override
        protected void rollbackDeleteOperation() {
            addItemToPosition(item, position);
        }

        @Override
        protected void onDeleteOperationSuccess() {
            HackyFollowingCreateDeleteController.this.onDeleteOperationSuccess(item);
        }
    }

}
