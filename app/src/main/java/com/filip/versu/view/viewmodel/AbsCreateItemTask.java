package com.filip.versu.view.viewmodel;

import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.abs.BaseDTO;



abstract public class AbsCreateItemTask<L extends BaseDTO<Long>> extends AbsAsynchronTask<L> {

    protected L item;

    public AbsCreateItemTask(L item) {
        this.item = item;
        updateViewBeforeCreateTask();
    }

    protected abstract void updateViewBeforeCreateTask();

    protected abstract void rollbackCreateOperation();

    protected abstract void onCreateOperationSuccess(L createdItem);

    public void execute() {
        super.execute();
    }

    protected abstract L sendCreateItemRequest(L item) throws ServiceException;

    @Override
    protected L asynchronOperation() throws ServiceException {
        return sendCreateItemRequest(item);
    }

    @Override
    protected void onPostExecuteSuccess(L item) {
        onCreateOperationSuccess(item);
    }

    @Override
    protected void onPostExecuteError(L item) {
        super.onPostExecuteError(item);
        rollbackCreateOperation();
    }

}
