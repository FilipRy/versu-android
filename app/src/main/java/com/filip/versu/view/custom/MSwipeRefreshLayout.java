package com.filip.versu.view.custom;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

/**
 * This class fixes the bug, that SwipeRefrehLayout progress bar is not showing
 * right after initialization.
 * See https://code.google.com/p/android/issues/detail?id=77712
 * @author Filip
 * 
 */
public class MSwipeRefreshLayout extends SwipeRefreshLayout {

	private boolean mMeasured = false;
	private boolean mPreMeasureRefreshing = false;

	public MSwipeRefreshLayout(Context context) {
		super(context);
	}
	
	public MSwipeRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (!mMeasured) {
			mMeasured = true;
			setRefreshing(mPreMeasureRefreshing);
		}
	}

	@Override
	public void setRefreshing(boolean refreshing) {
		if (mMeasured) {
			super.setRefreshing(refreshing);
		} else {
			mPreMeasureRefreshing = refreshing;
		}
	}

}
