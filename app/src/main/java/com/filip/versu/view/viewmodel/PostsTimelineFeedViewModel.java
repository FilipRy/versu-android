package com.filip.versu.view.viewmodel;

import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.Location;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.view.PostFeedViewModel;
import com.filip.versu.service.cache.ILocalCacheService;
import com.filip.versu.service.factory.LocalCacheFactory;
import com.filip.versu.service.helper.GlobalModelVersion;
import com.filip.versu.service.helper.Page;
import com.filip.versu.view.viewmodel.callback.IPostsTimelineFeedViewModel;
import com.filip.versu.view.viewmodel.callback.IPostsTimelineFeedViewModel.IPostsTimelineFeedViewModelCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a view model for displaying the shopping newsfeed
 */
public class PostsTimelineFeedViewModel
        extends AbsPostsTimelineFeedViewModel<PostFeedViewModel, IPostsTimelineFeedViewModelCallback<PostFeedViewModel>>
        implements IPostsTimelineFeedViewModel<PostFeedViewModel> {


    /**
     * This enum
     */
    public enum NewsfeedType {
        TIMELINE, NEAR_BY, DETAIL, HASHTAG, LOCATION
    }

    private NewsfeedType newsfeedType;
    /**
     * ShoppingItemID is used if newfeedType == DETAIL, to know which shopping item to show.
     */
    private Long shoppingItemID;
    private String[] feedbackPossibilities;
    private Location location;

    @Override
    public PostFeedViewModel createInstance() {
        return new PostFeedViewModel();
    }

    @Override
    public ILocalCacheService<PostFeedViewModel> getLocalCache() {
        return LocalCacheFactory.createForShoppingItems();
    }

    @Override
    public PostFeedViewModel retrieveItemsFromBackend(Page<Long> page) throws ServiceException {
        PostFeedViewModel postFeedViewModel = new PostFeedViewModel();
        if (newsfeedType == NewsfeedType.TIMELINE) {
            postFeedViewModel.feedItems = postService.findActiveForUserByTime(userSession.getLogedInUser().getId(), page);
            return postFeedViewModel;
        } else if (newsfeedType == NewsfeedType.NEAR_BY) {
            postFeedViewModel.feedItems = postService.findNewsfeedByLocation(userSession.getLogedInUser().getId(), userSession.getLogedInUser().myLocation, page);
            return postFeedViewModel;
        } else if (newsfeedType == NewsfeedType.DETAIL) {

            List<PostDTO> postDTOs = new ArrayList<>();

            if(page.getLastLoadedID() == null) {
                PostDTO postDTO = postService.getShoppingItem(shoppingItemID);
                postDTOs.add(postDTO);
            }

            postFeedViewModel.feedItems = postDTOs;
            return postFeedViewModel;
        } else if (newsfeedType == NewsfeedType.HASHTAG) {

            if (page.getLastLoadedID() != null) {// we are loading page n, n > 1
                int pageNr = page.getPageNr();
                page.setPageNr(++pageNr);
            }

            postFeedViewModel.feedItems = postService.findByPossibilityNames(feedbackPossibilities, page);
            return postFeedViewModel;


        } else if (newsfeedType == NewsfeedType.LOCATION) {
            postFeedViewModel.feedItems = postService.findByLocation(location, page);
            return postFeedViewModel;
        }


        else {
            throw new UnsupportedOperationException();
        }
    }




    @Override
    protected boolean shouldRefreshDiskCache() {
        if (newsfeedType == NewsfeedType.TIMELINE || newsfeedType == NewsfeedType.NEAR_BY) {
            return super.shouldRefreshDiskCache();
        } else {//not persisting details in internal storage
            return true;
        }
    }

    @Override
    public PostFeedViewModel retrieveItemsFromInternalStorage() {
        if (newsfeedType == NewsfeedType.TIMELINE || newsfeedType == NewsfeedType.NEAR_BY) {
            return super.retrieveItemsFromInternalStorage();
        }
        return new PostFeedViewModel();//not using internal storage for details
    }

    @Override
    public void persistItemsToInternalStorage(PostFeedViewModel item) {
        if (newsfeedType == NewsfeedType.TIMELINE || newsfeedType == NewsfeedType.NEAR_BY) {
            super.persistItemsToInternalStorage(item);
        } else {
            return;//not persisting details
        }
    }

    @Override
    public String getInternalStorageKey() {
        return newsfeedType.toString().toLowerCase();
    }

    @Override
    public String getLastTimeRefreshInternalStorageKey() {
        return MyProfileViewModel.class.getSimpleName().toLowerCase() + newsfeedType.toString().toLowerCase();
    }

    @Override
    public void setDependencies(NewsfeedType newsfeedType, Long shoppingItemID, String[] feedbackPossiblities, Location location) {
        this.newsfeedType = newsfeedType;
        this.shoppingItemID = shoppingItemID;
        this.feedbackPossibilities = feedbackPossiblities;
        this.location = location;
    }



    @Override
    public void updateViewModelVersionNumber(PostDTO updatedItem) {
        GlobalModelVersion.refreshGlobalShoppingItemTimestamp();
        lastLoadedItemsLastRefresh = GlobalModelVersion.getGlobalShoppingItemTimestamp();
    }

}
