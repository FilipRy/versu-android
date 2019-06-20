package com.filip.versu.view.fragment.parent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.filip.versu.R;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.dto.PostFeedbackPossibilityDTO;
import com.filip.versu.view.fragment.AbsPegContentFragment;
import com.filip.versu.view.fragment.AbsPostsFeedFragment;
import com.filip.versu.view.fragment.CommentFragment;
import com.filip.versu.view.fragment.PostFeedbackFragment;
import com.filip.versu.view.viewmodel.AbsPostsFeedViewModel;
import com.filip.versu.view.viewmodel.callback.parent.ITabsContainerViewModel.ITabsContainerViewCallback;
import com.filip.versu.view.viewmodel.parent.AbsTabsContainerViewModel;
import com.h6ah4i.android.tablayouthelper.TabLayoutHelper;

import java.util.ArrayList;
import java.util.List;


public class ItemFeedbackActionContainerFragment extends AbsTabsContainerFragment<ITabsContainerViewCallback, AbsTabsContainerViewModel<ITabsContainerViewCallback>> {

    public static final String TAG = "ItemFeedbackActionContainerFragment";

    public static final String POSSIBILITY_KEY = "POSSIBILITY_KEY";
    public static final String VOTERS_TYPE_KEY = "voters_type_key";
    public static final String CLICK_ORIGIN_KEY = "click_origin_key";

    private AbsPostsFeedFragment.INotifyFeedbackActionChange notifyFeedbackActionChange;

    private FeedbackActionDialogType feedbackActionDialogType;
    private AbsPostsFeedViewModel.FeedbackActionClickOrigin clickOrigin;

    private PostFeedbackPossibilityDTO possibilityDTO;

    private PostDTO postDTO;

    private int viewPagerCurrentItem;


    /**
     * To display comments, just pass an arbitrary possibility of the post
     * @param possibilityDTO
     * @param feedbackActionDialogType
     * @param actionClickOrigin
     * @return
     */
    public static ItemFeedbackActionContainerFragment newInstance(PostFeedbackPossibilityDTO possibilityDTO, FeedbackActionDialogType feedbackActionDialogType, AbsPostsFeedViewModel.FeedbackActionClickOrigin actionClickOrigin) {
        ItemFeedbackActionContainerFragment itemVotersContainerFragment = new ItemFeedbackActionContainerFragment();

        Bundle bundle = new Bundle();

        bundle.putSerializable(POSSIBILITY_KEY, possibilityDTO);
        bundle.putSerializable(VOTERS_TYPE_KEY, feedbackActionDialogType);
        bundle.putSerializable(CLICK_ORIGIN_KEY, actionClickOrigin);

        itemVotersContainerFragment.setArguments(bundle);
        return itemVotersContainerFragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        feedbackActionDialogType = (FeedbackActionDialogType) getArguments().getSerializable(VOTERS_TYPE_KEY);
        clickOrigin = (AbsPostsFeedViewModel.FeedbackActionClickOrigin) getArguments().getSerializable(CLICK_ORIGIN_KEY);

        possibilityDTO = (PostFeedbackPossibilityDTO) getArguments().getSerializable(POSSIBILITY_KEY);

        super.onViewCreated(view, savedInstanceState);


        TabLayoutHelper tabLayoutHelper = new TabLayoutHelper(tabLayout, viewPager);

        tabLayoutHelper.setAutoAdjustTabModeEnabled(true);
    }

    @Override
    public void setModelView() {
        setModelView(this);
    }

    @Override
    protected List<AbsPegContentFragment> createFragmentsContentList() {
        List<AbsPegContentFragment> votersFragments = new ArrayList<>();

        CommentFragment commentFragment = CommentFragment.newInstance(possibilityDTO.postDTO);
        commentFragment.setImageResource(R.drawable.comment);
        commentFragment.setTitle(getString(R.string.comments));

        commentFragment.setRecyclerViewAdapterCallback(notifyFeedbackActionChange);

        for (PostFeedbackPossibilityDTO possibilityDTO: this.possibilityDTO.postDTO.postFeedbackPossibilities) {
            PostFeedbackFragment postFeedbackFragment = PostFeedbackFragment.newInstance(possibilityDTO);
            postFeedbackFragment.setRecyclerViewAdapterCallback(notifyFeedbackActionChange);
            postFeedbackFragment.setTitle(possibilityDTO.name);
            votersFragments.add(postFeedbackFragment);
        }

        votersFragments.add(commentFragment);

        if(feedbackActionDialogType == FeedbackActionDialogType.COMMENTS) {//if click only on comment
            viewPagerCurrentItem = possibilityDTO.postDTO.postFeedbackPossibilities.size();
        } else {
            viewPagerCurrentItem = possibilityDTO.postDTO.postFeedbackPossibilities.indexOf(possibilityDTO);
        }

        return votersFragments;
    }


    @Override
    protected void initTabColors() {
        int color = ContextCompat.getColor(getActivity(), R.color.non_clickable_content);

        if(clickOrigin == AbsPostsFeedViewModel.FeedbackActionClickOrigin.FEEDBACK_ACTION) {
            color = ContextCompat.getColor(getActivity(), R.color.tab_notification_underline);
        } else if (clickOrigin == AbsPostsFeedViewModel.FeedbackActionClickOrigin.HOME) {
            color = ContextCompat.getColor(getActivity(), R.color.tab_home_underline);
        } else if (clickOrigin == AbsPostsFeedViewModel.FeedbackActionClickOrigin.PROFILE) {
            color = ContextCompat.getColor(getActivity(), R.color.tab_profile_underline);
        }
        int normalColor = ContextCompat.getColor(getActivity(), R.color.tab_unselected_text);
        tabLayout.setTabTextColors(normalColor, color);
        tabLayout.setSelectedTabIndicatorColor(color);
    }

    @Override
    protected int getViewPagerCurrentItem() {
        return viewPagerCurrentItem;
    }

    @Nullable
    @Override
    public Class<AbsTabsContainerViewModel<ITabsContainerViewCallback>> getViewModelClass() {
        return (Class<AbsTabsContainerViewModel<ITabsContainerViewCallback>>) ((Class) AbsTabsContainerViewModel.class);
    }

    public void setNotifyFeedbackActionChange(AbsPostsFeedFragment.INotifyFeedbackActionChange notifyFeedbackActionChange) {
        this.notifyFeedbackActionChange = notifyFeedbackActionChange;
    }

    public enum FeedbackActionDialogType {
        POST_FEEDBACK, COMMENTS
    }
}
