package com.filip.versu.controller.impl;

import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.Searchable;
import com.filip.versu.view.viewmodel.AbsCreateItemTask;

import java.util.List;


public class SearchHistoryCreateDeleteController extends AbsCreateDeleteController<Searchable> {


    private CreateSearchHistoryItemTask createItemTask;

    @Override
    public void executeCreateItemTask(Searchable item, List<Searchable> lastLoadedItems) {
        this.lastLoadedItems = lastLoadedItems;
        createItemTask = new CreateSearchHistoryItemTask(item);
        createItemTask.execute();
    }

    /**
     * @param createDeleteControllerCallback
     */
    public SearchHistoryCreateDeleteController(ICreateDeleteControllerCallback<Searchable> createDeleteControllerCallback) {
        super(createDeleteControllerCallback);
    }

    @Override
    public Searchable createCreateItemRequest(Searchable item) throws ServiceException {
        return item;
    }

    @Override
    public boolean createDeleteItemRequest(Searchable item) throws ServiceException {
        return false;
    }


    private class CreateSearchHistoryItemTask extends AbsCreateItemTask<Searchable> {

        public CreateSearchHistoryItemTask(Searchable item) {
            super(item);
        }
        @Override
        protected void updateViewBeforeCreateTask() {}
        @Override
        protected void rollbackCreateOperation() {}

        @Override
        protected void onCreateOperationSuccess(Searchable createdItem) {
            SearchHistoryCreateDeleteController.this.onCreateOperationSuccess(createdItem);
        }

        @Override
        protected Searchable sendCreateItemRequest(Searchable item) throws ServiceException {
            return createCreateItemRequest(item);
        }

        //this should not happen
        @Override
        protected void displayErrorMessage(String errorMsg) {
            createDeleteControllerCallback.displayCreateDeleteControllerError(errorMsg);
        }
    }

}
