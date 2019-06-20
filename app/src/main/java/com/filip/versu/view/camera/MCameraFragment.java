package com.filip.versu.view.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.cam2.CameraController;
import com.commonsware.cwac.cam2.CameraEngine;
import com.commonsware.cwac.cam2.CameraSelectionCriteria;
import com.commonsware.cwac.cam2.CameraView;
import com.commonsware.cwac.cam2.ErrorConstants;
import com.commonsware.cwac.cam2.Facing;
import com.commonsware.cwac.cam2.FlashMode;
import com.commonsware.cwac.cam2.FocusMode;
import com.commonsware.cwac.cam2.ImageContext;
import com.commonsware.cwac.cam2.PictureTransaction;
import com.filip.versu.R;
import com.filip.versu.view.fragment.AbsPegContentFragment;
import com.filip.versu.view.viewmodel.CameraViewModel;
import com.filip.versu.view.viewmodel.callback.ICameraViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.greenrobot.event.EventBus;


public class MCameraFragment extends AbsPegContentFragment<ICameraViewModel.ICameraViewModelCallback, CameraViewModel> {

    public static final String TAG = MCameraFragment.class.getSimpleName();

    enum FlashModeSet {
        ON, OFF, AUTO
    }

    private FlashModeSet flashMode;

    private static final int PINCH_ZOOM_DELTA = 10;


    private EventBus eventBus = EventBus.getDefault();
    private CameraController ctlr;


    private View progress;
    private ScaleGestureDetector scaleDetector;

    private ViewGroup previewStack;
    private View capturePhotoContent;
    private ImageView capturePhotoBtn;
    private ImageView changeCameraBtn;
    private TextView flashModeView;

    private View photoPreviewContent;
    private ImageView photoPreviewImageView;
    private ImageView retakePhotoBtn;
    private ImageView savePhotoBtn;

    private View flashView;

    private boolean inSmoothPinchZoom = false;
    private boolean mirrorPreview = false;


