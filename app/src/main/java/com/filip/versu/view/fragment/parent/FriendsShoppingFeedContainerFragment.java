package com.filip.versu.view.fragment.parent;


import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.filip.versu.R;
import com.filip.versu.view.fragment.AbsPegContentFragment;
import com.filip.versu.view.fragment.PostsTimelineFeedFragment;
import com.filip.versu.view.viewmodel.PostsTimelineFeedViewModel;
import com.filip.versu.view.viewmodel.callback.parent.IFriendsShoppingFeedContainerViewModel;
import com.filip.versu.view.viewmodel.parent.FriendsShoppingFeedContainerViewModel;

import java.util.ArrayList;
import java.util.List;

public class FriendsShoppingFeedContainerFragment extends AbsTabsContainerFragment<IFriendsShoppingFeedContainerViewModel.IFriendsShoppingFeedContainerViewCallback, FriendsShoppingFeedContainerViewModel> implements IFriendsShoppingFeedContainerViewModel.IFriendsShoppingFeedContainerViewCallback {

    public static final String TAG = "FriendsShoppingFeedContainerFragment";

    @Override
    protected List<AbsPegContentFragment> createFragmentsContentList() {
        List<AbsPegContentFragment> shoppingNewsfeedFragmentList = new ArrayList<>();

        PostsTimelineFeedFragment timelineNewsfeed = PostsTimelineFeedFragment.newInstance(PostsTimelineFeedViewModel.NewsfeedType.TIMELINE, 0l, null, null);
        timelineNewsfeed.setTitle(this.getString(R.string.timeline));
        timelineNewsfeed.setImageResource(R.mipmap.ic_access_time_white_24dp);
        PostsTimelineFeedFragment nearbyNewsfeed = PostsTimelineFeedFragment.newInstance(PostsTimelineFeedViewModel.NewsfeedType.NEAR_BY, 0l, null, null);
        nearbyNewsfeed.setTitle(this.getString(R.string.nearby));
        nearbyNewsfeed.setImageResource(R.mipmap.ic_near_me_white_24dp);

        shoppingNewsfeedFragmentList.add(timelineNewsfeed);
        shoppingNewsfeedFragmentList.add(nearbyNewsfeed);

        return shoppingNewsfeedFragmentList;
    }

    @Override
    protected void initTabColors() {
        int color = ContextCompat.getColor(getActivity(), R.color.tab_home_underline);
        int normalColor = ContextCompat.getColor(getActivity(), R.color.tab_unselected_text);
        tabLayout.setTabTextColors(normalColor, color);
        tabLayout.setSelectedTabIndicatorColor(color);
    }

    @Override
    public void setModelView() {
        setModelView(this);
    }

    @Nullable
    @Override
    public Class<FriendsShoppingFeedContainerViewModel> getViewModelClass() {
        return FriendsShoppingFeedContainerViewModel.class;
    }

}
