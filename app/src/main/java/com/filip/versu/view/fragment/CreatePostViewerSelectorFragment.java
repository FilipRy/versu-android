package com.filip.versu.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.filip.versu.R;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.view.adapter.AbsBaseEntityRecyclerViewAdapter;
import com.filip.versu.view.adapter.PostViewersRecyclerViewAdapter;
import com.filip.versu.view.viewmodel.CreatePostViewersSelectorViewModel;
import com.filip.versu.view.viewmodel.callback.IViewersSelectorViewModel.IViewersSelectorViewModelCallback;

import java.util.ArrayList;
import java.util.List;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;


public class CreatePostViewerSelectorFragment
        extends AbsFollowingsFragment<CreatePostViewersSelectorViewModel, IViewersSelectorViewModelCallback>
        implements IViewersSelectorViewModelCallback {

    public static final String TAG = CreatePostViewerSelectorFragment.class.getSimpleName();

    private View followersLayout;
    private ToggleSwitch viewersTypeSelector;

    private boolean areFollowersLoaded;

    public static final String POST_KEY = "POST_KEY";
    /**
     * postDTO we are creating in @CreatePostActivity
     */
    private PostDTO postDTO;

    public static CreatePostViewerSelectorFragment instance(PostDTO postDTO) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(POST_KEY, postDTO);
        CreatePostViewerSelectorFragment fragment = new CreatePostViewerSelectorFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_viewers_selector, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        postDTO = (PostDTO) getArguments().getSerializable(POST_KEY);

        super.onViewCreated(view, savedInstanceState);

        followersLayout = view.findViewById(R.id.mainLayout);
        viewersTypeSelector = (ToggleSwitch) view.findViewById(R.id.viewersSelector);

        ArrayList<String> labels = new ArrayList<>();
        labels.add(getString(R.string.followers_visibility));
        labels.add(getString(R.string.public_visibility));
        labels.add(getString(R.string.specific_visibility));

        viewersTypeSelector.setLabels(labels);

        PostDTO.AccessType accessType = postDTO.accessType;
        if (accessType != null) {
            if (accessType == PostDTO.AccessType.FOLLOWERS) {
                viewersTypeSelector.setCheckedTogglePosition(0);
            } else if (accessType == PostDTO.AccessType.PUBLICC) {
                viewersTypeSelector.setCheckedTogglePosition(1);
            } else if (accessType == PostDTO.AccessType.SPECIFIC) {
                viewersTypeSelector.setCheckedTogglePosition(2);
                followersLayout.setVisibility(View.VISIBLE);
                if (!areFollowersLoaded) {
                    getViewModel().requestItemsFromInternalStorage(getActivity());
                    areFollowersLoaded = true;
                }
            }
        }

        viewersTypeSelector.setOnToggleSwitchChangeListener(new ToggleSwitch.OnToggleSwitchChangeListener() {
            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                if (isChecked) {
                    if (position == 0) {
                        getViewModel().setAccessType(PostDTO.AccessType.FOLLOWERS);
                        hideFollowers();
                    } else if (position == 1) {
                        getViewModel().setAccessType(PostDTO.AccessType.PUBLICC);
                        hideFollowers();
                    } else if (position == 2) {
                        getViewModel().setAccessType(PostDTO.AccessType.SPECIFIC);
                        followersLayout.setVisibility(View.VISIBLE);
                        if (!areFollowersLoaded) {
                            getViewModel().requestItemsFromInternalStorage(getActivity());
                            areFollowersLoaded = true;
                        }
                    }
                }
            }
        });

    }

    private void hideFollowers() {
        followersLayout.setVisibility(View.GONE);
    }

    @Override
    public AbsBaseEntityRecyclerViewAdapter createRecyclerViewAdapter() {
        return new PostViewersRecyclerViewAdapter(getActivity(), getViewModel());
    }

    @Override
    public void setModelView() {
        setModelView(this);
        getViewModel().setDependencies(postDTO);
    }


    @Nullable
    @Override
    public Class<CreatePostViewersSelectorViewModel> getViewModelClass() {
        return CreatePostViewersSelectorViewModel.class;
    }


    @Override
    public void setLoadedContent(List<PostViewersRecyclerViewAdapter.PostViewer> postViewers) {
        recyclerViewAdapter.addAllItems(postViewers);
    }
}
