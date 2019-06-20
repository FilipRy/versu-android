package com.filip.versu.view.viewmodel;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;

import com.filip.versu.R;
import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.Location;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.dto.PostFeedbackPossibilityDTO;
import com.filip.versu.model.dto.PostFeedbackVoteDTO;
import com.filip.versu.model.dto.abs.AbsBaseEntityDTO;
import com.filip.versu.model.view.PostFeedViewModel;
import com.filip.versu.service.IPostFeedbackService;
import com.filip.versu.service.cache.ILocalCacheService;
import com.filip.versu.service.factory.LocalCacheFactory;
import com.filip.versu.service.helper.Page;
import com.filip.versu.service.impl.PostFeedbackService;
import com.filip.versu.view.viewmodel.callback.IMyFeedbackActionViewModel;

import java.util.HashMap;
import java.util.List;

/**
 * This view model takes care of displaying the liked/favourite items by me.
 */
public class MyFeedbackActionsViewModel
        extends AbsPostsTimelineFeedViewModel<PostFeedViewModel, IMyFeedbackActionViewModel.IMyFeedbackActionViewModelCallback>
        implements IMyFeedbackActionViewModel {

    private MyFeedbackActionFragmentType myFeedbackActionFragmentType;

    private IPostFeedbackService postFeedbackService = PostFeedbackService.instance();

    /**
     * This hashMap stores the positions of ShoppingItems in ListView.
     */
    private HashMap<Long, Integer> itemListPositions = new HashMap<>();

    public enum MyFeedbackActionFragmentType {
        FAVOURITES, POST_FEEDBACK
    }

    @Override
    protected void runRemoveVoteForPossibility(final PostFeedbackPossibilityDTO possibilityDTO) {
        if (myFeedbackActionFragmentType == MyFeedbackActionFragmentType.POST_FEEDBACK) {
            AlertDialog dialog = null;
            AlertDialog.Builder builder = new AlertDialog.Builder(((Fragment) getView()).getActivity());

            builder.setTitle(R.string.delete_post_feedback_dialog_title);
            builder.setMessage(R.string.delete_post_feedback_dialog_text);

            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MyFeedbackActionsViewModel.super.runRemoveVoteForPossibility(possibilityDTO);
                }
            });

            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });

            dialog = builder.create();
            dialog.show();

        } else {
            super.runRemoveVoteForPossibility(possibilityDTO);
        }
    }

    @Override
    protected void removeVoteFromPost(PostFeedbackVoteDTO postFeedbackVoteDTO) {
        super.removeVoteFromPost(postFeedbackVoteDTO);
        if (myFeedbackActionFragmentType == MyFeedbackActionFragmentType.POST_FEEDBACK) {

            PostDTO postDTO = postFeedbackVoteDTO.feedbackPossibilityDTO.postDTO;

            itemListPositions.put(postDTO.getId(), lastLoadedContent.getPageableContent().indexOf(postDTO));
            removeItem(postDTO);
        }
    }

    @Override
    protected void addVoteToPost(PostFeedbackVoteDTO postFeedbackVoteDTO) {
        super.addVoteToPost(postFeedbackVoteDTO);
        if (myFeedbackActionFragmentType == MyFeedbackActionFragmentType.POST_FEEDBACK) {
            PostDTO postDTO = postFeedbackVoteDTO.feedbackPossibilityDTO.postDTO;

            int position = itemListPositions.get(postDTO.getId());
            addItemToPosition(postDTO, position);
        }
    }

    protected void addItemToPosition(PostDTO item, int position) {
        lastLoadedContent.getPageableContent().add(position, item);
        if (getView() != null) {
            IMyFeedbackActionViewModelCallback callback = getView();
            callback.addItemToViewAdapter(item, position);
        }
    }

    public void removeItem(PostDTO item) {
        lastLoadedContent.getPageableContent().remove(item);
        if (getView() != null) {
            IMyFeedbackActionViewModelCallback callback = getView();
            callback.removeItemFromViewAdapter(item);
        }
    }

    @Override
    protected AbsBaseEntityDTO<Long> getLastItemOnPage() {
        List lastContent = lastLoadedContent.getPageableContent();
        int size = lastContent.size();

        if (size == 0) {
            return null;
        }

        PostFeedbackVoteDTO lastItem;
        try {
            PostDTO lastPost = (PostDTO) lastContent.get(size - 1);
            lastItem = lastPost.myPostFeedback;
        } catch (ClassCastException e) {
            lastItem = null;
        }


        return lastItem;

    }

    @Override
    public PostFeedViewModel retrieveItemsFromBackend(Page<Long> page) throws ServiceException {
        PostFeedViewModel postFeedViewModel = new PostFeedViewModel();
        if (myFeedbackActionFragmentType == MyFeedbackActionFragmentType.POST_FEEDBACK) {
            postFeedViewModel.feedItems = postFeedbackService.findPostsByUser(userSession.getLogedInUser().getId(), page);
            return postFeedViewModel;
        } else {
            throw new UnsupportedOperationException("The feedback action can display only \"votes yes\" and \"favourite\" fragments");
        }
    }

    @Override
    public ILocalCacheService getLocalCache() {
        return LocalCacheFactory.createForMyProfile();
    }

    @Override
    public PostFeedViewModel createInstance() {
        return new PostFeedViewModel();
    }

    @Override
    protected String getCustomNothingToShowMessage() {
        Fragment fragment = (Fragment) getView();
        if(fragment != null) {
            return fragment.getString(R.string.nothing_to_show_in_my_votes);
        }
        return "";
    }

    @Override
    public String getInternalStorageKey() {
        String key = MyFeedbackActionsViewModel.class.getSimpleName().toLowerCase();
        key = key + myFeedbackActionFragmentType.toString().toLowerCase();
        return key;
    }

    @Override
    public String getLastTimeRefreshInternalStorageKey() {
        String key = MyFeedbackActionsViewModel.class.getSimpleName().toLowerCase();
        key = key + myFeedbackActionFragmentType.toString().toLowerCase();
        return key;
    }

    @Override
    public void setDependencies(MyFeedbackActionFragmentType myFeedbackActionFragmentType) {
        this.myFeedbackActionFragmentType = myFeedbackActionFragmentType;
    }

    @Override
    public void setDependencies(PostsTimelineFeedViewModel.NewsfeedType newsfeedType, Long shoppingItemID, String[] feedbackPossiblities, Location location) {
        //not implemented
    }
}
