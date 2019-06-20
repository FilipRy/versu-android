package com.filip.versu.view.viewmodel;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.filip.versu.model.DevicePhoto;
import com.filip.versu.service.IPostService;
import com.filip.versu.service.impl.PostService;
import com.filip.versu.view.CreatePostActivity;
import com.filip.versu.view.fragment.PhotoSelectorFragment;
import com.filip.versu.view.viewmodel.callback.IPhotoSelectorViewModel;
import com.filip.versu.view.viewmodel.callback.IPhotoSelectorViewModel.IPhotoSelectorViewModelCallback;

import java.util.ArrayList;
import java.util.List;


public class PhotoSelectorViewModel extends AbsPegContentViewModel<IPhotoSelectorViewModelCallback> implements IPhotoSelectorViewModel {

    private IPostService shoppingItemService = PostService.instance();

    private Context context;
    private List<DevicePhoto> lastLoadedItems;
    private DeviceImageURIsLoader deviceImageURIsLoader;

    @Override
    public void requestLoadingAllDeviceImages(Context context) {
        this.context = context;
        if (lastLoadedItems != null) {
            if (getView() != null) {
                getView().setDevicePhotosViewItems(lastLoadedItems);
            }
            return;
        }

        if (getView() != null) {
            getView().showLoadingPhotosProgress(true);
        }
        if (deviceImageURIsLoader != null) {
            return;
        }

        deviceImageURIsLoader = new DeviceImageURIsLoader();
        deviceImageURIsLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void setPostDescription(String description) {
        PhotoSelectorFragment photoSelectorFragment = (PhotoSelectorFragment) getView();
        CreatePostActivity createPostActivity = (CreatePostActivity) photoSelectorFragment.getActivity();
        createPostActivity.getViewModel().getSimplePostToCreate().description = description;
    }

    class DeviceImageURIsLoader extends AsyncTask<Void, DevicePhoto, Void> {

        @Override
        protected void onProgressUpdate(DevicePhoto... values) {
            super.onProgressUpdate(values);

            for (DevicePhoto devicePhoto : values) {
                if (lastLoadedItems == null) {
                    lastLoadedItems = new ArrayList<>();
                }
                lastLoadedItems.add(devicePhoto);

                if (getView() != null) {
                    getView().addItemToView(devicePhoto);
                }
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            Uri uri;
            Cursor cursor;
            int column_index_data;

            String absolutePathOfImage = null;

            final String orderBy = MediaStore.Images.Media.DATE_ADDED;

            //get all images from external storage

            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME};

            cursor = context.getContentResolver().query(uri, projection, null,
                    null, orderBy + " DESC");

            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);

                DevicePhoto photo = new DevicePhoto();
                photo.path = absolutePathOfImage;
                publishProgress(photo);
                //Escape early if cancel() is called
                if (isCancelled()) {
                    break;
                }

            }

            // Get all Internal storage images

            uri = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;

            cursor = context.getContentResolver().query(uri, projection, null,
                    null, orderBy + " DESC");

            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);

                DevicePhoto photo = new DevicePhoto();
                photo.path = absolutePathOfImage;
                publishProgress(photo);
                //Escape early if cancel() is called
                if (isCancelled()) {
                    break;
                }

            }

            cursor.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (getView() != null) {
                getView().showLoadingPhotosProgress(false);
            }
            deviceImageURIsLoader = null;
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            if (getView() != null) {
                getView().showLoadingPhotosProgress(false);
            }
            deviceImageURIsLoader = null;
        }
    }
}
