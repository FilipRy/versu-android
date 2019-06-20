package com.filip.versu.view.fragment.parent;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.filip.versu.R;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.view.fragment.AbsPegContentFragment;
import com.filip.versu.view.fragment.CreatePostPhotosFragment;
import com.filip.versu.view.viewmodel.callback.parent.ITabsContainerViewModel.ITabsContainerViewCallback;
import com.filip.versu.view.viewmodel.parent.PostCreateContainerViewModel;

import java.util.ArrayList;
import java.util.List;

public class CreatePostPhotosContainerFragment extends AbsTabsContainerFragment<ITabsContainerViewCallback, PostCreateContainerViewModel> {

    public static final String TAG = CreatePostPhotosContainerFragment.class.getSimpleName();


    public static final String POST_KEY = "post_key";
    public static final String PAGER_INDEX_KEY = "pager_index_key";


    /**
     * The post which is being created now.
     */
    private PostDTO postDTO;
    private int currentPagerItem;

    private CreatePostPhotosFragment createPostPhotosFragment;

    public static CreatePostPhotosContainerFragment newInstance(PostDTO postDTO, int openPagerItem) {
        CreatePostPhotosContainerFragment fragment = new CreatePostPhotosContainerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(POST_KEY, postDTO);
        bundle.putInt(PAGER_INDEX_KEY, openPagerItem);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        postDTO = (PostDTO) getArguments().getSerializable(POST_KEY);
        currentPagerItem = getArguments().getInt(PAGER_INDEX_KEY);
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    protected List<AbsPegContentFragment> createFragmentsContentList() {
        List<AbsPegContentFragment> fragments = new ArrayList<>();

//        CameraContainerFragment cameraFragment = new CameraContainerFragment();
//        cameraFragment.setTitle(getString(R.string.camera));
//
//        fragments.add(cameraFragment);

        createPostPhotosFragment = CreatePostPhotosFragment.instance(postDTO);
        createPostPhotosFragment.setTitle(getString(R.string.gallery));


        fragments.add(createPostPhotosFragment);

        return fragments;
    }


    @Override
    protected void initTabColors() {
        int color = ContextCompat.getColor(getActivity(), R.color.tab_camera_underline);
        int normalColor = ContextCompat.getColor(getActivity(), R.color.tab_unselected_text);
        tabLayout.setTabTextColors(normalColor, color);
        tabLayout.setSelectedTabIndicatorColor(color);
    }

    @Override
    protected int getViewPagerCurrentItem() {
        return currentPagerItem;
    }

    public boolean areBitmapsSaved() {
        if (createPostPhotosFragment != null) {
//            return createPostPhotosFragment.areBitmapsSaved();
        }
        return false;
    }

    public void saveChosenBitmaps() {
        if (createPostPhotosFragment != null) {
//            createPostPhotosFragment.saveChosenBitmaps();
        }
    }


    @Override
    public void setModelView() {
        setModelView(this);
    }


    @Nullable
    @Override
    public Class<PostCreateContainerViewModel> getViewModelClass() {
        return PostCreateContainerViewModel.class;
    }
}