    /**
     * Standard callback method to create the UI managed by
     * this fragment.
     *
     * @param inflater           Used to inflate layouts
     * @param container          Parent of the fragment's UI (eventually)
     * @param savedInstanceState State of a previous instance
     * @return the UI being managed by this fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mcamera, container, false);

        previewStack = (ViewGroup) v.findViewById(R.id.cwac_cam2_preview_stack);
        progress = v.findViewById(R.id.cwac_cam2_progress);

        capturePhotoContent = v.findViewById(R.id.camera_tools_view);
        capturePhotoBtn = (ImageView) v.findViewById(R.id.capture_image_button);
        changeCameraBtn = (ImageView) v.findViewById(R.id.change_camera);
        flashModeView = (TextView) v.findViewById(R.id.auto_flash_icon);

        photoPreviewContent = v.findViewById(R.id.preview_tools_view);
        retakePhotoBtn = (ImageView) v.findViewById(R.id.retake_image_button);
        savePhotoBtn = (ImageView) v.findViewById(R.id.savePhotoBtn);
        photoPreviewImageView = (ImageView) v.findViewById(R.id.photo_preview_view);

        flashView = v.findViewById(R.id.flash);

        capturePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });


        changeCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setVisibility(View.VISIBLE);
                changeCameraBtn.setEnabled(false);

                try {
                    ctlr.switchCamera();
                } catch (Exception e) {
                    ctlr.postError(ErrorConstants.ERROR_SWITCHING_CAMERAS, e);
                    Log.e(getClass().getSimpleName(), "Exception switching camera", e);
                }
            }
        });

        retakePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayPhotoPreviewOverlay(false);
                changeCameraBtn.setEnabled(true);
                capturePhotoBtn.setEnabled(true);
            }
        });

        flashView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFlashMode();
            }
        });

        flashMode = FlashModeSet.ON;
        displayFlashMode();

        capturePhotoBtn.setEnabled(false);
        changeCameraBtn.setEnabled(false);

        int numberOfCameras = ctlr.getNumberOfCameras();

        if (ctlr != null && numberOfCameras > 0) {
            prepController();
        }

        return (v);
    }

    private void changeFlashMode() {
        if (flashMode == FlashModeSet.ON) {
            flashMode = FlashModeSet.OFF;
        } else if (flashMode == FlashModeSet.OFF) {
            flashMode = FlashModeSet.AUTO;
        } else {
            flashMode = FlashModeSet.ON;
        }
        displayFlashMode();
        if (ctlr != null) {
            configEngine(ctlr.getEngine());
        }
    }

    private void displayFlashMode() {
        if (flashMode == FlashModeSet.ON) {
            flashModeView.setText(R.string.on);
        } else if (flashMode == FlashModeSet.OFF) {
            flashModeView.setText(R.string.off);
        } else {
            flashModeView.setText(R.string.auto);
        }
    }

    /**
     * Standard fragment entry point.
     *
     * @param savedInstanceState State of a previous instance
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scaleDetector = new ScaleGestureDetector(getActivity().getApplicationContext(), scaleListener);
    }

    /**
     * Standard lifecycle method, passed along to the CameraController.
     */
    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);

        if (ctlr != null) {
            ctlr.start();
        }
    }

    /**
     * Standard lifecycle method, for when the fragment moves into
     * the stopped state. Passed along to the CameraController.
     */
    @Override
    public void onStop() {
        if (ctlr != null) {
            try {
                ctlr.stop();
            } catch (Exception e) {
                ctlr.postError(ErrorConstants.ERROR_STOPPING, e);
                Log.e(getClass().getSimpleName(), "Exception stopping controller", e);
            }
        }

        EventBus.getDefault().unregister(this);

        super.onStop();
    }

    /**
     * Standard lifecycle method, for when the fragment is utterly,
     * ruthlessly destroyed. Passed along to the CameraController,
     * because why should the fragment have all the fun?
     */
    @Override
    public void onDestroy() {
        if (ctlr != null) {
            ctlr.destroy();
        }

        super.onDestroy();
    }


    public void shutdown() {
        progress.setVisibility(View.VISIBLE);

        if (ctlr != null) {
            try {
                ctlr.stop();
            } catch (Exception e) {
                ctlr.postError(ErrorConstants.ERROR_STOPPING, e);
                Log.e(getClass().getSimpleName(),
                        "Exception stopping controller", e);
            }
        }
    }

    /**
     * @return the CameraController this fragment delegates to
     */
    public CameraController getController() {
        return (ctlr);
    }


    /**
     * Establishes the controller that this fragment delegates to
     */
    public void prepareCamera(Context context) {
        int currentCamera = -1;

        if (this.ctlr != null) {
            currentCamera = this.ctlr.getCurrentCamera();
        }

        CameraController ctrl = new CameraController(FocusMode.CONTINUOUS, null, true, false);

        this.ctlr = ctrl;
        this.ctlr.setQuality(1);

        if (currentCamera > -1) {
            this.ctlr.setCurrentCamera(currentCamera);
        }

        CameraSelectionCriteria criteria =
                new CameraSelectionCriteria.Builder()
                        .facing(Facing.BACK)
                        .facingExactMatch(false)
                        .build();

        //TODO check the camera engine ID here.
        CameraEngine cameraEngine = CameraEngine.buildInstance(context, null);

        ctrl.setEngine(cameraEngine, criteria);

        configEngine(ctrl.getEngine());
    }

    private void configEngine(CameraEngine engine) {

        List<FlashMode> flashModes = new ArrayList<>();
        if (flashMode == FlashModeSet.ON) {
            flashModes.add(FlashMode.ALWAYS);
        } else if (flashMode == FlashModeSet.OFF) {
            flashModes.add(FlashMode.OFF);
        } else if (flashMode == FlashModeSet.AUTO) {
            flashModes.add(FlashMode.AUTO);
        }

        engine.setPreferredFlashModes(flashModes);

    }


    @SuppressWarnings("unused")
    public void onEventMainThread(CameraController.ControllerReadyEvent event) {
        if (event.isEventForController(ctlr)) {
            prepController();
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(CameraEngine.OpenedEvent event) {
        if (event.exception == null) {
            progress.setVisibility(View.GONE);
            changeCameraBtn.setEnabled(canSwitchSources());
            capturePhotoBtn.setEnabled(true);

            if (ctlr.supportsZoom()) {
                previewStack.setOnTouchListener(
                        new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return (scaleDetector.onTouchEvent(event));
                            }
                        });
            } else {
                previewStack.setOnTouchListener(null);
            }
        } else {
            ctlr.postError(ErrorConstants.ERROR_OPEN_CAMERA, event.exception);
            getActivity().finish();
        }
    }


    @SuppressWarnings("unused")
    public void onEventMainThread(CameraEngine.SmoothZoomCompletedEvent event) {
        inSmoothPinchZoom = false;
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(CameraEngine.PictureTakenEvent event) {
        if (event.exception == null) {
            displayPhotoPreviewView(event.getImageContext());
        } else {
            Toast.makeText(getActivity(), R.string.error_taking_photo, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(PictureSavedEvent event) {
        if (event.exception == null) {
            displayPhotoPreviewOverlay(false);
            changeCameraBtn.setEnabled(true);
            capturePhotoBtn.setEnabled(true);
        } else {
            Toast.makeText(getActivity(), R.string.error_saving_photo, Toast.LENGTH_SHORT).show();
        }
    }


    private void displayPhotoPreviewView(ImageContext imageContext) {
        displayPhotoPreviewOverlay(true);

        MImageContext mImageContext = new MImageContext(imageContext);
        final Bitmap preview = mImageContext.buildResultThumbnail(true);


        photoPreviewImageView.setImageBitmap(preview);

        savePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri savedPictureUri = null;
                try {
                    savedPictureUri = ImageUtility.savePicture(getActivity(), preview);
                } catch (IOException e) {
                    getEventBus().post(new PictureSavedEvent(e));
                    Log.i(TAG, e.getMessage());
                    return;
                }
                getEventBus().post(new PictureSavedEvent(savedPictureUri.getPath()));
            }
        });

    }

    private void takePicture() {
        PictureTransaction.Builder b = new PictureTransaction.Builder();

        capturePhotoBtn.setEnabled(false);
        changeCameraBtn.setEnabled(false);
        ctlr.takePicture(b.build());
    }

    private boolean canSwitchSources() {
        return true;
    }


    private void prepController() {
        LinkedList<CameraView> cameraViews = new LinkedList<>();
        CameraView cv = (CameraView) previewStack.getChildAt(0);

        cv.setMirror(mirrorPreview);
        cameraViews.add(cv);

        for (int i = 1; i < ctlr.getNumberOfCameras(); i++) {
            cv = new CameraView(getActivity());
            cv.setVisibility(View.INVISIBLE);
            cv.setMirror(mirrorPreview);
            previewStack.addView(cv);
            cameraViews.add(cv);
        }

        ctlr.setCameraViews(cameraViews);
    }

    private void displayPhotoPreviewOverlay(boolean show) {
        if (show) {
            photoPreviewContent.setVisibility(View.VISIBLE);
            photoPreviewImageView.setVisibility(View.VISIBLE);
            capturePhotoContent.setVisibility(View.GONE);
            previewStack.setVisibility(View.GONE);
        } else {
            photoPreviewContent.setVisibility(View.GONE);
            photoPreviewImageView.setVisibility(View.INVISIBLE);
            capturePhotoContent.setVisibility(View.VISIBLE);
            previewStack.setVisibility(View.VISIBLE);
        }
    }

    private ScaleGestureDetector.OnScaleGestureListener scaleListener =
            new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                @Override
                public void onScaleEnd(ScaleGestureDetector detector) {
                    float scale = detector.getScaleFactor();
                    int delta;

                    if (scale > 1.0f) {
                        delta = PINCH_ZOOM_DELTA;
                    } else if (scale < 1.0f) {
                        delta = -1 * PINCH_ZOOM_DELTA;
                    } else {
                        return;
                    }

                    if (!inSmoothPinchZoom) {
                        if (ctlr.changeZoom(delta)) {
                            inSmoothPinchZoom = true;
                        }
                    }
                }
            };

    public EventBus getEventBus() {
        return eventBus;
    }

    @Nullable
    @Override
    public Class<CameraViewModel> getViewModelClass() {
        return CameraViewModel.class;
    }

}