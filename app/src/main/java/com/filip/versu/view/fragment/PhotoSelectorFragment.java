package com.filip.versu.view.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.filip.versu.R;
import com.filip.versu.model.DevicePhoto;
import com.filip.versu.view.adapter.DevicePhotoRecyclerViewAdapter;
import com.filip.versu.view.viewmodel.PhotoSelectorViewModel;
import com.filip.versu.view.viewmodel.callback.IPhotoSelectorViewModel.IPhotoSelectorViewModelCallback;

import java.util.List;


public class PhotoSelectorFragment extends AbsPegContentFragment<IPhotoSelectorViewModelCallback, PhotoSelectorViewModel> implements IPhotoSelectorViewModelCallback {

    public static final String TAG = PhotoSelectorFragment.class.getSimpleName();

    public static final int MY_REQUEST_READ_EXTERNAL_STORAGE = 47;

    private RecyclerView devicePhotosView;
    private ProgressBar progressBar;
    private DevicePhotoRecyclerViewAdapter devicePhotoRecyclerViewAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo_selector, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        devicePhotosView = (RecyclerView) view.findViewById(R.id.recyclerViewDevicePhotos);
        progressBar = (ProgressBar) view.findViewById(R.id.load_progress);

        devicePhotosView.setLayoutManager(new GridLayoutManager(getActivity(), 4));

        setModelView(this);

        //devicePhotoRecyclerViewAdapter = new DevicePhotoRecyclerViewAdapter(new ArrayList<DevicePhoto>(), getActivity(), true, getActivity(), null);
        devicePhotosView.setAdapter(devicePhotoRecyclerViewAdapter);

        checkReadExternalStoragePermission();
    }


    private void checkReadExternalStoragePermission() {


        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(getActivity(), R.string.allow_read_photos_explanation, Toast.LENGTH_LONG).show();
            }


            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_REQUEST_READ_EXTERNAL_STORAGE);

        } else {
            getViewModel().requestLoadingAllDeviceImages(getActivity());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getViewModel().requestLoadingAllDeviceImages(getActivity());
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), R.string.allow_read_photos, Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Nullable
    @Override
    public Class<PhotoSelectorViewModel> getViewModelClass() {
        return PhotoSelectorViewModel.class;
    }

    @Override
    public void addItemToView(DevicePhoto devicePhoto) {
        devicePhotoRecyclerViewAdapter.addItem(devicePhoto);
    }

    @Override
    public void setDevicePhotosViewItems(List<DevicePhoto> devicePhotos) {
        devicePhotoRecyclerViewAdapter.clear();
        devicePhotoRecyclerViewAdapter.addAllItems(devicePhotos);
    }

    @Override
    public void showLoadingPhotosProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            devicePhotosView.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            devicePhotosView.setVisibility(View.VISIBLE);
        }
    }


}
