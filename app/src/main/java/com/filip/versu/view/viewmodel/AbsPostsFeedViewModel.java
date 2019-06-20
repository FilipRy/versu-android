package com.filip.versu.view.viewmodel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.filip.versu.R;
import com.filip.versu.controller.impl.PostCreateDeleteController;
import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.Location;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.dto.PostFeedbackPossibilityDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.model.view.abs.AbsPostViewModel;
import com.filip.versu.service.IPostService;
import com.filip.versu.service.IUserSession;
import com.filip.versu.service.helper.GlobalModelVersion;
import com.filip.versu.service.impl.PostService;
import com.filip.versu.service.impl.UserSession;
import com.filip.versu.view.fragment.AbsPostsFeedFragment;
import com.filip.versu.view.fragment.FollowingsFragment;
import com.filip.versu.view.viewmodel.callback.IPostsFeedViewModel;

import java.util.List;


/**
 * This view model provides the functionality for loading likes/dislikes/comments.
 *
 * @param <T>
 * @param <K>
 */
public abstract class AbsPostsFeedViewModel<T extends AbsPostViewModel, K extends IPostsFeedViewModel.IPostsFeedViewCallback<T>>
        extends AbsRefreshablePageableViewModel<K, T>
        implements IPostsFeedViewModel<T>, AbsPostsFeedFragment.INotifyFeedbackActionChange {

    protected IUserSession userSession = UserSession.instance();
    protected IPostService postService = PostService.instance();

    public enum FeedbackActionClickOrigin {
        HOME, PROFILE, FEEDBACK_ACTION
    }

    private ShoppingItemStateUpdateTask shoppingItemStateUpdateTask;

    private PostCreateDeleteController postCreateDeleteController = new PostCreateDeleteController(this);

    @Override
    protected long getGlobalItemVersionTimestamp() {
        return GlobalModelVersion.getGlobalShoppingItemTimestamp();
    }

    @Override
    public void requestCommentsOfPost(PostDTO postDTO) {
        if (getView() != null) {
            getView().loadCommentsByPost(postDTO, getClickOrigin(this.getClass()));
        }
    }

    @Override
    public void requestPostFeedbackOfPossibility(PostFeedbackPossibilityDTO possibilityDTO) {
        if (getView() != null) {
            getView().loadPostFeedbackByPost(possibilityDTO, getClickOrigin(this.getClass()));
        }
    }

    private FeedbackActionClickOrigin getClickOrigin(Class<?> clazz) {
        FeedbackActionClickOrigin feedbackActionClickOrigin = FeedbackActionClickOrigin.HOME;

        if(clazz.getName() == MyProfileViewModel.class.getName()) {
            feedbackActionClickOrigin = FeedbackActionClickOrigin.PROFILE;
        } else if (clazz.getName() == MyFeedbackActionsViewModel.class.getName()) {
            feedbackActionClickOrigin = FeedbackActionClickOrigin.FEEDBACK_ACTION;
        } else if (clazz.getName() == PostsTimelineFeedViewModel.class.getName()) {
            feedbackActionClickOrigin = FeedbackActionClickOrigin.HOME;
        } else if (clazz.getName() == UserProfileViewModel.class.getName()) {
            feedbackActionClickOrigin = FeedbackActionClickOrigin.PROFILE;
        }
        return feedbackActionClickOrigin;
    }

    @Override
    public void requestPostsByFeedbackPossibilities(String[] feedbackPossibilities) {
        if (getView() != null) {
            getView().loadPostsByFeedbackPossibilities(feedbackPossibilities);
        }
    }

    @Override
    public void requestDisplayProfileOfUser(UserDTO userDTO) {
        if (getView() != null) {
            getView().displayProfileOfUserFragment(userDTO);
        }
    }

    @Override
    public void requestUserFollowers(UserDTO userDTO, FollowingsFragment.FollowingType followingType) {
        if (getView() != null) {
            getView().displayUserFollowers(userDTO, followingType);
        }
    }

    @Override
    public void requestDisplayLocationProfile(Location location) {
        if (getView() != null) {
            getView().displayLocationProfile(location);
        }
    }

    @Override
    public void onNewComments() {
        if (getView() != null) {
            getView().notifyDatasetChanged();
        }
        persistItemsToInternalStorage(lastLoadedContent);//persisting comments instantly
    }

    private int feedbackActionVersion;

    @Override
    public void onFeedbackActionCountChanged() {
        if (getView() != null) {
            getView().notifyDatasetChanged();
        }
        //Persisting items only each 3 times to save some operations.
        if (++feedbackActionVersion % 3 == 0) {
            feedbackActionVersion = 0;
            persistItemsToInternalStorage(lastLoadedContent);
        }
    }


    @Override
    public void displayUpdateChosenStateDialog(final PostFeedbackPossibilityDTO possibilityDTO) {

        AlertDialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(((Fragment) getView()).getActivity());

        builder.setTitle(R.string.update_chosen_state_dialog_title);
        builder.setMessage(R.string.update_chosen_state_dialog_text);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateChosenState(possibilityDTO);
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

    }


    private void updateChosenState(PostFeedbackPossibilityDTO possibilityDTO) {

        PostDTO originalShoppingItem = new PostDTO(possibilityDTO.postDTO);

        PostDTO postDTO = possibilityDTO.postDTO;

        if (postDTO.chosenFeedbackPossibility != null && postDTO.chosenFeedbackPossibility.equals(possibilityDTO)) {//if user unmarks a post as chosen.
            postDTO.chosenFeedbackPossibility = null;
        } else {
            postDTO.chosenFeedbackPossibility = possibilityDTO;
        }

        if (getView() != null) {
            getView().notifyDatasetChanged();
        }

        shoppingItemStateUpdateTask = new ShoppingItemStateUpdateTask(postDTO, originalShoppingItem);
        shoppingItemStateUpdateTask.execute((Void) null);

    }

    @Override
    public void reportPost(PostDTO postDTO) {
        if (getView() != null) {
            getView().postReported(postDTO);
        }
    }

    @Override
    public void sharePostWithLink(PostDTO postDTO) {

        PostDTO originalShoppingItem = new PostDTO(postDTO);

        if(getView() != null) {
            getView().sharePostWithLink(postDTO);
        }
    }

    public void executeCreateItemTask(PostDTO item, List<PostDTO> lastLoadedItems) {
        postCreateDeleteController.executeCreateItemTask(item, this.lastLoadedContent.getPageableContent());
    }

    public void executeDeleteItemTask(final PostDTO item, final int position, List<PostDTO> lastLoadedItems) {

        AlertDialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(((Fragment) getView()).getActivity());

        builder.setTitle(R.string.delete_post_dialog_title);
        builder.setMessage(R.string.delete_post_dialog_text);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                postCreateDeleteController.executeDeleteItemTask(item, position, AbsPostsFeedViewModel.this.lastLoadedContent.getPageableContent());
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

    }

    public void onCreateOperationSuccess(PostDTO createdItem) {
        persistItemsToInternalStorage(lastLoadedContent);
    }

    public void onDeleteOperationSuccess(PostDTO deletedItem) {
        persistItemsToInternalStorage(lastLoadedContent);
    }

    public void displayCreateDeleteControllerError(String errorMsg) {
        if (getView() != null) {
            Fragment fragment = (Fragment) getView();
            Toast.makeText(fragment.getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
        }
    }

    public void addItemToViewAdapter(PostDTO item) {
        if (getView() != null) {
            getView().addItemToViewAdapter(item);
        }
    }

    public void addItemToViewAdapter(PostDTO item, int position) {
        if (getView() != null) {
            getView().addItemToViewAdapter(item, position);
        }
    }

    public void removeItemFromViewAdapter(PostDTO item) {
        if (getView() != null) {
            getView().removeItemFromViewAdapter(item);
        }
    }


    @Override
    public void updateViewModelVersionNumber(PostDTO updatedItem) {
        GlobalModelVersion.refreshGlobalShoppingItemTimestamp();
        lastLoadedItemsLastRefresh = GlobalModelVersion.getGlobalShoppingItemTimestamp();
    }

    /**
     * This asynctask update the "buy" state of the ShoppingItem.
     */
    class ShoppingItemStateUpdateTask extends AbsAsynchronTask<PostDTO> {

        private PostDTO postDTO, originalShoppingItem;

        /**
         * @param postDTO              - shopping item, which contains the information to update
         * @param originalShoppingItem - shopping item before update (used for rollbacking operation)
         */
        public ShoppingItemStateUpdateTask(PostDTO postDTO, PostDTO originalShoppingItem) {
            this.postDTO = postDTO;
            this.originalShoppingItem = originalShoppingItem;
        }

        @Override
        protected PostDTO asynchronOperation() throws ServiceException {
            PostDTO postDTOCopy = PostService.instance().createCopyForSerialization(postDTO);
            return postService.update(postDTOCopy);
        }

        @Override
        protected void onPostExecuteSuccess(PostDTO item) {
            super.onPostExecuteSuccess(item);



            //not needed to update global version (timestamp) of shopping item, because the only place in the app where I can see @updated shopping item is my profile.
            //does not replacing postDTO by updated because postDTO can contain more information (last comments, likes count ...)
            persistItemsToInternalStorage(lastLoadedContent);
        }

        @Override
        protected void onPostExecuteError(PostDTO item) {
            super.onPostExecuteError(item);
            /**
             * reverting state
             */

            postDTO.chosenFeedbackPossibility = originalShoppingItem.chosenFeedbackPossibility;

            if (getView() != null) {
                getView().notifyDatasetChanged();
            }
        }

        @Override
        protected void displayErrorMessage(String errorMsg) {
            if (getView() != null) {
                Fragment fragment = (Fragment) getView();
                Toast.makeText(fragment.getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPostExecute(PostDTO updated) {
            super.onPostExecute(updated);
            shoppingItemStateUpdateTask = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            shoppingItemStateUpdateTask = null;
        }
    }

}
