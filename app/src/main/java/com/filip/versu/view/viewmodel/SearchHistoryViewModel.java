package com.filip.versu.view.viewmodel;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.filip.versu.R;
import com.filip.versu.controller.impl.SearchHistoryCreateDeleteController;
import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.HashtagSearch;
import com.filip.versu.model.Location;
import com.filip.versu.model.Searchable;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.model.view.SearchHistoryFeedViewModel;
import com.filip.versu.service.IGooglePlaceService;
import com.filip.versu.service.IPostService;
import com.filip.versu.service.IUserService;
import com.filip.versu.service.IUserSession;
import com.filip.versu.service.cache.ILocalCacheService;
import com.filip.versu.service.factory.LocalCacheFactory;
import com.filip.versu.service.helper.Page;
import com.filip.versu.service.impl.GooglePlaceService;
import com.filip.versu.service.impl.PostService;
import com.filip.versu.service.impl.UserService;
import com.filip.versu.service.impl.UserSession;
import com.filip.versu.view.MainViewActivity;
import com.filip.versu.view.fragment.SearchHistoryFragment;
import com.filip.versu.view.viewmodel.callback.IRefreshablePageableViewModel;
import com.filip.versu.view.viewmodel.callback.ISearchHistoryViewModel;
import com.filip.versu.view.viewmodel.callback.ISearchHistoryViewModel.ISearchHistoryViewModelCallback;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryViewModel
        extends AbsRefreshablePageableViewModel<ISearchHistoryViewModelCallback, SearchHistoryFeedViewModel>
        implements ISearchHistoryViewModel {

    private SearchHistoryFragment.SearchHistoryType searchHistoryType;

    private String usernameSearchQuery;

    protected IUserService userService = UserService.instance();
    protected IUserSession userSession = UserSession.instance();
    private IPostService postService = PostService.instance();
    private IGooglePlaceService googlePlaceService;

    private SearchHistoryFeedViewModel searchHistoryFeedCurrentItem = new SearchHistoryFeedViewModel();

    private SearchHistoryCreateDeleteController searchHistoryCreateDeleteController;

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);
        searchHistoryCreateDeleteController = new SearchHistoryCreateDeleteController(this);
    }

    @Override
    public void executeCreateItemTask(Searchable item, List<Searchable> lastLoadedItems) {
        if (this.lastLoadedContent.getPageableContent().contains(item)) {//TODO @param item has no ID yet, but lastLoadedContent have IDs.
            return;//avoid duplicates
        }
        searchHistoryCreateDeleteController.executeCreateItemTask(item, this.lastLoadedContent.getPageableContent());
    }

    @Override
    public void executeDeleteItemTask(Searchable item, int position, List<Searchable> lastLoadedItems) {
        searchHistoryCreateDeleteController.executeDeleteItemTask(item, position, this.lastLoadedContent.getPageableContent());
    }

    @Override
    public void onCreateOperationSuccess(Searchable createdItem) {
        searchHistoryFeedCurrentItem.getPageableContent().add(0, createdItem);//TODO revert operation in case of failure
        persistItemsToInternalStorage(searchHistoryFeedCurrentItem);
    }

    @Override
    public void onDeleteOperationSuccess(Searchable deletedItem) {
        persistItemsToInternalStorage(searchHistoryFeedCurrentItem);
    }

    @Override
    public void updateViewModelVersionNumber(Searchable updatedItem) {

    }

    @Override
    public void addItemToViewAdapter(Searchable item) {
        if (getView() != null) {
            getView().addItemToViewAdapter(item);
        }
    }

    @Override
    public void addItemToViewAdapter(Searchable item, int position) {
        if (getView() != null) {
            getView().addItemToViewAdapter(item, position);
        }
    }

    @Override
    public void removeItemFromViewAdapter(Searchable item) {
        if (getView() != null) {
            getView().removeItemFromViewAdapter(item);
        }
    }

    @Override
    public void setDependencies(SearchHistoryFragment.SearchHistoryType historyType) {
        this.searchHistoryType = historyType;
        Fragment fragment = (Fragment) getView();
        if(historyType == SearchHistoryFragment.SearchHistoryType.PLACE) {
            googlePlaceService = GooglePlaceService.createInstanceForActivity(fragment.getActivity(), MainViewActivity.TAG);
        }
    }

    @Override
    public void searchEntryByName(String name, Context context) {
        if(name.startsWith("#")) {
            if(name.length() < 4) {
                return;
            }
        }
        usernameSearchQuery = name;
        requestItemsFromBackend(new Page<Long>(IRefreshablePageableViewModel.PAGE_SIZE, null));
    }

    @Override
    public void requestPostsByFeedbackPossibilities(String[] feedbackPossiblities) {
        if(getView() != null) {
            getView().loadPostsByFeedbackPossibilities(feedbackPossiblities);
        }
    }

    @Override
    public void displaySearchHistory(Context context) {
        lastLoadedContent = searchHistoryFeedCurrentItem;
        lastErrorMessage = null;
        if (searchHistoryFeedCurrentItem.getSize() == 0) {
            lastErrorMessage = context.getResources().getString(R.string.nothing_to_show);
        }

        if (getView() != null) {
            getView().clearView();
            if (lastErrorMessage == null) {
                getView().hideErrorMessage();
            } else {
                getView().showErrorMessage(lastErrorMessage);
            }
            getView().addLoadedItemsToViews(lastLoadedContent);
        }
    }

    @Override
    public void displayCreateDeleteControllerError(String errorMsg) {
        if (getView() != null) {
            Fragment fragment = (Fragment) getView();
            Toast.makeText(fragment.getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected AbsBackendLoaderTask createBackendLoaderTask(Page<Long> page) {
        if (usernameSearchQuery != null) {
            return new SearchItemTask(page, usernameSearchQuery);
        } else {
            return new SearchHistoryBackendLoaderTask(page);
        }
    }

    @Override
    public SearchHistoryFeedViewModel createInstance() {
        return new SearchHistoryFeedViewModel();
    }

    @Override
    protected AbsInternalStorageLoaderTask createInternalStorageLoaderTask(Context applicationContext) {
        return new SearchHistoryStorageLoaderTask(applicationContext);
    }

    @Override
    public SearchHistoryFeedViewModel retrieveItemsFromBackend(Page<Long> page) throws ServiceException {
        //TODO create endpoint for retrieving the search history
        return retrieveItemsFromInternalStorage();
    }

    @Override
    public void requestDisplayProfileOfUser(UserDTO userDTO) {
        if (getView() != null) {
            getView().displayProfileOfUserFragment(userDTO);
        }
        Searchable searchable = userDTO;
        executeCreateItemTask(searchable, searchHistoryFeedCurrentItem.getPageableContent());
    }

    @Override
    public String getInternalStorageKey() {
        return searchHistoryType.toString().toLowerCase();
    }

    @Override
    public String getLastTimeRefreshInternalStorageKey() {
        return SearchHistoryViewModel.class.getSimpleName().toLowerCase();
    }

    @Override
    public ILocalCacheService<SearchHistoryFeedViewModel> getLocalCache() {
        return LocalCacheFactory.createForSearchHistory();
    }

    @Override
    public void requestDisplayLocationProfile(Location location) {
        if(getView() != null) {
            getView().displayLocationProfile(location);
        }
    }


    private class SearchHistoryBackendLoaderTask extends BackendLoaderTask {

        SearchHistoryBackendLoaderTask(Page<Long> page) {
            super(page);
        }

        @Override
        void onItemsSuccessLoaded(SearchHistoryFeedViewModel searchHistoryFeedViewModel) {
            onItemsSuccessLoadedFromBackend(searchHistoryFeedViewModel, getPage());
            searchHistoryFeedCurrentItem = searchHistoryFeedViewModel;
        }
    }

    private class SearchHistoryStorageLoaderTask extends InternalStorageLoaderTask {

        SearchHistoryStorageLoaderTask(Context context) {
            super(context);
        }

        @Override
        protected void onItemsSuccessLoaded(SearchHistoryFeedViewModel searchHistoryFeedViewModel) {
            super.onItemsSuccessLoaded(searchHistoryFeedViewModel);
            searchHistoryFeedCurrentItem = searchHistoryFeedViewModel;
        }
    }

    private class SearchItemTask extends AbsBackendLoaderTask {

        private String searchQuery;

        SearchItemTask(Page<Long> page, String searchQuery) {
            super(page);
            this.searchQuery = searchQuery;
        }

        @Override
        SearchHistoryFeedViewModel retrieveItemsFromBackendTask(Page<Long> page) throws ServiceException {

            List<Searchable> searchables = new ArrayList<>();

            if (page.getLastLoadedID() != null) {//we are requesting to load next page
                SearchHistoryFeedViewModel searchHistoryFeedViewModel = createInstance();
                searchHistoryFeedViewModel.feedItems = searchables;
                return searchHistoryFeedViewModel;
            }


            //searching hashtags
            if (searchHistoryType == SearchHistoryFragment.SearchHistoryType.FEEDBACK_POSSIBILITY) {
                List<String[]> stringLists = postService.findVSPossibilities(searchQuery);
                if(stringLists != null) {
                    for(String[] hashtag: stringLists) {
                        HashtagSearch hashtagSearch = new HashtagSearch();
                        hashtagSearch.possibilityA = hashtag[0];
                        hashtagSearch.possibilityB = hashtag[1];
                        searchables.add(hashtagSearch);
                    }
                }
            }
            //searching users
            else if(searchHistoryType == SearchHistoryFragment.SearchHistoryType.USERS) {
                List<UserDTO> userDTOs = userService.findByName(searchQuery, page);
                if (userDTOs != null) {
                    for (UserDTO userDTO : userDTOs) {
                        Searchable searchable = userDTO;
                        searchables.add(searchable);
                    }
                }
            }
            //searching places
            else if (searchHistoryType == SearchHistoryFragment.SearchHistoryType.PLACE) {
                List<Location> locations = googlePlaceService.findPlacesByConstraint(searchQuery, null, null);
                if(locations != null) {
                    for(Location location: locations) {
                        Searchable searchable = location;
                        searchables.add(searchable);
                    }
                }
            } else {
                throw new IllegalArgumentException();
            }

            SearchHistoryFeedViewModel searchHistoryFeedViewModel = createInstance();
            searchHistoryFeedViewModel.feedItems = searchables;
            return searchHistoryFeedViewModel;
        }

        @Override
        void onItemsSuccessLoaded(SearchHistoryFeedViewModel loadedItem) {
            if (getView() != null) {
                getView().addLoadedItemsToViews(loadedItem);
            }
            //not persisting the search results to internal storage
        }
    }

}
