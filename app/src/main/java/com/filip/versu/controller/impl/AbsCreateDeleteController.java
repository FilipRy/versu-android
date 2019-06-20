package com.filip.versu.controller.impl;



import android.os.AsyncTask;
import android.util.Log;

import com.filip.versu.controller.ICreateDeleteController;
import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.abs.BaseDTO;
import com.filip.versu.view.viewmodel.AbsCreateItemTask;


import java.util.List;

public abstract class AbsCreateDeleteController<T extends BaseDTO<Long>> implements ICreateDeleteController<T> {

    public static final String TAG = AbsCreateDeleteController.class.getSimpleName();

    private AbsCreateItemTask createItemTask;
    protected AbsDeleteItemTask deleteItemTask;

    protected ICreateDeleteControllerCallback<T> createDeleteControllerCallback;


    protected List<T> lastLoadedItems;

    /**
     * @param createDeleteControllerCallback
     */
    public AbsCreateDeleteController(ICreateDeleteControllerCallback<T> createDeleteControllerCallback) {
        this.createDeleteControllerCallback = createDeleteControllerCallback;
    }

    /**
     * This operation add the item only to memory-cache and to view.
     *
     * @param item
     */
    public void addItem(T item) {
        if(lastLoadedItems != null) {
            lastLoadedItems.add(item);
        }
        createDeleteControllerCallback.addItemToViewAdapter(item);
    }

    protected void addItemToPosition(T item, int position) {
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
    public void removeItem(T item) {
        if(lastLoadedItems != null) {
            lastLoadedItems.remove(item);
        }
        createDeleteControllerCallback.removeItemFromViewAdapter(item);
    }


    @Override
    public void executeCreateItemTask(T item, List<T> lastLoadedItems) {
        this.lastLoadedItems = lastLoadedItems;
        createItemTask = new CreateItemTask(item);
        createItemTask.execute();
    }

    @Override
    public void executeDeleteItemTask(T item, int position, List<T> lastLoadedItems) {
        if (item.getId() == null) {
            //TODO show error, item cannot be deleted now.
        }
        this.lastLoadedItems = lastLoadedItems;
        deleteItemTask = new DeleteItemTask(item, position);
        deleteItemTask.execute((Void) null);
    }



    /**
     * This method calls only the corresponding service create method.
     *
     * @param item
     * @return
     * @throws ServiceException
     */
    public abstract T createCreateItemRequest(T item) throws ServiceException;

    /**
     * This method calls only the corresponding service delete method.
     *
     * @param item
     * @return
     * @throws ServiceException
     */
    public abstract boolean createDeleteItemRequest(T item) throws ServiceException;


    public void onCreateOperationSuccess(T createdItem) {
        createDeleteControllerCallback.updateViewModelVersionNumber(createdItem);
        createDeleteControllerCallback.onCreateOperationSuccess(createdItem);
    }


    public void onDeleteOperationSuccess(T deletedItem) {
        createDeleteControllerCallback.updateViewModelVersionNumber(deletedItem);
        createDeleteControllerCallback.onDeleteOperationSuccess(deletedItem);
    }


    protected class CreateItemTask extends AbsCreateItemTask<T> {

        public CreateItemTask(T item) {
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
        protected void onCreateOperationSuccess(T i) {
            AbsCreateDeleteController.this.onCreateOperationSuccess(i);
        }

        @Override
        protected T sendCreateItemRequest(T item) throws ServiceException {
            return createCreateItemRequest(item);
        }

        @Override
        protected void displayErrorMessage(String errorMsg) {
            createDeleteControllerCallback.displayCreateDeleteControllerError(errorMsg);
        }
    }


    private abstract class AbsDeleteItemTask extends AsyncTask<Void, Void, Boolean> {

        protected T item;
        /**
         * This is a position of item in the RecyclerView.
         */
        protected int position;

        private String errorMsg;

        public AbsDeleteItemTask(T item, int position) {
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

        public DeleteItemTask(T item, int position) {
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
            AbsCreateDeleteController.this.onDeleteOperationSuccess(item);
        }
    }


}
