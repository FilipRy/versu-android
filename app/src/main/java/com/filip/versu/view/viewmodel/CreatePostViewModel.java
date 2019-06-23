package com.filip.versu.view.viewmodel;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.filip.versu.R;
import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.DevicePhoto;
import com.filip.versu.model.Location;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.dto.PostFeedbackPossibilityDTO;
import com.filip.versu.model.dto.PostPhotoDTO;
import com.filip.versu.service.IPostService;
import com.filip.versu.service.helper.GlobalModelVersion;
import com.filip.versu.service.impl.PostService;
import com.filip.versu.service.impl.UserSession;
import com.filip.versu.view.viewmodel.callback.ICreatePostViewModel;
import com.filip.versu.view.viewmodel.callback.ICreatePostViewModel.ICreatePostViewModelCallback;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.inloop.viewmodel.AbstractViewModel;


public class CreatePostViewModel extends AbstractViewModel<ICreatePostViewModelCallback> implements ICreatePostViewModel {

    public static final String TAG = CreatePostViewModel.class.getSimpleName();

    private IPostService shoppingItemService = PostService.instance();

    private PostDTO postToCreate;

    private int progressPointer;

    public CreatePostViewModel() {
        progressPointer = 0;
        postToCreate = new PostDTO();

        PostFeedbackPossibilityDTO possibility1 = new PostFeedbackPossibilityDTO();
        possibility1.name = "";

        PostFeedbackPossibilityDTO possibility2 = new PostFeedbackPossibilityDTO();
        possibility2.name = "";

        postToCreate.postFeedbackPossibilities.add(possibility1);
        postToCreate.postFeedbackPossibilities.add(possibility2);

        postToCreate.accessType = PostDTO.AccessType.FOLLOWERS;
        postToCreate.owner = UserSession.instance().getLogedInUser();
        postToCreate.location = postToCreate.owner.location;
    }

    @Override
    public PostDTO getSimplePostToCreate() {
        return postToCreate;
    }


