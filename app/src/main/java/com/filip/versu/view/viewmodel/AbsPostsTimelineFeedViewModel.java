package com.filip.versu.view.viewmodel;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.dto.PostFeedbackPossibilityDTO;
import com.filip.versu.model.dto.PostFeedbackVoteDTO;
import com.filip.versu.model.dto.abs.AbsBaseEntityWithOwnerDTO;
import com.filip.versu.model.view.abs.AbsPostViewModel;
import com.filip.versu.service.helper.GlobalModelVersion;
import com.filip.versu.service.impl.PostFeedbackService;
import com.filip.versu.view.viewmodel.callback.IPostsTimelineFeedViewModel;
import com.filip.versu.view.viewmodel.callback.IPostsTimelineFeedViewModel.IPostsTimelineFeedViewModelCallback;


/**
 * This view model provides the functionality for liking/disliking/commeting shopping items in newsfeed.
 *
 * @param <T>
 * @param <C>
 */
public abstract class AbsPostsTimelineFeedViewModel<T extends AbsPostViewModel, C extends IPostsTimelineFeedViewModelCallback<T>>
        extends AbsPostsFeedViewModel<T, C>
        implements IPostsTimelineFeedViewModel<T> {


    private void addToPostItemTask(AbsFeedbackActionTask task, PostDTO post) {
        if (post.absFeedbackActionTask == null) {
            post.absFeedbackActionTask = task;
            post.absFeedbackActionTask.execute();
        } else {
            post.absFeedbackActionTask = task;
        }
    }

    @Override
    public void votingForFeedback(PostFeedbackPossibilityDTO possibility) {

        PostDTO postDTO = possibility.postDTO;

        //remove operation
        if (postDTO.myPostFeedback != null && postDTO.myPostFeedback.feedbackPossibilityDTO.equals(possibility)) {
            runRemoveVoteForPossibility(possibility);
        } else {//create operation
            runCreateVoteForPossibility(possibility);
        }

        getView().notifyDatasetChanged();
    }

    /**
     *
     * @param possibilityDTO
     */
    protected void runRemoveVoteForPossibility(PostFeedbackPossibilityDTO possibilityDTO) {

        PostDTO postDTO = possibilityDTO.postDTO;

        //remove my vote before voting for another possibility
        PostFeedbackVoteDTO toRemove = new PostFeedbackVoteDTO(possibilityDTO.postDTO.myPostFeedback, false);

        removeVoteFromPost(postDTO.myPostFeedback);


        PostFeedbackTask postFeedbackTask = new PostFeedbackTask(toRemove, null, possibilityDTO.postDTO);
        addToPostItemTask(postFeedbackTask, postDTO);
    }

    /**
     * prepares to create vote for possibility @possibilityDTO
     * @param possibilityDTO
     */
    protected void runCreateVoteForPossibility(PostFeedbackPossibilityDTO possibilityDTO) {
        PostFeedbackVoteDTO postFeedbackVoteDTO = new PostFeedbackVoteDTO();
        postFeedbackVoteDTO.owner = userSession.getLogedInUser();
        postFeedbackVoteDTO.feedbackPossibilityDTO = possibilityDTO;

        PostDTO postDTO = possibilityDTO.postDTO;

        PostFeedbackVoteDTO toRemove = null;
        if (postDTO.myPostFeedback != null) {//there is already a vote by this user
            toRemove = new PostFeedbackVoteDTO(postDTO.myPostFeedback, false);
            removeVoteFromPost(postDTO.myPostFeedback);
        }

        addVoteToPost(postFeedbackVoteDTO);

        PostFeedbackTask postFeedbackTask = new PostFeedbackTask(toRemove, postFeedbackVoteDTO, postDTO);

        addToPostItemTask(postFeedbackTask, postDTO);
    }

    protected void addVoteToPost(PostFeedbackVoteDTO postFeedbackVoteDTO) {

        postFeedbackVoteDTO.feedbackPossibilityDTO.count++;

        postFeedbackVoteDTO.feedbackPossibilityDTO.postDTO.myPostFeedback = postFeedbackVoteDTO;

    }

    protected void removeVoteFromPost(PostFeedbackVoteDTO postFeedbackVoteDTO) {
        PostDTO postDTO = postFeedbackVoteDTO.feedbackPossibilityDTO.postDTO;

        postDTO.myPostFeedback = null;

        postFeedbackVoteDTO.feedbackPossibilityDTO.count--;
    }


    public abstract class AbsFeedbackActionTask<T extends AbsBaseEntityWithOwnerDTO> extends AbsAsynchronTask<T> {

        /**
         * a feedback action, which was deleted at create. (e.g. dislike was deleted if like was created, e.g. like was deleted if dislikes was deleted)
         */
        protected T feedbackActionDelete;
        /**
         * a feedback action which is to create
         */
        protected T feedbackActionCreate;

        protected PostDTO post;

        /**
         * @param feedbackActionDelete
         * @param feedbackActionCreate
         * @param post                 - I need to pass post here, because if feedbackActionCreate == null,
         *                             then feedbackActionDelete contains only a copy of post from lastLoadedContent ->
         *                             this causes that the copy's absFeedbackAction task is set to null after execution,
         *                             but post (from lastLoadedContent)'s task stays the same.
         */
        public AbsFeedbackActionTask(T feedbackActionDelete, T feedbackActionCreate, PostDTO post) {
            this.feedbackActionDelete = feedbackActionDelete;
            this.feedbackActionCreate = feedbackActionCreate;

            if (feedbackActionCreate == null && feedbackActionDelete == null) {
                throw new IllegalArgumentException();
            }

            this.post = post;

        }

        public void execute() {
            super.execute();
        }

        protected abstract T createFeedbackAction() throws ServiceException;

        protected abstract T deleteFeedbackAction() throws ServiceException;


        @Override
        protected void displayErrorMessage(String errorMsg) {
            if (getView() != null) {
                Fragment fragment = (Fragment) getView();
                Toast.makeText(fragment.getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected T asynchronOperation() throws ServiceException {
            if (feedbackActionCreate == null) {//delete operation
                feedbackActionDelete = deleteFeedbackAction();
            } else {//create operation
                feedbackActionCreate = createFeedbackAction();
            }
            return feedbackActionCreate;
        }

        @Override
        protected void onPostExecuteSuccess(T item) {
            super.onPostExecuteSuccess(item);
            onFeedActExecuteSuccess();
            persistItemsToInternalStorage(lastLoadedContent);
        }

        @Override
        protected void onPostExecuteError(T item) {
            super.onPostExecuteError(item);
            onFeedActExecuteError();
            if (getView() != null) {
                getView().notifyDatasetChanged();
            }
        }

        protected void onFeedActExecuteSuccess() {
            //setting the last time of shopping item's update (in this case: create feedback action) to current time.
            GlobalModelVersion.refreshGlobalShoppingItemTimestamp();
            lastLoadedItemsLastRefresh = GlobalModelVersion.getGlobalShoppingItemTimestamp();
            //delete operation
            if (feedbackActionCreate == null) {
                onPostExecuteDeleteSuccess();
            } else {//create operation
                onPostExecuteCreateSuccess();
            }
        }

        protected void onPostExecuteDeleteSuccess() {
            if (getMyCreateFeedbackAction() == null && getMyDeleteFeedbackAction() == null) {
                //OK
                removeFeedbackActionTask();
            } else {
                executeTaskOfShoppingItem();
            }
        }

        protected void onPostExecuteCreateSuccess() {
            //create operation
            if (getMyCreateFeedbackAction() != null && getMyCreateFeedbackAction().equals(feedbackActionCreate) && getMyDeleteFeedbackAction() == null) {
                //OK
                removeFeedbackActionTask();
            } else {
                executeTaskOfShoppingItem();
            }

        }

        protected void onFeedActExecuteError() {

            removeFeedbackActionTask();
            //create operation
            if (feedbackActionCreate != null) {
                onCreateErrorRollback();
            }
            //delete operation
            else if (feedbackActionDelete != null) {
                onDeleteErrorRollback();
            }

        }

        protected void onCreateErrorRollback() {
            removeFeedbackActionCreate();
            if (feedbackActionDelete != null) {
                addFeedbackActionDelete();
            }
        }

        protected void onDeleteErrorRollback() {
            addFeedbackActionDelete();
        }

        private void executeTaskOfShoppingItem() {
            AbsFeedbackActionTask feedbackActionTask = getFeedbackActionTask();
            if (feedbackActionTask != null) {
                feedbackActionTask.execute();
            }
        }

        protected abstract void addFeedbackActionDelete();

        protected abstract void removeFeedbackActionCreate();

        protected abstract T getMyCreateFeedbackAction();

        /**
         * returns null on voting post
         *
         * @return
         */
        protected abstract T getMyDeleteFeedbackAction();

        protected abstract AbsFeedbackActionTask getFeedbackActionTask();

        protected abstract void removeFeedbackActionTask();


        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

    }

    public class PostFeedbackTask extends AbsFeedbackActionTask<PostFeedbackVoteDTO> {

        /**
         * @param feedbackActionDelete
         * @param feedbackActionCreate
         * @param post         - I need to pass post here, because if feedbackActionCreate == null,
         *                             then feedbackActionDelete contains only a copy of post from lastLoadedContent ->
         *                             this causes that the copy's absFeedbackAction task is set to null after execution,
         */
        public PostFeedbackTask(PostFeedbackVoteDTO feedbackActionDelete, PostFeedbackVoteDTO feedbackActionCreate, PostDTO post) {
            super(feedbackActionDelete, feedbackActionCreate, post);
        }

        @Override
        protected PostFeedbackVoteDTO createFeedbackAction() throws ServiceException {
            return PostFeedbackService.instance().create(feedbackActionCreate);
        }

        @Override
        protected PostFeedbackVoteDTO deleteFeedbackAction() throws ServiceException {
            PostFeedbackService.instance().delete(feedbackActionDelete.getId());
            return feedbackActionDelete;
        }

        @Override
        protected void addFeedbackActionDelete() {
            addVoteToPost(feedbackActionDelete);
        }

        @Override
        protected void removeFeedbackActionCreate() {
            removeVoteFromPost(feedbackActionCreate);
        }

        @Override
        protected PostFeedbackVoteDTO getMyCreateFeedbackAction() {
            return post.myPostFeedback;
        }

        @Override
        protected PostFeedbackVoteDTO getMyDeleteFeedbackAction() {
            return null;
        }

        @Override
        protected AbsFeedbackActionTask getFeedbackActionTask() {
            return post.absFeedbackActionTask;
        }

        @Override
        protected void removeFeedbackActionTask() {
            post.absFeedbackActionTask = null;
        }
    }

}