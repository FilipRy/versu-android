package com.filip.versu.view.viewmodel;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.filip.versu.R;
import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.abs.AbsBaseEntityDTO;
import com.filip.versu.model.view.abs.AbsBaseViewModel;
import com.filip.versu.model.view.abs.AbsPostViewModel;
import com.filip.versu.service.cache.ILocalCacheService;
import com.filip.versu.service.factory.LocalCacheFactory;
import com.filip.versu.service.helper.Page;
import com.filip.versu.view.viewmodel.callback.IRefreshablePageableViewModel;
import com.filip.versu.view.viewmodel.callback.IRefreshablePageableViewModel.IRefreshablePageableViewCallback;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;

import java.util.List;


public abstract class AbsRefreshablePageableViewModel<C extends IRefreshablePageableViewCallback, K extends AbsBaseViewModel>
        extends AbsPegContentViewModel<C> implements IRefreshablePageableViewModel<K> {

    public static final String TAG = AbsRefreshablePageableViewModel.class.getSimpleName();

    protected DualCache<Long> lastTimeRefreshCache = LocalCacheFactory.createForLastTimeRefreshTimestamp();


    private AbsInternalStorageLoaderTask internalStorageLoaderTask;
    private AbsBackendLoaderTask backendLoaderTask;

    protected K lastLoadedContent;
    protected long lastLoadedItemsLastRefresh;
    protected String lastErrorMessage;


    @Override
    public void requestItemsFromInternalStorage(Context applicationContext) {
        if (lastErrorMessage != null) {
            getView().showErrorMessage(lastErrorMessage);
        }

        if (backendLoaderTask != null) { // posts are still loading from backend
            if (backendLoaderTask.getPage().getLastLoadedID() != null) {
                getView().showProgressBarAtBottom(true);
            } else {
                getView().showSmallProgressBar(true);
            }
        }

        if (lastLoadedContent != null) {
            if (shouldRefreshRAMCache()) {
                requestItemsFromBackend(new Page<Long>(PAGE_SIZE, null));
                return;
            }
            addLoadedContentToView();
            return;
        } else {
            getView().hideErrorMessage();
            if (internalStorageLoaderTask != null) {
                getView().showBigProgressBar(true);
            } else {
                getView().showBigProgressBar(true);

                internalStorageLoaderTask = createInternalStorageLoaderTask(applicationContext);
                /*
                 * "AsyncTask loading data from storage" is started as parallel
				 * task to avoid the waiting time before finishing previous
				 * AsyncTasks. Backend AsyncTask are not run in parallel to
				 * avoid concurrency problems.
				 */
                internalStorageLoaderTask.executeOnExecutor(AbsAsynchronTask.THREAD_POOL_EXECUTOR,
                        (Void) null);
            }
        }
    }

    @Override
    public boolean requestItemsFromBackend() {
        return requestItemsFromBackend(new Page<Long>(PAGE_SIZE, null));
    }

    public boolean requestNextPageFromBackend() {
        if (lastLoadedContent != null) {

            AbsBaseEntityDTO<Long> lastItem = getLastItemOnPage();

            if (lastItem != null) {
                return requestItemsFromBackend(new Page<>(IRefreshablePageableViewModel.PAGE_SIZE, lastItem.getId()));
            }


        }
        return false;
    }

    @Override
    public boolean requestItemsFromBackend(Page<Long> page) {

        if (getView() == null) {
            return false;
        }

        if (page.getLastLoadedID() != null) {
            if (lastLoadedContent == null) {
                return false;
            }
        }

        if (internalStorageLoaderTask != null) {
            getView().showBigProgressBar(true);
            return false;
        }

        if (backendLoaderTask != null) {
            if (backendLoaderTask.getPage().getLastLoadedID() != null) {
                getView().showProgressBarAtBottom(true);
            } else {
                getView().showSmallProgressBar(true);
            }
            return false;
        }


        if (page.getLastLoadedID() != null) {
            getView().showProgressBarAtBottom(true);
        } else {
            getView().showSmallProgressBar(true);
        }
        getView().hideErrorMessage();


        backendLoaderTask = createBackendLoaderTask(page);
        backendLoaderTask.execute((Void) (null));

        return true;
    }

    protected AbsInternalStorageLoaderTask createInternalStorageLoaderTask(Context applicationContext) {
        return new InternalStorageLoaderTask(applicationContext);
    }

    protected AbsBackendLoaderTask createBackendLoaderTask(Page<Long> page) {
        return new BackendLoaderTask(page);
    }

    public void addLoadedContentToView() {
        if (getView() != null) {
            getView().addLoadedItemsToViews(lastLoadedContent);
        }
    }

    public K retrieveItemsFromInternalStorage() {
        Object object = null;
        try {//TODO solve this: this is a hack to avoid "Invalid type id 'com.filip.versu.model.view.PostFeedViewModel' (for id type 'Id.class'): Class com.filip.versu.model.view.PostFeedViewModel is not assignable to com.filip.versu.model.view.UserProfileFeedViewModel"
            object = getLocalCache().get(getInternalStorageKey());
        } catch (IllegalArgumentException e) {
            Log.i(TAG, e.getMessage());
        }
        K locallyCachedItem = (K) object;
        if (locallyCachedItem == null) {
            return createInstance();
        }
        fixReferencesOnItemsLoad(locallyCachedItem);
        return locallyCachedItem;
    }

    protected void fixReferencesOnItemsLoad(K loadedItem) {
        loadedItem.fixReferences();
    }


    public void persistItemsToInternalStorage(K item) {
        new SaveItemsToInternalStorage(item).execute((Void) null);
    }

    public long getLastTimeRefreshOfCache() {
        Long lastTimeRefresh = lastTimeRefreshCache.get(getLastTimeRefreshInternalStorageKey());
        if (lastTimeRefresh == null) {
            lastTimeRefresh = 0l;
        }
        return lastTimeRefresh;
    }

    /**
     * @return true if the lastLoadedItem are too old and should be refreshed.
     */
    protected boolean shouldRefreshRAMCache() {
        boolean shouldRefreshRAM = lastLoadedItemsLastRefresh < getGlobalItemVersionTimestamp();
        if (shouldRefreshRAM) {
            Log.d(this.getClass().getSimpleName(), "The RAM cached last loaded items are too old, refreshing");
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return
     */
    protected long getGlobalItemVersionTimestamp() {
        return lastLoadedItemsLastRefresh;
    }

    /**
     * @return true if the cached item on the disk are too "old" and should be refreshed, otherwise returns true.
     */
    protected boolean shouldRefreshDiskCache() {
        long lastTimeCacheRefresh = getLastTimeRefreshOfCache();
        long timeDiff = System.currentTimeMillis() - lastTimeCacheRefresh;
        timeDiff = timeDiff / (60 * 1000);

        boolean isCacheOld = timeDiff > DISK_CACHE_MAX_LIFE_TIME;

        if (isCacheOld) {
            Log.d(this.getClass().getSimpleName(), "The items loaded from internal strg are too old, refreshing");
            return true;
        }

        if (getGlobalItemVersionTimestamp() > lastTimeCacheRefresh) {
            Log.d(this.getClass().getSimpleName(), "The items loaded from internal strg are too old, refreshing");
            return true;
        }

        return false;
    }

    //TODO this is only a helper method, find better solution
    public abstract K createInstance();

    /**
     * @return the key which is used to persist/obtain item to/from internal storage.
     */
    public abstract String getInternalStorageKey();

    /**
     * @return the key which is used to persist/obtain last time of refresh of the according internal storage.
     */
    public abstract String getLastTimeRefreshInternalStorageKey();

    public abstract ILocalCacheService<K> getLocalCache();

    public abstract K retrieveItemsFromBackend(Page<Long> page) throws ServiceException;

    public void onItemsSuccessLoadedFromInternalStorage(K loadedItem) {
        if(loadedItem.getSize() != 0) {
            addLoadedContentToView();
        }
    }


    public void onItemsSuccessLoadedFromBackend(K loadedItem, Page<Long> page) {
        addLoadedContentToView();
        lastLoadedItemsLastRefresh = System.currentTimeMillis();
        persistItemsToInternalStorage(loadedItem);
    }


    protected String getCustomNothingToShowMessage() {
        Fragment fragment = (Fragment) getView();
        if (fragment != null) {
            return fragment.getString(R.string.nothing_to_show);
        }
        return "";
    }

    protected abstract class AbsInternalStorageLoaderTask extends AbsAsynchronTask<K> {

        private Context context;

        AbsInternalStorageLoaderTask(Context context) {
            this.context = context;
        }

        protected abstract void onItemsSuccessLoaded(K loadedItem);

        @Override
        protected K asynchronOperation() throws ServiceException {
            return retrieveItemsFromInternalStorage();
        }

        @Override
        protected void onPostExecuteSuccess(K item) {
            lastErrorMessage = null;
            lastLoadedContent = item;

            if (lastLoadedContent == null) {
                lastLoadedContent = createInstance();
            }

            if (getView() != null) {
                getView().showBigProgressBar(false);
                getView().clearView();

            }

            if (lastLoadedContent.getSize() == 0) {
                lastErrorMessage = getCustomNothingToShowMessage();
                if (getView() != null) {
                    getView().showErrorMessage(lastErrorMessage);
                }
            }

            onItemsSuccessLoaded(lastLoadedContent);

            internalStorageLoaderTask = null;

            if (shouldRefreshDiskCache()) {
                requestItemsFromBackend(new Page<Long>(PAGE_SIZE, null));
            }
        }

        @Override
        protected void onPostExecuteError(K item) {
            super.onPostExecuteError(item);
        }

        //this should not happen
        @Override
        protected void displayErrorMessage(String errorMsg) {
            if (getView() != null) {
                lastErrorMessage = errorMsg;
                getView().showErrorMessage(errorMsg);
            }
        }

        @Override
        protected void onCancelled() {
            if (getView() != null) {
                getView().showBigProgressBar(false);
            }
            internalStorageLoaderTask = null;
        }

    }

    protected void addNextPageItems(K nextPage) {
        lastLoadedContent.getPageableContent().addAll(nextPage.getPageableContent());
    }

    protected AbsBaseEntityDTO<Long> getLastItemOnPage() {
        List lastContent = lastLoadedContent.getPageableContent();
        int size = lastContent.size();


        if (size == 0) {
            return null;
        }

        AbsBaseEntityDTO<Long> lastItem;
        try {
            lastItem = (AbsBaseEntityDTO<Long>) lastContent.get(size - 1);
        } catch (ClassCastException e) {
            lastItem = null;
        }


        return lastItem;
    }

    class InternalStorageLoaderTask extends AbsInternalStorageLoaderTask {

        InternalStorageLoaderTask(Context context) {
            super(context);
        }

        @Override
        protected void onItemsSuccessLoaded(K loadedItem) {
            onItemsSuccessLoadedFromInternalStorage(loadedItem);
        }

    }

    protected abstract class AbsBackendLoaderTask extends AbsAsynchronTask<K> {

        private Page<Long> page;

        AbsBackendLoaderTask(Page<Long> page) {
            this.page = page;
        }

        public Page<Long> getPage() {
            return page;
        }

        abstract K retrieveItemsFromBackendTask(Page<Long> page) throws ServiceException;

        abstract void onItemsSuccessLoaded(K loadedItem);

        @Override
        protected K asynchronOperation() throws ServiceException {
            return retrieveItemsFromBackendTask(page);
        }

        @Override
        protected void onPostExecuteSuccess(K loadedItem) {
            lastErrorMessage = null;
            if (getView() != null) {
                getView().clearView();
            }
            if (loadedItem == null) {
                loadedItem = createInstance();
            }

            if (page.getLastLoadedID() != null) {
                if (lastLoadedContent == null) {
                    lastLoadedContent = createInstance();
                }
                addNextPageItems(loadedItem);
            } else {
                lastLoadedContent = loadedItem;


                if (lastLoadedContent == null || lastLoadedContent.getSize() == 0) {
                    errorMsg = getCustomNothingToShowMessage();
                    onPostExecuteError(loadedItem);
                }

            }
            onItemsSuccessLoaded(lastLoadedContent);


        }

        @Override
        protected void onPostExecuteError(K item) {
            lastErrorMessage = errorMsg;
            super.onPostExecuteError(item);
        }

        @Override
        protected void displayErrorMessage(String errorMsg) {
            if (getView() != null) {
                getView().showErrorMessage(lastErrorMessage);
            }
        }

        @Override
        protected void onPostExecute(K loadedItem) {
            super.onPostExecute(loadedItem);
            cleanUp();
        }

        @Override
        protected void onCancelled() {
            cleanUp();
        }

        private void cleanUp() {
            if (getView() != null) {
                getView().showSmallProgressBar(false);
                getView().showProgressBarAtBottom(false);
            }
            backendLoaderTask = null;
        }

    }

    protected class BackendLoaderTask extends AbsBackendLoaderTask {

        BackendLoaderTask(Page<Long> page) {
            super(page);
        }

        @Override
        K retrieveItemsFromBackendTask(Page<Long> page) throws ServiceException {
            return retrieveItemsFromBackend(page);
        }

        @Override
        void onItemsSuccessLoaded(K loadedItem) {
            onItemsSuccessLoadedFromBackend(loadedItem, getPage());
        }

    }

    private class SaveItemsToInternalStorage extends AbsAsynchronTask<K> {

        private K item;

        SaveItemsToInternalStorage(K item) {
            this.item = item;
        }

        @Override
        protected K asynchronOperation() throws ServiceException {
            K itemCopy = (K) item.removeReferencesBeforeSerialization();

            //TODO find better solution
            //only first 5 post are being saved to cache to save memory
            if (itemCopy instanceof AbsPostViewModel) {
                AbsPostViewModel postViewModel = (AbsPostViewModel) itemCopy;

                if (postViewModel.feedItems.size() > 5) {
                    while (postViewModel.feedItems.size() > 5) {
                        postViewModel.feedItems.remove(postViewModel.feedItems.size() - 1);
                    }
                }
            }

            getLocalCache().put(itemCopy, getInternalStorageKey());


            long nowTimestamp = System.currentTimeMillis();
            lastTimeRefreshCache.put(getLastTimeRefreshInternalStorageKey(), nowTimestamp);
            return itemCopy;
        }


        //this should not happen
        @Override
        protected void displayErrorMessage(String errorMsg) {
            if (getView() != null) {
                lastErrorMessage = errorMsg;
                getView().showErrorMessage(errorMsg);
            }
        }

    }

}