    @Override
    public void searchNearbyPlaces(GoogleApiClient mGoogleApiClient) {

        final List<Location> mostLikelyPlaces = new ArrayList<>();

        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);

        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    if (placeLikelihood.getLikelihood() > 0.5f || mostLikelyPlaces.size() < 2) {
                        mostLikelyPlaces.add(new Location(placeLikelihood.getPlace()));
                    }
                }
                likelyPlaces.release();
                if (getView() != null) {
                    getView().setNearbyPlaces(mostLikelyPlaces);
                }
            }
        });
    }

    @Override
    public void requestCreateShoppingItem() {

        for (int i = 0; i < postToCreate.postFeedbackPossibilities.size(); i++) {
            PostFeedbackPossibilityDTO possibility = postToCreate.postFeedbackPossibilities.get(i);

            possibility.name = possibility.name.replaceAll("#","");
        }

        ShoppingItemCreateTask shoppingItemCreateTask = new ShoppingItemCreateTask(postToCreate);
        shoppingItemCreateTask.execute();
    }

    @Override
    public void incrementProgressPointer() {
        boolean isValid = validateInputBeforeProgressIncrement();
        if (isValid) {
            progressPointer++;
            refreshViewAfterProgressChanged();
        }

    }

    @Override
    public void decrementProgressPointer() {
        progressPointer--;
        refreshViewAfterProgressChanged();
    }

    @Override
    public void submitCreatePostTask() {

        postToCreate.photos.clear();

        int index = 0;
        for (DevicePhoto devicePhoto : postToCreate.devicePhotos) {

            ImageView photoImageView = devicePhoto.photoImageView;
            ImageView blurImageView = devicePhoto.blurImageView;

            Bitmap bmp = photoImageView.getDrawingCache();
            Bitmap blurBg = blurImageView.getDrawingCache();

            if(bmp == null) {
                photoImageView.buildDrawingCache();
                bmp = photoImageView.getDrawingCache();
            }

            if(blurBg == null) {
                blurImageView.buildDrawingCache();
                blurBg = blurImageView.getDrawingCache();
            }

            if(bmp == null) {
                bmp = Bitmap.createBitmap(photoImageView.getMeasuredWidth(), photoImageView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                Canvas bitmapHolder = new Canvas(bmp);
                photoImageView.draw(bitmapHolder);
            }

            if (blurBg == null) {
                blurBg = Bitmap.createBitmap(blurImageView.getMeasuredWidth(), blurImageView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                Canvas blurBitmapHolder = new Canvas(blurBg);
                blurImageView.draw(blurBitmapHolder);
            }



            //devicePhoto.blurImageView.setDrawingCacheEnabled(false);

            Bitmap bmOverlay = Bitmap.createBitmap(blurBg.getWidth(), blurBg.getHeight(), blurBg.getConfig());

            Canvas canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(blurBg, 0, 0, null);
            canvas.drawBitmap(bmp, 0, 0, null);

            //create temporary photo
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + System.currentTimeMillis() + (index++) + ".jpg");
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(f);
                bmOverlay.compress(Bitmap.CompressFormat.JPEG, 100, out);

            } catch (IOException e) {
                Log.i(TAG, e.getMessage());
            } finally {
                try {
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                } catch (IOException e) {
                }
            }

            bmp.recycle();
            blurBg.recycle();
            bmOverlay.recycle();

            blurImageView.setDrawingCacheEnabled(false);
            photoImageView.setDrawingCacheEnabled(false);

            photoImageView.destroyDrawingCache();
            blurImageView.destroyDrawingCache();

            PostPhotoDTO postPhotoDTO = new PostPhotoDTO();
            postPhotoDTO.path = f.getAbsolutePath();
            postPhotoDTO.takenTime = System.currentTimeMillis();

            postToCreate.photos.add(postPhotoDTO);
        }

        requestCreateShoppingItem();
    }

    /**
     * @return true if all fields for the current progress are valid, else false
     */
    private boolean validateInputBeforeProgressIncrement() {
        if (getView() == null) {
            return true;
        }
        if (progressPointer == 0) {
            if (postToCreate.devicePhotos.size() == 0) {
                getView().displayCreateMsg(R.string.invalid_photos);
                return false;
            }
        }
        if (progressPointer == 1) {
            if (postToCreate.postFeedbackPossibilities.size() < 2) {
                getView().displayCreateMsg(R.string.invalid_possibilities);
                return false;
            }
            if (postToCreate.postFeedbackPossibilities.get(0).name == null || postToCreate.postFeedbackPossibilities.get(0).name.isEmpty()) {
                getView().displayCreateMsg(R.string.invalid_possibilities);
                return false;
            }
            if (postToCreate.postFeedbackPossibilities.get(1).name == null || postToCreate.postFeedbackPossibilities.get(1).name.isEmpty()) {
                getView().displayCreateMsg(R.string.invalid_possibilities);
                return false;
            }

            if (postToCreate.postFeedbackPossibilities.get(0).name.equals(postToCreate.postFeedbackPossibilities.get(1).name)) {
                getView().displayCreateMsg(R.string.invalid_same_possibilities);
                return false;
            }

            String possibilityA = postToCreate.postFeedbackPossibilities.get(0).name;
            String possibilityB = postToCreate.postFeedbackPossibilities.get(1).name;

            if(possibilityA.length() > 20 || possibilityB.length() > 20) {
                getView().displayCreateMsg(R.string.invalid_possibilities_too_long);
                return false;
            }

        }
        if (progressPointer == 2) {
            if (postToCreate.description != null && postToCreate.description.length() > 191) {
                getView().displayCreateMsg(R.string.invalid_desc);
                return false;
            }
        }

        if (progressPointer == 3) {
            if(postToCreate.location != null && postToCreate.location.isUnknown() && (postToCreate.location.googleID == null || postToCreate.location.googleID.isEmpty())) {
                getView().displayCreateMsg(R.string.invalid_location);
                return false;
            }
        }

        if (progressPointer == 4) {
            if (postToCreate.accessType == null) {
                getView().displayCreateMsg(R.string.invalid_access_type);
                return false;
            }
        }
        return true;
    }

    private void refreshViewAfterProgressChanged() {
        if (getView() != null) {
            if (progressPointer <= -1) {
                getView().leaveActivity();
            }
            if (progressPointer == 0) {
                getView().displayPhotoSelector(true);
            }
            if (progressPointer == 1) {
                getView().displayPossibilitiesRequester();
            }
            if (progressPointer == 2) {
                getView().displayPostDescriptionCreator();
            }
            if (progressPointer == 3) {
                getView().displayPlacesSelector();
            }
            if (progressPointer == 4) {
                getView().displayViewersSelector();
            }
            if (progressPointer == 5) {
                submitCreatePostTask();
                progressPointer--;//if I click back I want to go direct to photo selector, not again to viewers selector
            }
        }
    }


    class ShoppingItemCreateTask extends AbsCreateItemTask<PostDTO> {


        public ShoppingItemCreateTask(PostDTO item) {
            super(item);
        }

        @Override
        protected void updateViewBeforeCreateTask() {
            if (getView() != null) {
                getView().showCreatePostProgress(true);
            }
        }

        @Override
        protected PostDTO sendCreateItemRequest(PostDTO item) throws ServiceException {
            item = shoppingItemService.create(item);
            UserSession.instance().newPostWasCreated();
            return item;
        }

        @Override
        protected void rollbackCreateOperation() {
            if (getView() != null) {
                getView().showCreatePostProgress(false);
                getView().displayCreateMsg(errorMsg);
            }
        }

        @Override
        protected void onCreateOperationSuccess(PostDTO createdItem) {
            GlobalModelVersion.refreshGlobalShoppingItemTimestamp();
            if (getView() != null) {
                getView().showCreatePostProgress(false);
                getView().displayCreateMsg(R.string.create_post_success);
                getView().leaveActivity();
            }
        }

        @Override
        protected void displayErrorMessage(String errorMsg) {
            if (getView() != null) {
                Activity activity = (Activity) getView();
                Toast.makeText(activity, errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
