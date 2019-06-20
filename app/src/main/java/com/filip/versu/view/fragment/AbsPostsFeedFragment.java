package com.filip.versu.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import com.filip.versu.R;
import com.filip.versu.model.Location;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.dto.PostFeedbackPossibilityDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.model.view.abs.AbsPostViewModel;
import com.filip.versu.view.PostDetailActivity;
import com.filip.versu.view.fragment.parent.ItemFeedbackActionContainerFragment;
import com.filip.versu.view.viewmodel.AbsPostsFeedViewModel;
import com.filip.versu.view.viewmodel.callback.IPostsFeedViewModel;


/**
 * This fragment can load likes/dislikes/comments of shopping items.
 *
 * @param <K>
 * @param <T>
 * @param <L>
 */
public abstract class AbsPostsFeedFragment<K extends IPostsFeedViewModel.IPostsFeedViewCallback<L>, T extends AbsPostsFeedViewModel<L, K>, L extends AbsPostViewModel>
        extends AbsRefreshablePageableFragment<K, L, T>
        implements IPostsFeedViewModel.IPostsFeedViewCallback<L> {

    protected ProfileDisplayer profileDisplayer = new ProfileDisplayer();

    public interface INotifyFeedbackActionChange {

        /**
         * This method is invoked, when user loads likes -> it says to shoppingFeedViewModel, that it should persist items to intrl strg.
         */
        public void onFeedbackActionCountChanged();

        /**
         * if user created new comment or loaded comments from backend.
         */
        public void onNewComments();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void loadFeedbackActionsOfPost(PostFeedbackPossibilityDTO possibilityDTO, ItemFeedbackActionContainerFragment.FeedbackActionDialogType feedbackActionDialogType,
                                           AbsPostsFeedViewModel.FeedbackActionClickOrigin clickOrigin) {

        ItemFeedbackActionContainerFragment itemVotersContainerFragment =
                ItemFeedbackActionContainerFragment.newInstance(possibilityDTO, feedbackActionDialogType, clickOrigin);
        //TODO find another solution for the callback
        itemVotersContainerFragment.setNotifyFeedbackActionChange(getViewModel());
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.add(R.id.content_frame, itemVotersContainerFragment, ItemFeedbackActionContainerFragment.TAG).addToBackStack(null).commit();
    }


    @Override
    public void postReported(PostDTO postDTO) {
        Toast.makeText(getActivity(), "This is not implemeted yet! Hahahahah!!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void sharePostWithLink(PostDTO postDTO) {
        PostShareFragment postShareFragment = PostShareFragment.newInstance(postDTO);

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.add(R.id.content_frame, postShareFragment, PostShareFragment.TAG).addToBackStack(null).commit();
    }

    @Override
    public void loadCommentsByPost(PostDTO postDTO, AbsPostsFeedViewModel.FeedbackActionClickOrigin clickOrigin) {
        loadFeedbackActionsOfPost(postDTO.postFeedbackPossibilities.get(0), ItemFeedbackActionContainerFragment.FeedbackActionDialogType.COMMENTS, clickOrigin);
    }

    @Override
    public void loadPostFeedbackByPost(PostFeedbackPossibilityDTO possibilityDTO, AbsPostsFeedViewModel.FeedbackActionClickOrigin clickOrigin) {
        loadFeedbackActionsOfPost(possibilityDTO, ItemFeedbackActionContainerFragment.FeedbackActionDialogType.POST_FEEDBACK, clickOrigin);
    }

    @Override
    public void loadPostsByFeedbackPossibilities(String[] feedbackPossibilities) {
        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
        intent.putExtra(PostDetailActivity.POST_FEED_TYPE_KEY, PostDetailActivity.PostFeedType.HASHTAG);
        intent.putExtra(PostsTimelineFeedFragment.FEEDBACK_POSSIBLITIES_KEY, feedbackPossibilities);

        getActivity().startActivity(intent);
    }

    @Override
    public void displayProfileOfUserFragment(UserDTO userDTO) {
        profileDisplayer.displayUserProfile(userDTO, getActivity());
    }

    @Override
    public void displayLocationProfile(Location location) {
        profileDisplayer.displayLocationProfile(location, getActivity());
    }

    @Override
    public void displayUserFollowers(UserDTO userDTO, FollowingsFragment.FollowingType followingType) {
        profileDisplayer.displayUserFollowers(userDTO, followingType, getActivity());
    }

    @Override
    public void addItemToViewAdapter(PostDTO item) {
        recyclerViewAdapter.addItem(item);
    }

    @Override
    public void addItemToViewAdapter(PostDTO item, int position) {
        recyclerViewAdapter.addItemToPosition(item, position);
    }

    @Override
    public void removeItemFromViewAdapter(PostDTO item) {
        recyclerViewAdapter.removeItem(item);
    }

}
