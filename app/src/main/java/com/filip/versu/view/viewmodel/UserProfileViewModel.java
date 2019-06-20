package com.filip.versu.view.viewmodel;


import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.filip.versu.R;
import com.filip.versu.controller.impl.HackyFollowingCreateDeleteController;
import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.Location;
import com.filip.versu.model.dto.FollowingDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.model.dto.UserProfileDTO;
import com.filip.versu.model.view.UserProfileFeedViewModel;
import com.filip.versu.service.cache.ILocalCacheService;
import com.filip.versu.service.factory.LocalCacheFactory;
import com.filip.versu.service.helper.GlobalModelVersion;
import com.filip.versu.service.helper.Page;
import com.filip.versu.view.viewmodel.callback.IUserProfileViewModel;
import com.filip.versu.view.viewmodel.callback.IUserProfileViewModel.IUserProfileViewModelCallback;

import java.util.List;

public class UserProfileViewModel
        extends AbsPostsTimelineFeedViewModel<UserProfileFeedViewModel, IUserProfileViewModelCallback>
        implements IUserProfileViewModel {

    private UserDTO shopper;

    private HackyFollowingCreateDeleteController followingCreateDeleteController;

    @Override
    public UserProfileFeedViewModel retrieveItemsFromBackend(Page<Long> page) throws ServiceException {
        UserProfileDTO userProfileDTO = postService.getUserProfile(shopper.getId(), page);
        UserProfileFeedViewModel userProfileFeedViewModel = new UserProfileFeedViewModel(userProfileDTO);

        return userProfileFeedViewModel;
    }

    @Override
    protected String getCustomNothingToShowMessage() {
        Fragment fragment = (Fragment) getView();
        if(fragment != null) {
            return fragment.getString(R.string.nothing_to_show_in_my_profile);
        }
        return "";
    }

    @Override
    public UserProfileFeedViewModel createInstance() {
        return new UserProfileFeedViewModel();
    }

    @Override
    public String getInternalStorageKey() {
        return shopper.getId().toString();
    }

    @Override
    public String getLastTimeRefreshInternalStorageKey() {
        return UserProfileViewModel.class.getSimpleName().toLowerCase() + shopper.getId();
    }

    @Override
    public ILocalCacheService<UserProfileFeedViewModel> getLocalCache() {
        return LocalCacheFactory.createForUserProfile();
    }

    @Override
    public void requestDisplayProfileOfUser(UserDTO userDTO) {
        //I can display only profile of another user if I am in profile of some user.
        if(!userDTO.equals(shopper)) {
            super.requestDisplayProfileOfUser(userDTO);
        }
    }

    @Override
    public void setDependencies(UserDTO userDTO) {
        this.shopper = userDTO;
        followingCreateDeleteController = new HackyFollowingCreateDeleteController(this);
    }

    @Override
    public void executeCreateItemTask(FollowingDTO item, List<FollowingDTO> lastLoadedItems) {
        followingCreateDeleteController.executeCreateItemTask(item, lastLoadedItems);
    }

    @Override
    public void executeDeleteItemTask(FollowingDTO item, int position, List<FollowingDTO> lastLoadedItems) {
        followingCreateDeleteController.executeDeleteItemTask(item, position, lastLoadedItems);
    }

    @Override
    public void onCreateOperationSuccess(FollowingDTO createdItem) {
        GlobalModelVersion.refreshGlobalFollowingTimestamp();
        //refreshing the content, because some posts can now be visible for the user
        requestItemsFromBackend();
    }

    @Override
    public void onDeleteOperationSuccess(FollowingDTO deletedItem) {
        GlobalModelVersion.refreshGlobalFollowingTimestamp();
        //refreshing the content, because some posts are not visible to the user anymore
        requestItemsFromBackend();
    }

    @Override
    public void updateViewModelVersionNumber(FollowingDTO updatedItem) {
        GlobalModelVersion.refreshGlobalFollowingTimestamp();
        GlobalModelVersion.refreshGlobalShoppingItemTimestamp();
        lastLoadedItemsLastRefresh = GlobalModelVersion.getGlobalShoppingItemTimestamp();//geting shopping item' timestamp here, because it's more recent then following timestamp.

    }


    public void addItemToViewAdapter(FollowingDTO item) {
        lastLoadedContent.userCard.followingDTO = item;
        lastLoadedContent.userCard.followersCount++;
        if(getView() != null) {
            getView().addItemToViewAdapter(item);
        }
    }


    public void addItemToViewAdapter(FollowingDTO item, int position) {
        addItemToViewAdapter(item);
    }


    public void removeItemFromViewAdapter(FollowingDTO item) {
        lastLoadedContent.userCard.followingDTO = null;
        lastLoadedContent.userCard.followersCount--;
        if(getView() != null) {
            getView().removeItemFromViewAdapter(item);
        }
    }

    @Override
    public void setDependencies(PostsTimelineFeedViewModel.NewsfeedType newsfeedType, Long shoppingItemID, String[] feedbackPossibilities, Location location) {

    }

    @Override
    public void displayCreateDeleteControllerError(String errorMsg) {
        if(getView() != null) {
            Fragment fragment = (Fragment) getView();
            Toast.makeText(fragment.getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
        }
    }


}
