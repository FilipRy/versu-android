package com.filip.versu.view.viewmodel;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.filip.versu.R;
import com.filip.versu.controller.impl.CommentCreateDeleteController;
import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.CommentDTO;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.model.dto.abs.AbsBaseEntityDTO;
import com.filip.versu.model.view.CommentsFeedViewModel;
import com.filip.versu.service.ICommentService;
import com.filip.versu.service.IUserSession;
import com.filip.versu.service.cache.ILocalCacheService;
import com.filip.versu.service.factory.LocalCacheFactory;
import com.filip.versu.service.helper.GlobalModelVersion;
import com.filip.versu.service.helper.Page;
import com.filip.versu.service.impl.CommentService;
import com.filip.versu.service.impl.UserSession;
import com.filip.versu.view.fragment.AbsPostsFeedFragment;
import com.filip.versu.view.viewmodel.callback.ICommentViewModel;
import com.filip.versu.view.viewmodel.callback.ICommentViewModel.ICommentViewCallback;
import com.filip.versu.view.viewmodel.callback.IRefreshablePageableViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CommentViewModel extends AbsRefreshablePageableViewModel<ICommentViewCallback, CommentsFeedViewModel>
        implements ICommentViewModel {

    public static final int FEED_COMMENT_SIZE = 2;

    private PostDTO postDTO;
    private IUserSession IUserSession = UserSession.instance();
    private ICommentService commentService = CommentService.instance();

    private CommentCreateDeleteController commentCreateDeleteController;

    private AbsPostsFeedFragment.INotifyFeedbackActionChange notifyFeedbackActionChange;


    @Override
    protected AbsBaseEntityDTO<Long> getLastItemOnPage() {

        List lastContent = lastLoadedContent.getPageableContent();

        if (lastContent.size() == 0) {
            return null;
        }

        AbsBaseEntityDTO<Long> lastItem = (AbsBaseEntityDTO<Long>) lastContent.get(0);

        return lastItem;
    }

    @Override
    protected void addNextPageItems(CommentsFeedViewModel nextPage) {
        if (nextPage.feedItems.size() == 0) {//there is nothing new
            if (getView() != null) {
                getView().hideLoadMoreBtn();
            }
        }
        lastLoadedContent.getPageableContent().addAll(0, nextPage.getPageableContent());
    }

    @Override
    public void setDependencies(PostDTO postDTO) {
        this.postDTO = postDTO;
        this.commentCreateDeleteController = new CommentCreateDeleteController(this);
    }

    @Override
    protected String getCustomNothingToShowMessage() {
        Fragment fragment = (Fragment) getView();
        if (fragment != null) {
            return fragment.getString(R.string.nothing_to_show_in_comment);
        }
        return "";
    }

    @Override
    public void executeCreateItemTask(CommentDTO item, List<CommentDTO> lastLoadedItems) {
        commentCreateDeleteController.executeCreateItemTask(item, this.lastLoadedContent.getPageableContent());
    }

    @Override
    public void executeDeleteItemTask(CommentDTO item, int position, List<CommentDTO> lastLoadedItems) {
        commentCreateDeleteController.executeDeleteItemTask(item, position, this.lastLoadedContent.getPageableContent());
    }

    @Override
    public void createCommentTask(String content) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.content = content;
        commentDTO.owner = IUserSession.getLogedInUser();
        commentDTO.postDTO = postDTO;
        commentDTO.timestamp = System.currentTimeMillis();

        commentCreateDeleteController.executeCreateItemTask(commentDTO, this.lastLoadedContent.getPageableContent());
    }


    @Override
    public void onCreateOperationSuccess(CommentDTO createdItem) {
        lastErrorMessage = "";
        if (getView() != null) {
            getView().hideErrorMessage();
        }
        persistItemsToInternalStorage(lastLoadedContent);
        postDTO.comments.add(createdItem);
        if (postDTO.comments.size() > FEED_COMMENT_SIZE) {
            postDTO.comments.remove(0);
        }
        if (notifyFeedbackActionChange != null) {
            notifyFeedbackActionChange.onNewComments();
        }
    }

    @Override
    public void onDeleteOperationSuccess(CommentDTO deletedItem) {
        persistItemsToInternalStorage(lastLoadedContent);
        postDTO.comments.remove(deletedItem);
        if (notifyFeedbackActionChange != null) {
            notifyFeedbackActionChange.onNewComments();
        }
    }

    @Override
    public void updateViewModelVersionNumber(CommentDTO updatedItem) {
        GlobalModelVersion.refreshGlobalShoppingItemTimestamp();
    }

    @Override
    public void displayCreateDeleteControllerError(String errorMsg) {
        if (getView() != null) {
            Fragment fragment = (Fragment) getView();
            Toast.makeText(fragment.getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void addItemToViewAdapter(CommentDTO item) {
        if (getView() != null) {
            getView().addItemToViewAdapter(item);
        }
    }

    @Override
    public void addItemToViewAdapter(CommentDTO item, int position) {
        if (getView() != null) {
            getView().addItemToViewAdapter(item, position);
        }
    }

    @Override
    public void removeItemFromViewAdapter(CommentDTO item) {
        if (getView() != null) {
            getView().removeItemFromViewAdapter(item);
        }
    }

    @Override
    public CommentsFeedViewModel createInstance() {
        return new CommentsFeedViewModel();
    }

    @Override
    public String getInternalStorageKey() {
        return postDTO.getId().toString();
    }

    @Override
    public void onItemsSuccessLoadedFromInternalStorage(CommentsFeedViewModel loadedItem) {
        super.onItemsSuccessLoadedFromInternalStorage(loadedItem);

        if (getView() != null && loadedItem.getSize() < IRefreshablePageableViewModel.PAGE_SIZE) {
            getView().hideLoadMoreBtn();

            if (loadedItem.getSize() > 0) {
                getView().scrollToLastItem();
            }

        }

    }

    @Override
    public void onItemsSuccessLoadedFromBackend(CommentsFeedViewModel loadedItem, Page<Long> page) {
        super.onItemsSuccessLoadedFromBackend(loadedItem, page);


        if (page.getLastLoadedID() == null) {


            List<CommentDTO> lastComments = Collections.synchronizedList(new ArrayList<CommentDTO>());
            if (loadedItem.getSize() == FEED_COMMENT_SIZE - 1) {
                for (int i = 0; i < FEED_COMMENT_SIZE - 1; i++) {
                    lastComments.add((CommentDTO) loadedItem.getPageableContent().get(i));
                }
            } else if (loadedItem.getSize() >= FEED_COMMENT_SIZE) {
                for (int i = loadedItem.getSize() - FEED_COMMENT_SIZE; i < loadedItem.getSize(); i++) {
                    lastComments.add((CommentDTO) loadedItem.getPageableContent().get(i));
                }
            }

            for (int i = 0; i < lastComments.size(); i++) {
                CommentDTO commentDTO = lastComments.get(i);
                //creating comment copy, because in CommentViewModel we don't need the reference to shopping item.
                commentDTO = new CommentDTO(commentDTO, true);
                commentDTO.postDTO = postDTO;
                lastComments.set(i, commentDTO);
            }

            synchronized (postDTO.comments) {
                postDTO.comments = lastComments;
            }

            if (notifyFeedbackActionChange != null) {
                notifyFeedbackActionChange.onNewComments();
            }

            if (getView() != null) {
                getView().scrollToLastItem();

                if (loadedItem.getSize() < IRefreshablePageableViewModel.PAGE_SIZE) {
                    getView().hideLoadMoreBtn();
                }

            }

        }

    }

    @Override
    public String getLastTimeRefreshInternalStorageKey() {
        return CommentViewModel.class.getSimpleName().toLowerCase() + postDTO.getId();
    }

    @Override
    public ILocalCacheService<CommentsFeedViewModel> getLocalCache() {
        return LocalCacheFactory.createForComments();
    }

    @Override
    public CommentsFeedViewModel retrieveItemsFromBackend(Page<Long> page) throws ServiceException {
        List<CommentDTO> commentDTOs = commentService.findByPost(postDTO.getId(), page);
        CommentsFeedViewModel commentsFeedViewModel = new CommentsFeedViewModel();
        commentsFeedViewModel.feedItems = commentDTOs;
        return commentsFeedViewModel;
    }

    @Override
    public void setRecyclerViewAdapterCallback(AbsPostsFeedFragment.INotifyFeedbackActionChange notifyFeedbackActionChangeCallback) {
        this.notifyFeedbackActionChange = notifyFeedbackActionChangeCallback;
    }

    @Override
    public void requestDisplayProfileOfUser(UserDTO userDTO) {
        if (getView() != null) {
            getView().displayProfileOfUserFragment(userDTO);
        }
    }
}
