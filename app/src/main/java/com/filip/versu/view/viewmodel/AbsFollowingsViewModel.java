package com.filip.versu.view.viewmodel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.filip.versu.R;
import com.filip.versu.controller.impl.FollowingCreateDeleteController;
import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.FollowingDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.model.view.FollowingsFeedViewModel;
import com.filip.versu.service.IFollowingService;
import com.filip.versu.service.IUserSession;
import com.filip.versu.service.cache.ILocalCacheService;
import com.filip.versu.service.factory.LocalCacheFactory;
import com.filip.versu.service.helper.GlobalModelVersion;
import com.filip.versu.service.helper.Page;
import com.filip.versu.service.impl.FollowingService;
import com.filip.versu.service.impl.UserSession;
import com.filip.versu.view.fragment.FollowingsFragment;
import com.filip.versu.view.viewmodel.callback.IFollowingsViewModel;
import com.filip.versu.view.viewmodel.callback.IFollowingsViewModel.IFollowingsViewModelCallback;

import java.util.ArrayList;
import java.util.List;


public abstract class AbsFollowingsViewModel<K extends IFollowingsViewModelCallback> extends AbsRefreshablePageableViewModel<K, FollowingsFeedViewModel> implements IFollowingsViewModel {



    protected FollowingsFragment.FollowingType followingsType;
    /**
     * User whose FOLLOWERS are being loaded.
     */
    protected UserDTO userDTO;


    public static final String TAG = AbsFollowingsViewModel.class.getSimpleName();

    protected IUserSession userSession = UserSession.instance();
    protected IFollowingService followingService = FollowingService.instance();

    protected FollowingCreateDeleteController followingCreateDeleteController;

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);
        followingCreateDeleteController = new FollowingCreateDeleteController(this);
    }


    @Override
    public FollowingsFeedViewModel retrieveItemsFromInternalStorage() {
        if(userDTO.equals(userSession.getLogedInUser())) {
            return super.retrieveItemsFromInternalStorage();
        }
        //not caching
        FollowingsFeedViewModel followingsFeedViewModel = new FollowingsFeedViewModel();
        followingsFeedViewModel.feedItems = new ArrayList<>();

        return followingsFeedViewModel;

    }

    @Override
    public FollowingsFeedViewModel retrieveItemsFromBackend(Page<Long> page) throws ServiceException {
        FollowingsFeedViewModel followingsFeedViewModel = new FollowingsFeedViewModel();
        if (followingsType == FollowingsFragment.FollowingType.FOLLOWERS) {
            List<FollowingDTO> followingDTOs = followingService.listFollowers(userDTO.getId(), page);
            followingsFeedViewModel.feedItems = followingDTOs;
        } else {
            List<FollowingDTO> followingDTOs = followingService.listFollowings(userDTO.getId(), page);
            followingsFeedViewModel.feedItems = followingDTOs;
        }
        return followingsFeedViewModel;
    }

    @Override
    protected long getGlobalItemVersionTimestamp() {
        return GlobalModelVersion.getGlobalFollowingTimestamp();
    }


    @Override
    public String getInternalStorageKey() {
        return followingsType.toString().toLowerCase();
    }

    @Override
    public String getLastTimeRefreshInternalStorageKey() {
        return (FollowingsViewModel.class.getSimpleName() + followingsType.toString()).toLowerCase();
    }


    @Override
    public ILocalCacheService<FollowingsFeedViewModel> getLocalCache() {
        return LocalCacheFactory.createForMyFollowings();
    }

    @Override
    public void persistItemsToInternalStorage(FollowingsFeedViewModel item) {
        UserDTO me = userSession.getLogedInUser();
        if (me.equals(userDTO)) {
            super.persistItemsToInternalStorage(item);
        }
        //not persisting followings of other users into internal storage
    }


    @Override
    public long getLastTimeRefreshOfCache() {
        UserDTO me = userSession.getLogedInUser();
        if (me.equals(userDTO)) {
            return super.getLastTimeRefreshOfCache();
        }
        //for other users, the internal storage is not used -> there is no time when it was last refreshed
        return 0l;
    }

    @Override
    public FollowingsFeedViewModel createInstance() {
        return new FollowingsFeedViewModel();
    }

    @Override
    public void executeCreateItemTask(FollowingDTO item, List<FollowingDTO> lastLoadedItems) {
        followingCreateDeleteController.executeCreateItemTask(item, this.lastLoadedContent.getPageableContent());
    }

    @Override
    public void executeDeleteItemTask(FollowingDTO item, int position, List<FollowingDTO> lastLoadedItems) {
        followingCreateDeleteController.executeDeleteItemTask(item, position, this.lastLoadedContent.getPageableContent());
    }

    @Override
    public void onCreateOperationSuccess(FollowingDTO createdItem) {
        persistItemsToInternalStorage(lastLoadedContent);
    }

    @Override
    public void onDeleteOperationSuccess(FollowingDTO deletedItem) {
        persistItemsToInternalStorage(lastLoadedContent);
    }

    @Override
    public void displayCreateDeleteControllerError(String errorMsg) {
        if(getView() != null) {
            Fragment fragment = (Fragment) getView();
            Toast.makeText(fragment.getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected String getCustomNothingToShowMessage() {
        Fragment fragment = (Fragment) getView();

        if(!userDTO.getId().equals(userSession.getLogedInUser().getId())) {
            return super.getCustomNothingToShowMessage();
        }

        if(followingsType == FollowingsFragment.FollowingType.FOLLOWINGS) {
            return fragment.getString(R.string.nothing_to_show_in_followings);
        } else {
            return fragment.getString(R.string.nothing_to_show_in_followers);
        }
    }

    @Override
    public void updateViewModelVersionNumber(FollowingDTO updatedItem) {
        GlobalModelVersion.refreshGlobalFollowingTimestamp();
        GlobalModelVersion.refreshGlobalShoppingItemTimestamp();
        lastLoadedItemsLastRefresh = GlobalModelVersion.getGlobalShoppingItemTimestamp();//geting shopping item' timestamp here, because it's more recent then following timestamp.
    }

    @Override
    public void addItemToViewAdapter(FollowingDTO item) {
        if (getView() != null) {
            getView().addItemToViewAdapter(item);
        }
    }

    @Override
    public void addItemToViewAdapter(FollowingDTO item, int position) {
        if (getView() != null) {
            getView().addItemToViewAdapter(item, position);
        }
    }

    @Override
    public void removeItemFromViewAdapter(FollowingDTO item) {
        if (getView() != null) {
            getView().removeItemFromViewAdapter(item);
        }
    }

    @Override
    public void requestDisplayProfileOfUser(UserDTO userDTO) {
        if (getView() != null) {
            getView().displayProfileOfUserFragment(userDTO);
        }
    }
}
