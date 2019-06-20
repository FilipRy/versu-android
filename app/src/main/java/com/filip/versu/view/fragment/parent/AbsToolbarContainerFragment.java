package com.filip.versu.view.fragment.parent;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.filip.versu.R;
import com.filip.versu.view.fragment.AbsPegContentFragment;
import com.filip.versu.view.fragment.parent.AbsTabsContainerFragment.PegViewPagerAdapter;
import com.filip.versu.view.viewmodel.callback.parent.IToolbarContainerViewModel.IToolbarContainerViewCallback;

import java.util.ArrayList;
import java.util.List;

import eu.inloop.viewmodel.AbstractViewModel;
import eu.inloop.viewmodel.base.ViewModelBaseFragment;

public abstract class AbsToolbarContainerFragment<C extends IToolbarContainerViewCallback, K extends AbstractViewModel<C>> extends ViewModelBaseFragment<C, K> implements IToolbarContainerViewCallback {

    private ViewPager viewPager;
    private PegViewPagerAdapter feedPagerAdapter;

    protected View headerToolbar;
    protected ImageView headerToolbarIcon;
    protected TextView headerToolbarText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_toolbar_view_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        headerToolbar = view.findViewById(R.id.toolbar);
        headerToolbarIcon = (ImageView) view.findViewById(R.id.toolbarIcon);
        headerToolbarText = (TextView) view.findViewById(R.id.toolbarText);

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        setModelView();

        List<AbsPegContentFragment> contentFragments = new ArrayList<>();
        contentFragments.add(createFragmentContent());

        feedPagerAdapter = new PegViewPagerAdapter(getChildFragmentManager(), contentFragments);
        viewPager.setAdapter(feedPagerAdapter);


    }

    public abstract void setModelView();

    protected abstract AbsPegContentFragment createFragmentContent();

}
