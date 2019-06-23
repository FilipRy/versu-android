package com.filip.versu.view.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.filip.versu.R;
import com.filip.versu.model.DevicePhoto;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.service.helper.PhotoSize;
import com.filip.versu.service.impl.UserSession;
import com.filip.versu.view.CreatePostActivity;
import com.filip.versu.view.adapter.DevicePhotoRecyclerViewAdapter;
import com.filip.versu.view.camera.ImageUtility;
import com.filip.versu.view.custom.PostVSDivider;
import com.filip.versu.view.viewmodel.CreatePostPhotosViewModel;
import com.filip.versu.view.viewmodel.callback.ICreatePostPhotosViewModel.ICreatePostPhotosViewModelCallback;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class CreatePostPhotosFragment
        extends AbsPegContentFragment<ICreatePostPhotosViewModelCallback, CreatePostPhotosViewModel>
        implements ICreatePostPhotosViewModelCallback {

    public static final String TAG = CreatePostPhotosFragment.class.getSimpleName();

    public static final int MY_REQUEST_PERMISSIONS = 47;


    private static final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

//    private EventBus eventBus = EventBus.getDefault();

    private SlidingUpPanelLayout slidingUpPanelLayout;

    private RecyclerView postPhotosView;
    private RecyclerView devicePhotosView;

    private ImageView hideGalleryButton;
    private ProgressBar progressBar;

    private DevicePhotoRecyclerViewAdapter postPhotosRecyclerViewAdapter;
    private DevicePhotoRecyclerViewAdapter devicePhotoRecyclerViewAdapter;

    /**
     * this is post which is beeing created
     */
    private PostDTO postDTO;

    public static final String POST_KEY = "POST_KEY";


    public static CreatePostPhotosFragment instance(PostDTO postDTO) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(POST_KEY, postDTO);
        CreatePostPhotosFragment fragment = new CreatePostPhotosFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_post_photos, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postDTO = (PostDTO) getArguments().getSerializable(POST_KEY);

        setModelView(this);
        getViewModel().setDependencies(postDTO);

        postPhotosView = (RecyclerView) view.findViewById(R.id.recyclerViewPost);
        devicePhotosView = (RecyclerView) view.findViewById(R.id.recyclerViewDevicePhotos);
        slidingUpPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.slidingUpPanel);
        hideGalleryButton = (ImageView) view.findViewById(R.id.hideGalleryButton);

        progressBar = (ProgressBar) view.findViewById(R.id.load_progress);

        devicePhotoRecyclerViewAdapter = new DevicePhotoRecyclerViewAdapter(new ArrayList<DevicePhoto>(), getActivity(), true, this, null, false);
        devicePhotosView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        devicePhotosView.setAdapter(devicePhotoRecyclerViewAdapter);

        postPhotosView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        postPhotosRecyclerViewAdapter = new DevicePhotoRecyclerViewAdapter(new ArrayList<DevicePhoto>(), getActivity(), false, this, postDTO, false);
        postPhotosView.addItemDecoration(new PostVSDivider(getActivity()));
        postPhotosView.setAdapter(postPhotosRecyclerViewAdapter);


        hideGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });


        checkReadAndWriteExternalStoragePermission();

        /**
         * adding photos to recycler view, if returning to this fragment
         */
        if (postDTO.devicePhotos.size() > 0) {
            postPhotosRecyclerViewAdapter.addAllItems(postDTO.devicePhotos);
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            lockExpandedGallery();
            displayTutorialOverlay();
        }


    }


    private void lockExpandedGallery() {
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        hideGalleryButton.setVisibility(View.INVISIBLE);
    }

    private void unlockExpandedGallery() {
        hideGalleryButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
//        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
//        EventBus.getDefault().unregister(this);
        super.onStop();
    }

//    @SuppressWarnings("unused")
//    public void onEventMainThread(PictureSavedEvent event) {
//        if(event.exception == null) {
//            addPhotoToGallery(event.getSavedDevicePhoto());
//        } else {
//        }
//    }

    private void displayTutorialOverlay() {
        if (UserSession.instance().needDisplayTutorial()) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.rootLayout, TutorialFragment.newInstance(getString(R.string.post_create_choose_photos_hint), true), TutorialFragment.TAG).addToBackStack(null).commit();
        }
    }

    private DevicePhoto devicePhotoCropped;

    @Override
    public void cropDevicePhoto(DevicePhoto devicePhoto) {
        CreatePostActivity activity = (CreatePostActivity) getActivity();
        devicePhotoCropped = devicePhoto;
        activity.cropDevicePhoto(devicePhoto);
    }

    public void croppedDevicePhoto(Uri uri) {
        File file = new File(uri.getPath());

        devicePhotoCropped.path = file.getAbsolutePath();

        int photoWidth = PhotoSize.getVotingItemPhotoWidth();
        int photoHeight = PhotoSize.getVotingItemPhotoHeight();

        Bitmap bitmap = ImageUtility.decodeSampledBitmapFromPath(devicePhotoCropped.path, photoHeight, photoWidth);

        devicePhotoCropped.bitmap = bitmap;

        postPhotosRecyclerViewAdapter.notifyDataSetChanged();
    }


    @Override
    public void addPhotoToPost(DevicePhoto devicePhoto) {
        if (postPhotosRecyclerViewAdapter.getItemCount() == 2) {
            return;
        }
        getViewModel().addPhotoToPost(devicePhoto);

        int photoWidth = PhotoSize.getVotingItemPhotoWidth();
        int photoHeight = PhotoSize.getVotingItemPhotoHeight();

        Bitmap bitmap = ImageUtility.decodeSampledBitmapFromPath(devicePhoto.path, photoHeight, photoWidth);

        devicePhoto.bitmap = bitmap;

        postPhotosRecyclerViewAdapter.addItem(devicePhoto);
        if (postPhotosRecyclerViewAdapter.getItemCount() == 2) {
            postPhotosView.smoothScrollToPosition(1);
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        unlockExpandedGallery();
    }


    @Override
    public void removePhotoFromPost(DevicePhoto devicePhoto) {
        getViewModel().removePhotoFromPost(devicePhoto);

        postPhotosRecyclerViewAdapter.removeItem(devicePhoto);


        if (postPhotosRecyclerViewAdapter.getItemCount() == 0) {//all photos are removed
            lockExpandedGallery();
        }

    }

    @Override
    public void addItemToView(DevicePhoto devicePhoto) {
        devicePhotoRecyclerViewAdapter.addItem(devicePhoto);
    }

    @Override
    public void addItemToViewPosition(DevicePhoto devicePhoto, int position) {
        devicePhotoRecyclerViewAdapter.addItemToPosition(devicePhoto, position);
    }

    @Override
    public void setDevicePhotosViewItems(List<DevicePhoto> devicePhotos) {
        devicePhotoRecyclerViewAdapter.clear();
        devicePhotoRecyclerViewAdapter.addAllItems(devicePhotos);
    }


    public void addPhotoToGallery(DevicePhoto devicePhoto) {
        getViewModel().addPhotoToGallery(devicePhoto, 0);
        devicePhotosView.smoothScrollToPosition(0);
    }

    private void checkReadAndWriteExternalStoragePermission() {

        boolean granted = true;
        for (String permission: PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                granted = false;
            }
        }

        if (granted) {
            getViewModel().requestLoadingAllDeviceImages(getActivity());
        } else {
            requestPermissions(PERMISSIONS, MY_REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_PERMISSIONS: {
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

    @Nullable
    @Override
    public Class<CreatePostPhotosViewModel> getViewModelClass() {
        return CreatePostPhotosViewModel.class;
    }

}
