package com.filip.versu.view.fragment.parent;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.filip.versu.R;
import com.filip.versu.view.fragment.AbsPegContentFragment;
import com.filip.versu.view.viewmodel.callback.parent.ITabsContainerViewModel.ITabsContainerViewCallback;

import java.util.ArrayList;
import java.util.List;

import eu.inloop.viewmodel.AbstractViewModel;
import eu.inloop.viewmodel.base.ViewModelBaseFragment;


public abstract class AbsTabsContainerFragment<C extends ITabsContainerViewCallback, K extends AbstractViewModel<C>> extends ViewModelBaseFragment<C, K> implements ITabsContainerViewCallback {

    protected ViewPager viewPager;
    private PegViewPagerAdapter feedPagerAdapter;

    protected TabLayout tabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_tab_view_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);

        initTabColors();

        int pixelSizeMargin = getActivity().getApplicationContext().getResources().getDimensionPixelSize(R.dimen.main_view_side_margin);
        viewPager.setPageMargin(pixelSizeMargin);
        viewPager.setPageMarginDrawable(R.color.app_bg);

        setModelView();

        List<AbsPegContentFragment> contentFragments = createFragmentsContentList();

        buildTabsFragmentContent(contentFragments);

    }

    protected void initTabColors() {
        int color = ContextCompat.getColor(getActivity(), R.color.non_clickable_content);
        int normalColor = ContextCompat.getColor(getActivity(), R.color.tab_unselected_text);
        tabLayout.setTabTextColors(normalColor, color);
        tabLayout.setSelectedTabIndicatorColor(color);
    }

    protected void buildTabsFragmentContent(List<AbsPegContentFragment> contentFragments) {
        feedPagerAdapter = new PegViewPagerAdapter(getChildFragmentManager(), contentFragments);
        viewPager.setAdapter(feedPagerAdapter);
        viewPager.setCurrentItem(getViewPagerCurrentItem());

        tabLayout.setupWithViewPager(viewPager);

        //setting tab title and icon
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            AbsPegContentFragment contentFragment = contentFragments.get(i);

//            int iconResource = contentFragment.getImageResource();
            tabLayout.getTabAt(i).setText(contentFragment.getTitle());
//            if (iconResource != 0) {
//                tabLayout.getTabAt(i).setIcon(iconResource);
//            }
//            tabLayout.getTabAt(i).setCustomView(R.layout.tab_top_main_view);
        }

        tabLayout.getTabAt(getViewPagerCurrentItem()).select();

        //circleSlidingTabLayout.setViewPager(viewPager);
    }

    public abstract void setModelView();

    abstract protected List<AbsPegContentFragment> createFragmentsContentList();

    protected int getViewPagerCurrentItem() {
        return 0;
    }


    public static class PegViewPagerAdapter extends FragmentPagerAdapter {

        private List<? extends AbsPegContentFragment> absPegViewFragments = new ArrayList<>();

        PegViewPagerAdapter(FragmentManager fm, List<? extends AbsPegContentFragment> absShoppingFeedFragments) {
            super(fm);
            this.absPegViewFragments = absShoppingFeedFragments;
        }

        @Override
        public AbsPegContentFragment getItem(int position) {
            return absPegViewFragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getItem(position).getTitle();
        }

        public int getFragmentIcon(int position) {
            return getItem(position).getImageResource();
        }

        public int getFragmentBg(int position) {
            return getItem(position).getImageBackground();
        }

        public String getFragmentIconURL(int position) {
            return getItem(position).getImageURL();
        }

        @Override
        public int getCount() {
            return absPegViewFragments.size();
        }
    }

}
