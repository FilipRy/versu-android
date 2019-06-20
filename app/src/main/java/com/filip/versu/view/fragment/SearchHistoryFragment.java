package com.filip.versu.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.filip.versu.R;
import com.filip.versu.model.Location;
import com.filip.versu.model.Searchable;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.model.view.SearchHistoryFeedViewModel;
import com.filip.versu.view.PostDetailActivity;
import com.filip.versu.view.adapter.AbsBaseEntityRecyclerViewAdapter;
import com.filip.versu.view.adapter.SearchHistoryRecyclerViewAdapter;
import com.filip.versu.view.viewmodel.SearchHistoryViewModel;
import com.filip.versu.view.viewmodel.callback.ISearchHistoryViewModel.ISearchHistoryViewModelCallback;

import java.util.ArrayList;

public class SearchHistoryFragment
        extends AbsRefreshablePageableFragment<ISearchHistoryViewModelCallback, SearchHistoryFeedViewModel, SearchHistoryViewModel>
        implements ISearchHistoryViewModelCallback {

    public enum SearchHistoryType {
        USERS, FEEDBACK_POSSIBILITY, PLACE
    }

    public static final String HISTORY_TYPE_KEY = "HISTORY_TYPE_KEY";
    public static final String TAG = SearchHistoryFragment.class.getSimpleName();

    private SearchHistoryType searchHistoryType;



    protected ImageView searchView;
    protected EditText searchEditText;

    public static SearchHistoryFragment newInstance(SearchHistoryType historyType) {
        SearchHistoryFragment fragment = new SearchHistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(HISTORY_TYPE_KEY, historyType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        searchHistoryType = (SearchHistoryType) getArguments().getSerializable(HISTORY_TYPE_KEY);

        super.onViewCreated(view, savedInstanceState);

        searchView = (ImageView) view.findViewById(R.id.imageViewLayout);
        searchEditText = (EditText) view.findViewById(R.id.editTextLayout);

        if(searchHistoryType == SearchHistoryType.USERS) {
            searchEditText.setHint(R.string.search_user_prompt);
        } else if (searchHistoryType == SearchHistoryType.PLACE) {
            searchEditText.setHint(R.string.search_place_prompt);
        } else if (searchHistoryType == SearchHistoryType.FEEDBACK_POSSIBILITY) {
            searchEditText.setHint(R.string.search_posts_prompt);
        }

        searchView.setImageResource(R.mipmap.ic_search_black);


        getViewModel().requestItemsFromInternalStorage(getActivity().getApplicationContext());

        searchEditText.post(new Runnable() {
            @Override
            public void run() {
                searchEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String text = s.toString();
                        if (text.length() > 0) {
                            searchView.setImageResource(R.mipmap.ic_cancel_black);
                        } else {
                            cancelSearch();
                        }
                        if (text.length() >= 3) {
                            getViewModel().searchEntryByName(text, getActivity().getApplicationContext());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
                cancelSearch();
            }
        });

    }


    private final void cancelSearch() {
        searchView.setImageResource(R.mipmap.ic_search_black);
        getViewModel().displaySearchHistory(getActivity().getApplicationContext());
    }

    @Override
    public void loadPostsByFeedbackPossibilities(String[] feedbackPossibilities) {
        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
        intent.putExtra(PostDetailActivity.POST_FEED_TYPE_KEY, PostDetailActivity.PostFeedType.HASHTAG);
        intent.putExtra(PostsTimelineFeedFragment.FEEDBACK_POSSIBLITIES_KEY, feedbackPossibilities);

        startActivity(intent);
    }

    @Override
    public void addItemToViewAdapter(Searchable item) {
        recyclerViewAdapter.addItem(item);
    }

    @Override
    public void addItemToViewAdapter(Searchable item, int position) {
        recyclerViewAdapter.addItemToPosition(item, position);
    }

    @Override
    public void removeItemFromViewAdapter(Searchable item) {
        recyclerViewAdapter.removeItem(item);
    }

    @Override
    public AbsBaseEntityRecyclerViewAdapter<Searchable> createRecyclerViewAdapter() {
        return new SearchHistoryRecyclerViewAdapter(new ArrayList<Searchable>(), getActivity().getApplicationContext(), this.getViewModel());
    }

    @Override
    public void setModelView() {
        setModelView(this);
        getViewModel().setDependencies(searchHistoryType);
    }

    @Override
    public void displayProfileOfUserFragment(UserDTO userDTO) {
        profileDisplayer.displayUserProfile(userDTO, getActivity());
    }

    @Override
    public void displayLocationProfile(Location location) {
        profileDisplayer.displayLocationProfile(location, getActivity());
    }

    @Nullable
    @Override
    public Class<SearchHistoryViewModel> getViewModelClass() {
        return SearchHistoryViewModel.class;
    }

}
