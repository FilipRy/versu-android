/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the SPECIFIC language governing permissions and
 * limitations under the License.
 */

package com.filip.versu.view.custom;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import com.filip.versu.R;
import com.filip.versu.view.fragment.AbsPegContentFragment;
import com.filip.versu.view.fragment.parent.AbsTabsContainerFragment;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * To be used with ViewPager to provide a tab indicator component which give constant feedback as to
 * the user's scroll progress.
 * <p>
 * To use the component, simply add it to your view hierarchy. Then in your
 * {@link android.app.Activity} or {@link android.support.v4.app.Fragment} call
 * {@link #setViewPager(ViewPager)} providing it the ViewPager this layout is being used for.

 */
public class CircleSlidingTabLayout extends HorizontalScrollView {

    private LayoutInflater layoutInflater;

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mViewPagerPageChangeListener;

    private final CircleSlidingTabStrip mTabStrip;

    public CircleSlidingTabLayout(Context context) {
        this(context, null);

        init(context);
    }

    public CircleSlidingTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

        init(context);
    }

    public CircleSlidingTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);

        // Disable the Scroll Bar
        setHorizontalScrollBarEnabled(false);
        // Make sure that the Tab Strips fills this View
        setFillViewport(true);

        mTabStrip = new CircleSlidingTabStrip(context);
        addView(mTabStrip, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    private void init(Context context) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void setTabStripColorID(int tabStripColorID) {
        mTabStrip.setSelectedIndicatorColorID(tabStripColorID);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mViewPager != null) {
            scrollToTab(mViewPager.getCurrentItem(), 0);
        }
    }

    /**
     * Set the {@link ViewPager.OnPageChangeListener}. When using {@link CircleSlidingTabLayout} you are
     * required to set any {@link ViewPager.OnPageChangeListener} through this method. This is so
     * that the layout can update it's scroll position correctly.
     *
     * @see ViewPager#setOnPageChangeListener(ViewPager.OnPageChangeListener)
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mViewPagerPageChangeListener = listener;
    }

    /**
     * Sets the associated view pager. Note that the assumption here is that the pager content
     * (number of tabs and tab titles) does not change after this call has been made.
     */
    public void setViewPager(ViewPager viewPager) {
        mTabStrip.removeAllViews();

        mViewPager = viewPager;
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
    }

    private void populateTabStrip() {
        final PagerAdapter adapter = mViewPager.getAdapter();
        final OnClickListener tabClickListener = new TabClickListener();


        mTabStrip.addView(createFirstTabView(getContext()));

        for (int i = 0; i < adapter.getCount(); i++) {
            View tabView = createDefaultTabView(i);
            tabView.setOnClickListener(tabClickListener);
            mTabStrip.addView(tabView);
        }

        mTabStrip.addView(createLastTabView(getContext()));
    }

    /**
     * Create a default view to be used for tabs.
     */
    protected View createDefaultTabView(int position) {
        View view = layoutInflater.inflate(R.layout.tab_circle, null);
        CircleImageView circleImageView = (CircleImageView) view.findViewById(R.id.profile_image);

        AbsPegContentFragment absPegContentFragment = null;

        if(mViewPager.getAdapter() instanceof AbsTabsContainerFragment.PegViewPagerAdapter) {
            AbsTabsContainerFragment.PegViewPagerAdapter fragmentPagerAdapter = (AbsTabsContainerFragment.PegViewPagerAdapter) mViewPager.getAdapter();
            absPegContentFragment = fragmentPagerAdapter.getItem(position);
        }

        if(absPegContentFragment != null) {
            circleImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if(absPegContentFragment.getImageBackground() != 0) {
                circleImageView.setBackgroundResource(absPegContentFragment.getImageBackground());
            }
            if(absPegContentFragment.getImageResource() != 0) {
                circleImageView.setImageResource(absPegContentFragment.getImageResource());
            }
            if(absPegContentFragment.getImageURL() != null) {
                Picasso.with(getContext()).load(absPegContentFragment.getImageURL()).into(circleImageView);

            }
        }

        return view;
    }

    /**
     * Create the first "fake" tab (non-clickable), which is used for fill the space.
     * @param context
     * @return
     */
    private View createFirstTabView(Context context) {

        int photoWidth = computeCircleHeaderWithMarginDiameter();
        int layoutSideMargin = context.getResources().getDimensionPixelSize(R.dimen.main_view_side_margin);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int layoutWidth = screenWidth - 2 * layoutSideMargin;
        int tabWidth = (layoutWidth - photoWidth) / 2;

        View tabView = new View(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(tabWidth, 0);
        tabView.setLayoutParams(layoutParams);

        return tabView;
    }

    private View createLastTabView(Context context) {
        return createFirstTabView(context);
    }


    /**
     *
     * @return the diameter [px] of the circle with photo (together with the margins) at the top of the screen.
     */
    private int computeCircleHeaderWithMarginDiameter() {
        int photoPixelSize = getContext().getResources().getDimensionPixelSize(R.dimen.header_photo_circle_diameter);
        int photoPixelMarginSize = getContext().getResources().getDimensionPixelSize(R.dimen.header_photo_circle_margin);

        int photoWidth = photoPixelSize + 2*photoPixelMarginSize;
        return photoWidth;
    }

    private void scrollToTab(int tabIndex, int positionOffset) {
        if(tabIndex != 0) {
            tabIndex++;
        }

        final int tabStripChildCount = mTabStrip.getChildCount();
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }

        View selectedChild = mTabStrip.getChildAt(tabIndex);
        if (selectedChild != null) {
            int targetScrollX = 0;
            targetScrollX = selectedChild.getLeft() + positionOffset;
            if (tabIndex > 0 || positionOffset > 0) {
                // If we're not at the first child and are mid-scroll, make sure we obey the offset
                targetScrollX -= (getWidth()-selectedChild.getWidth())/2;
            }
            scrollTo(targetScrollX, 0);
        }
    }


    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int tabStripChildCount = mTabStrip.getChildCount();
            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
                return;
            }

            mTabStrip.onViewPagerPageChanged(position, positionOffset);

            View selectedTitle = mTabStrip.getChildAt(position);
            int extraOffset = (selectedTitle != null)
                    ? (int) (positionOffset * selectedTitle.getWidth())
                    : 0;
            scrollToTab(position, extraOffset);

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrolled(position, positionOffset,
                        positionOffsetPixels);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                mTabStrip.onViewPagerPageChanged(position, 0f);
                scrollToTab(position, 0);
            }

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageSelected(position);
            }
        }

    }

    private class TabClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            /**
             * We don't need to iterate over 1st and last tab, because they are the "faked" tabs.
             */
            for (int i = 1; i < mTabStrip.getChildCount() - 1; i++) {
                if (v == mTabStrip.getChildAt(i)) {
                    mViewPager.setCurrentItem(i-1);// i - 1, because we ignore the first "fake" tab.
                    return;
                }
            }
        }
    }

}
