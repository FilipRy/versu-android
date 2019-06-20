package com.filip.versu.view.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.filip.versu.R;
import com.filip.versu.model.view.abs.AbsBaseViewModel;
import com.filip.versu.service.helper.Page;
import com.filip.versu.view.adapter.AbsBaseEntityRecyclerViewAdapter;
import com.filip.versu.view.custom.MSwipeRefreshLayout;
import com.filip.versu.view.viewmodel.AbsRefreshablePageableViewModel;
import com.filip.versu.view.viewmodel.callback.IRefreshablePageableViewModel;
import com.filip.versu.view.viewmodel.callback.IRefreshablePageableViewModel.IRefreshablePageableViewCallback;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;


public abstract class AbsRefreshablePageableFragment<C extends IRefreshablePageableViewCallback<K>, K extends AbsBaseViewModel, T extends AbsRefreshablePageableViewModel<C, K>>
        extends AbsPegContentFragment<C, T>
        implements MSwipeRefreshLayout.OnRefreshListener, IRefreshablePageableViewCallback<K> {

    private MSwipeRefreshLayout mSwipeRefreshLayout;

    private View contentView;

    protected RecyclerView recyclerView;
    protected LinearLayoutManager recyclerViewLayoutManager;

    private ProgressBar loadMoreProgressBar;

    private View progressBarView;
    private TextView errorMsgTextView;
    /**
     * Takes care of displaying the user's profile in a fragment.
     */
    protected ProfileDisplayer profileDisplayer = new ProfileDisplayer();

    protected AbsBaseEntityRecyclerViewAdapter recyclerViewAdapter;


    /**
     * helpers for scrolling
     */
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 3;
    int firstVisibleItem, visibleItemCount, totalItemCount;


    /**
     * Inflates the fragment view, each of subclasses views must include @layout/fragment_refreshable_recycler
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_refreshable_recycler, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contentView = view.findViewById(R.id.show_content_view);
        mSwipeRefreshLayout = (MSwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        progressBarView = view.findViewById(R.id.load_progress);
        recyclerView = (RecyclerView) view.findViewById(R.id.listView);
        loadMoreProgressBar = (ProgressBar) view.findViewById(R.id.load_more_progress);

        errorMsgTextView = (TextView) view.findViewById(R.id.errorMsgView);

        setModelView();

        mSwipeRefreshLayout.setOnRefreshListener(this);

        setupRecyclerView();

        initializeEndlessScrolling(view);



        //TODO the items should be loaded here
        //getViewModel().requestItemsFromInternalStorage(getActivity().getApplicationContext());
    }

    protected void setupRecyclerView() {
        recyclerViewAdapter = createRecyclerViewAdapter();
        SlideInBottomAnimationAdapter animationAdapter = new SlideInBottomAnimationAdapter(recyclerViewAdapter);
        AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(animationAdapter);
        recyclerView.setAdapter(alphaInAnimationAdapter);

        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(recyclerViewLayoutManager);
    }


    @Override
    public void initializeEndlessScrolling(View parentView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = recyclerViewLayoutManager.getItemCount();
                firstVisibleItem = recyclerViewLayoutManager.findFirstVisibleItemPosition();


                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached

                    getViewModel().requestNextPageFromBackend();

                    // Do something

                    loading = true;
                }


            }
        });
    }

    public abstract AbsBaseEntityRecyclerViewAdapter createRecyclerViewAdapter();

    public abstract void setModelView();

    @Override
    public void addLoadedItemsToViews(K items) {
        recyclerViewAdapter.addAllItems(items.getPageableContent());
    }

    @Override
    public void clearView() {
        recyclerViewAdapter.clear();
    }

    @Override
    public void notifyDatasetChanged() {
        recyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        getViewModel().requestItemsFromBackend(new Page<Long>(IRefreshablePageableViewModel.PAGE_SIZE, null));
    }

    @Override
    public void showBigProgressBar(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            contentView.setVisibility(show ? View.GONE : View.VISIBLE);
            contentView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            contentView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });

            progressBarView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBarView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            progressBarView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBarView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBarView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void hideErrorMessage() {
        errorMsgTextView.setVisibility(View.GONE);
    }

    @Override
    public void showErrorMessage(int stringResID) {
        showErrorMessage(getString(stringResID));
    }

    @Override
    public void showErrorMessage(String errorMsg) {
        errorMsgTextView.setVisibility(View.VISIBLE);
        errorMsgTextView.setText(errorMsg);
    }

    @Override
    public void showSmallProgressBar(boolean show) {
        mSwipeRefreshLayout.setRefreshing(show);
    }

    @Override
    public void showProgressBarAtBottom(boolean show) {
        if(show) {
            loadMoreProgressBar.setVisibility(View.VISIBLE);
        } else {
            loadMoreProgressBar.setVisibility(View.GONE);
        }
    }

}
