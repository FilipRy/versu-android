package com.filip.versu.view.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.filip.versu.R;
import com.filip.versu.model.DevicePhoto;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.service.helper.BlurBuilder;
import com.filip.versu.service.helper.Helper;
import com.filip.versu.view.viewmodel.callback.ICreatePostPhotosViewModel.ICreatePostPhotosViewModelCallback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;


public class DevicePhotoRecyclerViewAdapter extends AbsBaseEntityRecyclerViewAdapter<DevicePhoto> {

    /**
     * This is true if the adapter is used to display only the small thumbnails of all photos on the device.
     */
    private boolean showingThumbnails;
    private ICreatePostPhotosViewModelCallback createPostViewModelCallback;

    /**
     * says if the possibilities overlay should be displayed or not
     */
    private boolean displayVotingPossibilities;

    /**
     * says if the remove and zoom photo functionality should be disabled
     */
    private boolean disableZoomAndDelete;


    private PostDTO postDTO;

    public DevicePhotoRecyclerViewAdapter(List<DevicePhoto> recyclerViewItems, Context context,
                                          boolean showingThumbnails, ICreatePostPhotosViewModelCallback createPostViewModelCallback,
                                          PostDTO postDTO, boolean disableZoomAndDelete) {
        super(recyclerViewItems, context);
        this.showingThumbnails = showingThumbnails;
        this.createPostViewModelCallback = createPostViewModelCallback;
        this.postDTO = postDTO;
        this.disableZoomAndDelete = disableZoomAndDelete;

    }

    @Override
    public AbsBindViewHolder<DevicePhoto> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (showingThumbnails) {
            view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_device_photo, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_create_post_photo, parent, false);
        }

        return new DevicePhotoRecyclerViewHolder(view, showingThumbnails);
    }

    public class DevicePhotoRecyclerViewHolder extends AbsBindViewHolder<DevicePhoto> {

        private DevicePhoto devicePhoto;
        private ImageView devicePhotoView;
        private ImageView blurPhotoView;


        private ImageView removePhotoView;
        private ImageView cropPhotoView;

        private EditText possibilityEditTextTop, possibilityEditTextBottom;
        private View possibilityOverlay;

        /**
         * This is true if the adapter is used to display only the small thumbnails of all photos on the device.
         */
        private boolean showingThumbnails;

        public DevicePhotoRecyclerViewHolder(View itemView, boolean showingThumbnails) {
            super(itemView);
            devicePhotoView = (ImageView) itemView.findViewById(R.id.shoppingItemPhoto);
            blurPhotoView = (ImageView) itemView.findViewById(R.id.postPhotoBlurBg);

            removePhotoView = (ImageView) itemView.findViewById(R.id.removePhoto);
            cropPhotoView = (ImageView) itemView.findViewById(R.id.cropPhoto);

            this.showingThumbnails = showingThumbnails;
            if(!showingThumbnails) {

                possibilityEditTextTop = (EditText) itemView.findViewById(R.id.possibility_name_edit_text_top);
                possibilityEditTextBottom = (EditText) itemView.findViewById(R.id.possibility_name_edit_text_bottom);
                possibilityOverlay = itemView.findViewById(R.id.possibilityOverlay);
            }

            if(disableZoomAndDelete) {
                removePhotoView.setVisibility(View.GONE);
                cropPhotoView.setVisibility(View.GONE);
            }
        }

        @Override
        public void bindView(DevicePhoto entity, final int pos) {
            this.devicePhoto = entity;

            if (showingThumbnails) {
                Picasso.with(context).load(new File(devicePhoto.path)).placeholder(R.drawable.device_photo_place_holder).
                        resizeDimen(R.dimen.recycler_view_item_device_photo_height, R.dimen.recycler_view_item_device_photo_height).centerCrop().into(devicePhotoView);
                devicePhotoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createPostViewModelCallback.addPhotoToPost(devicePhoto);
                    }
                });
            } else {

                TextWatcher textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        postDTO.postFeedbackPossibilities.get(pos).name = editable.toString();
                    }
                };

                //we are creating a voting post
                if(getContent().size() > 1 && displayVotingPossibilities) {
                    possibilityOverlay.setVisibility(View.VISIBLE);
                    possibilityOverlay.setAlpha(Helper.POSSIBILITIES_OVERLAY_DISPLAYED_ALPHA);

                    String feedbackPossibilityName = postDTO.postFeedbackPossibilities.get(pos).name;

                    if(pos == 0) {
                        possibilityEditTextBottom.setVisibility(View.VISIBLE);
                        possibilityEditTextTop.setVisibility(View.INVISIBLE);

                        possibilityEditTextBottom.setText(feedbackPossibilityName);

                        possibilityEditTextBottom.addTextChangedListener(textWatcher);

                        possibilityEditTextBottom.requestFocus();

                    } else if (pos == 1) {
                        possibilityEditTextBottom.setVisibility(View.INVISIBLE);
                        possibilityEditTextTop.setVisibility(View.VISIBLE);

                        possibilityEditTextTop.setText(feedbackPossibilityName);

                        possibilityEditTextTop.addTextChangedListener(textWatcher);
                    }


                } else {
                    possibilityOverlay.setVisibility(View.GONE);
                }
                devicePhotoView.setScaleType(ImageView.ScaleType.FIT_CENTER);

                blurPhotoView.setVisibility(View.VISIBLE);

                devicePhotoView.setDrawingCacheEnabled(true);
                blurPhotoView.setDrawingCacheEnabled(true);


                blurPhotoView.setImageBitmap(BlurBuilder.blur(devicePhoto.bitmap, context));

                devicePhoto.blurImageView = blurPhotoView;
                devicePhoto.photoImageView = devicePhotoView;

                devicePhotoView.setImageBitmap(devicePhoto.bitmap);

                removePhotoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(createPostViewModelCallback != null) {
                            createPostViewModelCallback.removePhotoFromPost(devicePhoto);
                        }
                    }
                });

                cropPhotoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createPostViewModelCallback.cropDevicePhoto(devicePhoto);
                    }
                });
            }
        }

    }


    public void setDisplayPossibilities(boolean displayPossibilities) {
        displayVotingPossibilities = displayPossibilities;
        notifyDataSetChanged();
    }

}
