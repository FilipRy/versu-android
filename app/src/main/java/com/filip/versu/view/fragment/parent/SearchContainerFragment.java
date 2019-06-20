package com.filip.versu.view.fragment.parent;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.filip.versu.R;
import com.filip.versu.view.fragment.AbsPegContentFragment;
import com.filip.versu.view.fragment.SearchHistoryFragment;
import com.filip.versu.view.viewmodel.callback.parent.ISearchContainerViewModel.ISearchContainerViewCallback;
import com.filip.versu.view.viewmodel.parent.SearchContainerViewModel;

import java.util.ArrayList;
import java.util.List;


public class SearchContainerFragment extends AbsTabsContainerFragment<ISearchContainerViewCallback, SearchContainerViewModel> implements ISearchContainerViewCallback {


    @Nullable
    @Override
    public Class<SearchContainerViewModel> getViewModelClass() {
        return SearchContainerViewModel.class;
    }

    @Override
    public void setModelView() {
        setModelView(this);
    }

    @Override
    protected List<AbsPegContentFragment> createFragmentsContentList() {
        List<AbsPegContentFragment> searchFragments = new ArrayList<>();

        SearchHistoryFragment searchHistoryUsers = SearchHistoryFragment.newInstance(SearchHistoryFragment.SearchHistoryType.USERS);
        searchHistoryUsers.setImageResource(R.mipmap.ic_search_white_24dp);
        searchHistoryUsers.setTitle(this.getString(R.string.people));

        SearchHistoryFragment searchHistoryPosts = SearchHistoryFragment.newInstance(SearchHistoryFragment.SearchHistoryType.FEEDBACK_POSSIBILITY);
        searchHistoryPosts.setImageResource(R.mipmap.ic_search_white_24dp);
        searchHistoryPosts.setTitle(getString(R.string.posts));

        SearchHistoryFragment searchHistoryPlaces = SearchHistoryFragment.newInstance(SearchHistoryFragment.SearchHistoryType.PLACE);
        searchHistoryPlaces.setImageResource(R.mipmap.ic_search_white_24dp);
        searchHistoryPlaces.setTitle(getString(R.string.places));

        searchFragments.add(searchHistoryUsers);
        searchFragments.add(searchHistoryPosts);
        searchFragments.add(searchHistoryPlaces);

        return searchFragments;
    }

    @Override
    protected void initTabColors() {
        int color = ContextCompat.getColor(getActivity(), R.color.tab_search_underline);
        int normalColor = ContextCompat.getColor(getActivity(), R.color.tab_unselected_text);
        tabLayout.setTabTextColors(normalColor, color);
        tabLayout.setSelectedTabIndicatorColor(color);
    }
}
