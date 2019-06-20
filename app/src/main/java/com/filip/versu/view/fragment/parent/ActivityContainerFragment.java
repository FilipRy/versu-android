package com.filip.versu.view.fragment.parent;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.filip.versu.R;
import com.filip.versu.view.fragment.AbsPegContentFragment;
import com.filip.versu.view.fragment.MyFeedbackActionsFragment;
import com.filip.versu.view.fragment.NotificationsFragment;
import com.filip.versu.view.viewmodel.MyFeedbackActionsViewModel;
import com.filip.versu.view.viewmodel.callback.parent.IActivityContainerViewModel;

import java.util.ArrayList;
import java.util.List;


public class ActivityContainerFragment extends AbsTabsContainerFragment<IActivityContainerViewModel.IActivityContainerViewCallback, com.filip.versu.view.viewmodel.parent.ActivityContainerViewModel> implements IActivityContainerViewModel.IActivityContainerViewCallback {


    @Override
    protected List<AbsPegContentFragment> createFragmentsContentList() {
        List<AbsPegContentFragment> shoppingMyProfileFragments = new ArrayList<>();
        MyFeedbackActionsFragment myVotesYes = MyFeedbackActionsFragment.newInstance(MyFeedbackActionsViewModel.MyFeedbackActionFragmentType.POST_FEEDBACK);

        myVotesYes.setImageResource(R.drawable.heart_white);
        myVotesYes.setTitle(this.getString(R.string.my_votes));

        NotificationsFragment notificationFragment = new NotificationsFragment();
        notificationFragment.setTitle(getString(R.string.notifications));


        shoppingMyProfileFragments.add(notificationFragment);
        shoppingMyProfileFragments.add(myVotesYes);
        return shoppingMyProfileFragments;
    }

    @Override
    protected void initTabColors() {
        int color = ContextCompat.getColor(getActivity(), R.color.tab_notification_underline);
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
    public Class<com.filip.versu.view.viewmodel.parent.ActivityContainerViewModel> getViewModelClass() {
        return com.filip.versu.view.viewmodel.parent.ActivityContainerViewModel.class;
    }
}
