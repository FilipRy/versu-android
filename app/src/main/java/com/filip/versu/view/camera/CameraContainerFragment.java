package com.filip.versu.view.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.filip.versu.R;
import com.filip.versu.view.fragment.AbsPegContentFragment;
import com.filip.versu.view.viewmodel.CameraViewModel;
import com.filip.versu.view.viewmodel.callback.ICameraViewModel;


public class CameraContainerFragment extends AbsPegContentFragment<ICameraViewModel.ICameraViewModelCallback, CameraViewModel> {

    public static final int CAMERA_PERMISSION_REQUEST = 42;
    public static final String TAG = CameraContainerFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera_container, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (hasPermissionToUseCamera()) {
            displayCamera();
        } else {
            requestCameraPermission();
        }

    }

    private void displayCamera() {
        MCameraFragment cameraFragment = new MCameraFragment();
        cameraFragment.prepareCamera(getActivity());

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        //using commitAllowingStateLoss instead of commit here, explanation: http://stackoverflow.com/a/10261449/2205582 (to avoid IllegalStateException: Can not perform this action after onSaveInstanceState)
        fragmentTransaction.add(R.id.cameraContent, cameraFragment, MCameraFragment.TAG).commitAllowingStateLoss();
    }


    private boolean hasPermissionToUseCamera() {
        return (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermission() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            Toast.makeText(getActivity(), R.string.allow_camera, Toast.LENGTH_LONG).show();
        }


        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    displayCamera();
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Toast.makeText(getActivity(), R.string.allow_camera, Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }


    @Nullable
    @Override
    public Class<CameraViewModel> getViewModelClass() {
        return CameraViewModel.class;
    }
}
