package com.filip.versu.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;


import com.filip.versu.model.Location;
import com.filip.versu.model.view.PostFeedViewModel;
import com.filip.versu.view.adapter.AbsBaseEntityRecyclerViewAdapter;
import com.filip.versu.view.adapter.NearbySections;
import com.filip.versu.view.adapter.WrapAdapter;
import com.filip.versu.view.viewmodel.PostsTimelineFeedViewModel;
import com.filip.versu.view.viewmodel.PostsTimelineFeedViewModel.NewsfeedType;
import com.filip.versu.view.viewmodel.callback.IPostsTimelineFeedViewModel.IPostsTimelineFeedViewModelCallback;

import java.util.List;


/**
 * This fragment is a timeline or newsfeed in home section.
 */
public class PostsTimelineFeedFragment
        extends AbsPostsTimelineFeedFragment<PostFeedViewModel, PostsTimelineFeedViewModel, IPostsTimelineFeedViewModelCallback<PostFeedViewModel>>
        implements IPostsTimelineFeedViewModelCallback<PostFeedViewModel> {

    public static final String NEWSFEED_TYPE_KEY = "NEWSFEED_TYPE_KEY";
    public static final String SHOPPING_ITEM_ID = "SHOPPING_ITEM_ID";
    public static final String FEEDBACK_POSSIBLITIES_KEY = "FEEDBACK_POSSIBLITIES_KEY";
    public static final String LOCATION_KEY = "LOCATION_KEY";

    public static final int[] DISTANCE_STEP_IN_METER = {0, 500, 1000, 5000, 10000, 20000, 30000};

    private NewsfeedType newsfeedType;
    private Long shoppingItemID;
    private String[] feedbackPossibilities;
    private Location location;

    /**
     * This is used for sections in "Near by" fragment
     */
    private WrapAdapter wrapAdapter;

    public static PostsTimelineFeedFragment newInstance(NewsfeedType newsfeedType, Long shoppingItemID, String[] feedbackPossbilities, Location location) {

        PostsTimelineFeedFragment shoppingNewsfeedFragment = new PostsTimelineFeedFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NEWSFEED_TYPE_KEY, newsfeedType);
        if (shoppingItemID != null) {
            bundle.putLong(SHOPPING_ITEM_ID, shoppingItemID);
        }
        if (feedbackPossbilities != null) {
            bundle.putStringArray(FEEDBACK_POSSIBLITIES_KEY, feedbackPossbilities);
        }
        if (location != null) {
            bundle.putSerializable(LOCATION_KEY, location);
        }

        shoppingNewsfeedFragment.setArguments(bundle);
        return shoppingNewsfeedFragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        newsfeedType = (NewsfeedType) getArguments().getSerializable(NEWSFEED_TYPE_KEY);
        shoppingItemID = getArguments().getLong(SHOPPING_ITEM_ID);
        feedbackPossibilities = getArguments().getStringArray(FEEDBACK_POSSIBLITIES_KEY);
        location = (Location) getArguments().getSerializable(LOCATION_KEY);
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    protected void setupRecyclerView() {
        if (newsfeedType == NewsfeedType.NEAR_BY) {
            recyclerViewAdapter = createRecyclerViewAdapter();
            wrapAdapter = new WrapAdapter(recyclerViewAdapter, new NearbySections());
            recyclerView.setAdapter(wrapAdapter);

            recyclerViewLayoutManager = new LinearLayoutManager(getActivity());

            recyclerView.setLayoutManager(recyclerViewLayoutManager);
        } else {
            super.setupRecyclerView();
        }
    }

    @Override
    public void addLoadedItemsToViews(PostFeedViewModel items) {
        if (newsfeedType == NewsfeedType.NEAR_BY) {
            AbsBaseEntityRecyclerViewAdapter adapter = createRecyclerViewAdapter();
            adapter.addAllItems(items.feedItems);

            List<NearbySections.SectionWrapper> sectionWrappers = NearbySections.createSectionsForNearbyPosts(items.feedItems);
            int sectionsAt[] = new int[sectionWrappers.size()];
            int index = 0;
            for (NearbySections.SectionWrapper sectionWrapper : sectionWrappers) {
                sectionsAt[index++] = sectionWrapper.index;
            }

            wrapAdapter = new WrapAdapter(adapter, new NearbySections(sectionWrappers, sectionsAt));
            recyclerView.setAdapter(wrapAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            super.addLoadedItemsToViews(items);
        }
    }

    @Override
    public void notifyDatasetChanged() {
        super.notifyDatasetChanged();
        if (wrapAdapter != null) {
            wrapAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setModelView() {
        setModelView(this);
        getViewModel().setDependencies(newsfeedType, shoppingItemID, feedbackPossibilities, location);
    }

    @Nullable
    @Override
    public Class<PostsTimelineFeedViewModel> getViewModelClass() {
        return PostsTimelineFeedViewModel.class;
    }
}
