package com.filip.versu.view.viewmodel;

import android.support.v4.app.Fragment;

import com.filip.versu.R;
import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.PostFeedbackPossibilityDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.model.view.PostFeedbackFeedViewModel;
import com.filip.versu.service.IPostFeedbackService;
import com.filip.versu.service.cache.ILocalCacheService;
import com.filip.versu.service.factory.LocalCacheFactory;
import com.filip.versu.service.helper.Page;
import com.filip.versu.service.impl.PostFeedbackService;
import com.filip.versu.view.fragment.AbsPostsFeedFragment;
import com.filip.versu.view.viewmodel.callback.IPostFeedbackViewModel;


public class PostFeedbackViewModel extends AbsRefreshablePageableViewModel<IPostFeedbackViewModel.IPostFeedbackViewModelCallback, PostFeedbackFeedViewModel>
        implements IPostFeedbackViewModel {


    private PostFeedbackPossibilityDTO possibilityDTO;

    private AbsPostsFeedFragment.INotifyFeedbackActionChange notifyFeedbackActionChange;
    private IPostFeedbackService postFeedbackService = PostFeedbackService.instance();


    @Override
    public PostFeedbackFeedViewModel createInstance() {
        return new PostFeedbackFeedViewModel();
    }

    @Override
    public String getInternalStorageKey() {
        return possibilityDTO.postDTO.getId() + possibilityDTO.getId().toString();
    }

    @Override
    public void onItemsSuccessLoadedFromBackend(PostFeedbackFeedViewModel loadedItem, Page<Long> page) {
        super.onItemsSuccessLoadedFromBackend(loadedItem, page);

        if (page.getLastLoadedID() == null) {


            if (possibilityDTO.count > loadedItem.getSize()) {//if e.g we loadedItem is only one page of all items (feedback votes)
                possibilityDTO.count = loadedItem.getSize();

                if (notifyFeedbackActionChange != null) {//notifying parent, because feedback action's count might have changed.
                    notifyFeedbackActionChange.onFeedbackActionCountChanged();
                }
            }

        }

    }

    @Override
    protected boolean shouldRefreshDiskCache() {
        boolean shouldRefresh = super.shouldRefreshDiskCache();

        if (shouldRefresh) {
            return true;
        }

        int votesCount = possibilityDTO.count;

        /**
         * If count of loaded items (from cache) is different from the loaded votesCount at post -> cache should be refreshed.
         */
        if (lastLoadedContent != null && lastLoadedContent.feedItems.size() != votesCount) {
            return true;
        }

        return false;

    }

    @Override
    protected String getCustomNothingToShowMessage() {
        Fragment fragment = (Fragment) getView();
        if (fragment != null) {
            return fragment.getString(R.string.nothing_to_show_in_post_votes) + " " + possibilityDTO.name;
        }
        return "";
    }

    @Override
    public String getLastTimeRefreshInternalStorageKey() {
        return PostFeedbackViewModel.class.getSimpleName().toLowerCase() + possibilityDTO.getId();
    }

    @Override
    public ILocalCacheService<PostFeedbackFeedViewModel> getLocalCache() {
        return LocalCacheFactory.createForPostFeedback();
    }

    @Override
    public PostFeedbackFeedViewModel retrieveItemsFromBackend(Page<Long> page) throws ServiceException {
        PostFeedbackFeedViewModel postFeedbackFeedViewModel = new PostFeedbackFeedViewModel();
        postFeedbackFeedViewModel.feedItems = postFeedbackService.findByFeedbackPossibility(possibilityDTO.getId(), page);
        return postFeedbackFeedViewModel;
    }


    @Override
    public void setDependencies(PostFeedbackPossibilityDTO possibilityDTO, AbsPostsFeedFragment.INotifyFeedbackActionChange notifyFeedbackActionChange) {
        this.possibilityDTO = possibilityDTO;
        this.notifyFeedbackActionChange = notifyFeedbackActionChange;
    }

    @Override
    public void requestDisplayProfileOfUser(UserDTO userDTO) {
        if (getView() != null) {
            getView().displayProfileOfUserFragment(userDTO);
        }
    }
}
