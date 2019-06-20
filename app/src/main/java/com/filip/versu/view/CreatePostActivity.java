package com.filip.versu.view;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.filip.versu.R;
import com.filip.versu.model.DevicePhoto;
import com.filip.versu.model.Location;
import com.filip.versu.view.fragment.CreatePostDescriptionFragment;
import com.filip.versu.view.fragment.CreatePostPhotosFragment;
import com.filip.versu.view.fragment.CreatePostPlacesFragment;
import com.filip.versu.view.fragment.CreatePostPossibilitiesFragment;
import com.filip.versu.view.fragment.CreatePostViewerSelectorFragment;
import com.filip.versu.view.fragment.parent.CreatePostPhotosContainerFragment;
import com.filip.versu.view.viewmodel.CreatePostViewModel;
import com.filip.versu.view.viewmodel.callback.ICreatePostViewModel.ICreatePostViewModelCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.inloop.viewmodel.base.ViewModelBaseActivity;
import io.github.rockerhieu.emojiconize.Emojiconize;

public class CreatePostActivity
        extends ViewModelBaseActivity<ICreatePostViewModelCallback, CreatePostViewModel>
        implements ICreatePostViewModelCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = CreatePostActivity.class.getSimpleName();

    public static final int REQUEST_CAMERA_CODE = 47;
    public static final int REQUEST_ACCESS_CAMERA = 42;

    private GoogleApiClient mGoogleApiClient;

    private ImageView leftArrow, rightArrow, cameraBtn;

    private ProgressDialog createItemProgressDialog;

    private CreatePostPhotosFragment createPostPhotosFragment;

    private List<Location> nearbyPlaces = new ArrayList<>();
//    private CreatePostPhotosContainerFragment createPostPhotosContainerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Emojiconize.activity(this).go();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        leftArrow = (ImageView) findViewById(R.id.leftArrow);
        rightArrow = (ImageView) findViewById(R.id.rightArrow);
        cameraBtn = (ImageView) findViewById(R.id.takePhotoBtn);

        leftArrow.setImageResource(R.drawable.arrow_left);
        rightArrow.setImageResource(R.drawable.arrow_right);

        setModelView(this);


        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getViewModel().decrementProgressPointer();
            }
        });

        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getViewModel().incrementProgressPointer();
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestTakePhoto();
            }
        });

        displayPhotoSelector(false);


        buildGoogleApiClient();
    }

    private void requestTakePhoto() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_ACCESS_CAMERA);

        } else {
            accessCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    accessCamera();
                } else {
                    Toast.makeText(this, R.string.allow_camera, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private String photoPath;

    private void accessCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {


            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    getString(R.string.app_name)
            );

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return;
                }
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            File image = new File(
                    mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg"
            );

            if(image == null) {
                return;
            }

            photoPath = image.getAbsolutePath();

            Uri photoUri = Uri.fromFile(image);

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, REQUEST_CAMERA_CODE);
        } else {
            //TODO no camera found
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA_CODE && resultCode == Activity.RESULT_OK) {
            if (createPostPhotosFragment != null) {
                DevicePhoto devicePhoto = new DevicePhoto();
                devicePhoto.path = photoPath;
                createPostPhotosFragment.addPhotoToPost(devicePhoto);
                createPostPhotosFragment.addPhotoToGallery(devicePhoto);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();
                if(createPostPhotosFragment != null) {
                    createPostPhotosFragment.croppedDevicePhoto(resultUri);
                }
            }
        }
    }
    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getViewModel().searchNearbyPlaces(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    public void cropDevicePhoto(DevicePhoto devicePhoto) {
        File file = new File(devicePhoto.path);

        CropImage.activity(Uri.fromFile(file))
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    public void setNearbyPlaces(List<Location> nearbyPlaces) {
        this.nearbyPlaces = nearbyPlaces;
    }

    @Override
    public List<Location> getNearbyPlaces() {
        return nearbyPlaces;
    }

    @Override
    public Class<CreatePostViewModel> getViewModelClass() {
        return CreatePostViewModel.class;
    }


    @Override
    public void showCreatePostProgress(boolean show) {
        if (show) {
            createItemProgressDialog = ProgressDialog.show(this, "", getString(R.string.creating), true);
        } else {
            if (createItemProgressDialog != null) {
                createItemProgressDialog.dismiss();
            }
        }
    }


    @Override
    public void displayCreateMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayCreateMsg(int resID) {
        displayCreateMsg(getString(resID));
    }

    @Override
    public void displayPhotoSelector(boolean isReturning) {

        hideKeyboard();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        int openPagerItem = 0;
        if (isReturning) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, android.R.anim.fade_out);
            openPagerItem = 1;
        } else {
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, android.R.anim.fade_out);
            openPagerItem = 0;
        }

        createPostPhotosFragment = CreatePostPhotosFragment.instance(getViewModel().getSimplePostToCreate());

//        createPostPhotosContainerFragment = CreatePostPhotosContainerFragment.newInstance(getViewModel().getSimplePostToCreate(), openPagerItem);
        fragmentTransaction.replace(R.id.containerFrame, createPostPhotosFragment, CreatePostPhotosContainerFragment.TAG).commit();

        leftArrow.setImageResource(R.drawable.close);
        cameraBtn.setVisibility(View.VISIBLE);
        rightArrow.setImageResource(R.drawable.arrow_right);
    }

    @Override
    public void displayPossibilitiesRequester() {

        hideKeyboard();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        CreatePostPossibilitiesFragment fragment = CreatePostPossibilitiesFragment.instance(getViewModel().getSimplePostToCreate());
        fragmentTransaction.replace(R.id.containerFrame, fragment, CreatePostPossibilitiesFragment.TAG).commit();

        leftArrow.setImageResource(R.drawable.arrow_left);
        cameraBtn.setVisibility(View.GONE);
        rightArrow.setImageResource(R.drawable.arrow_right);
    }

    @Override
    public void displayViewersSelector() {

        hideKeyboard();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        CreatePostViewerSelectorFragment createPostViewerSelectorFragment = CreatePostViewerSelectorFragment.instance(getViewModel().getSimplePostToCreate());
        fragmentTransaction.replace(R.id.containerFrame, createPostViewerSelectorFragment, CreatePostViewerSelectorFragment.TAG).commit();

        leftArrow.setImageResource(R.drawable.arrow_left);
        rightArrow.setImageResource(R.drawable.check);
    }

    @Override
    public void displayPostDescriptionCreator() {

        hideKeyboard();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        CreatePostDescriptionFragment fragment = CreatePostDescriptionFragment.instance(getViewModel().getSimplePostToCreate());
        fragmentTransaction.replace(R.id.containerFrame, fragment, CreatePostDescriptionFragment.TAG).commit();

        leftArrow.setImageResource(R.drawable.arrow_left);
        rightArrow.setImageResource(R.drawable.arrow_right);
    }

    @Override
    public void displayPlacesSelector() {

        hideKeyboard();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        CreatePostPlacesFragment fragment = CreatePostPlacesFragment.newInstance(getViewModel().getSimplePostToCreate());
        fragmentTransaction.replace(R.id.containerFrame, fragment, CreatePostPlacesFragment.TAG).commit();

        leftArrow.setImageResource(R.drawable.arrow_left);
        rightArrow.setImageResource(R.drawable.arrow_right);
    }


    private void hideKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    @Override
    public void leaveActivity() {
        finish();
    }
}
