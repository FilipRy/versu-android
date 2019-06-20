package com.filip.versu.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.filip.versu.R;
import com.filip.versu.model.DevicePhoto;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.service.helper.Helper;
import com.filip.versu.service.impl.UserSession;
import com.filip.versu.view.adapter.DevicePhotoRecyclerViewAdapter;
import com.filip.versu.view.adapter.FeedbackPossibilityRecyclerViewAdapter;
import com.filip.versu.view.custom.PostVSDivider;
import com.filip.versu.view.viewmodel.CreatePostPossibilitiesViewModel;
import com.filip.versu.view.viewmodel.callback.ICreatePostPossibilitiesViewModel.ICreatePostPossibilitiesViewModelCallback;

import java.util.ArrayList;

import eu.inloop.viewmodel.base.ViewModelBaseFragment;

public class CreatePostPossibilitiesFragment
        extends ViewModelBaseFragment<ICreatePostPossibilitiesViewModelCallback, CreatePostPossibilitiesViewModel>
        implements ICreatePostPossibilitiesViewModelCallback {


    public static final String TAG = CreatePostPossibilitiesFragment.class.getSimpleName();

    private RecyclerView possibilitiesView;
    private View possibilitiesLayout;

    private RecyclerView postPhotosView;

    private DevicePhotoRecyclerViewAdapter postPhotosRecyclerViewAdapter;
    private FeedbackPossibilityRecyclerViewAdapter feedbackPossibilityRecyclerViewAdapter;

    /**
     * this is post which is beeing created
     */
    private PostDTO postDTO;

    public static final String POST_KEY = "POST_KEY";


    public static CreatePostPossibilitiesFragment instance(PostDTO postDTO) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(POST_KEY, postDTO);
        CreatePostPossibilitiesFragment fragment = new CreatePostPossibilitiesFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postDTO = (PostDTO) getArguments().getSerializable(POST_KEY);

        setModelView(this);

        possibilitiesView = (RecyclerView) view.findViewById(R.id.voting_poss_view);
        possibilitiesLayout = view.findViewById(R.id.possibilityLayout);

        postPhotosView = (RecyclerView) view.findViewById(R.id.recyclerViewPost);

        postPhotosView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        postPhotosRecyclerViewAdapter = new DevicePhotoRecyclerViewAdapter(new ArrayList<DevicePhoto>(), getActivity(), false, null, postDTO, true);
        postPhotosView.addItemDecoration(new PostVSDivider(getActivity()));
        postPhotosView.setAdapter(postPhotosRecyclerViewAdapter);


        /**
         * setting layout for post vote possibilities
         */
        //adding 2 possibilities

        feedbackPossibilityRecyclerViewAdapter = new FeedbackPossibilityRecyclerViewAdapter(postDTO.postFeedbackPossibilities, postDTO, getActivity(), null, true, null);
        possibilitiesView.setAdapter(feedbackPossibilityRecyclerViewAdapter);
        possibilitiesView.setHasFixedSize(true);
        possibilitiesView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        possibilitiesView.addItemDecoration(new PostVSDivider(getActivity()));

        possibilitiesLayout.setAlpha(Helper.POSSIBILITIES_OVERLAY_DISPLAYED_ALPHA);

        /**
         * adding photos to recycler view, if returning to this fragment
         */
        if (postDTO.devicePhotos.size() > 0) {
            postPhotosRecyclerViewAdapter.addAllItems(postDTO.devicePhotos);
        }


        displayPossibilitiesRequester();

        /**
         * displaying tutorial only if user hasn't changed the possibilities from default values
         */
        if (postDTO.postFeedbackPossibilities.get(0).name.equals("") &&
                postDTO.postFeedbackPossibilities.get(1).name.equals("")) {
            displayTutorialOverlay();
        }

    }

    private void displayTutorialOverlay() {
        if(UserSession.instance().needDisplayTutorial()) {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.tutorialContainerFrame, TutorialFragment.newInstance(getString(R.string.post_create_possibilities_hint), false), TutorialFragment.TAG).addToBackStack(null).commit();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_post_possibilities, container, false);
    }

    public void displayPossibilitiesRequester() {
        int chosenPhotoCount = postPhotosRecyclerViewAdapter.getContent().size();
        //possibilities are shown in a separate recycler view only for simple post, otherwise they are shown within postPhotosRecyclerViewAdapter
        if (chosenPhotoCount == 1) {
            possibilitiesLayout.setVisibility(View.VISIBLE);
        } else {
            postPhotosRecyclerViewAdapter.setDisplayPossibilities(true);
        }
    }

    @Nullable
    @Override
    public Class<CreatePostPossibilitiesViewModel> getViewModelClass() {
        return CreatePostPossibilitiesViewModel.class;
    }
}
