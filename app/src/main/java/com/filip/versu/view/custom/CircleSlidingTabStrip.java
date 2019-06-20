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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.filip.versu.R;


class CircleSlidingTabStrip extends LinearLayout {

    private Paint mSelectedIndicatorPaint;
    private Paint mPaintWithoutStroke;

    private final int mSelectedCirclePhotoThickness;
    private final int mCircleMargin;
    private final int mCircleMarginTop;
    private final int mCirclePhotoDiameter;
    private final int mCirclePhotoRadius;
    private final int mCircleBottomOffset;


    private int mSelectedPosition;
    private float mSelectionOffset;

    CircleSlidingTabStrip(Context context) {
        this(context, null);
    }

    CircleSlidingTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        mSelectedCirclePhotoThickness = context.getResources().getDimensionPixelSize(R.dimen.header_photo_circle_selected_border);
        mCircleMargin = getContext().getResources().getDimensionPixelOffset(R.dimen.header_photo_circle_margin);
        mCircleMarginTop = getContext().getResources().getDimensionPixelOffset(R.dimen.header_photo_circle_margin_top);
        mCirclePhotoDiameter = getContext().getResources().getDimensionPixelSize(R.dimen.header_photo_circle_diameter) + 2 * mSelectedCirclePhotoThickness;
        mCircleBottomOffset = getContext().getResources().getDimensionPixelSize(R.dimen.header_photo_circle_rounded_corners_side_offset);
        mCirclePhotoRadius = mCirclePhotoDiameter / 2;

        mSelectedIndicatorPaint = new Paint();
        int color = Color.parseColor("#f2f2f2");
        mSelectedIndicatorPaint.setColor(color);
        mSelectedIndicatorPaint.setAntiAlias(true);
        mSelectedIndicatorPaint.setStrokeWidth(mSelectedCirclePhotoThickness);

        mPaintWithoutStroke = new Paint();
        mPaintWithoutStroke.setColor(color);
    }

    void setSelectedIndicatorColorID(int selectedIndicatorColorID) {
        mSelectedIndicatorPaint = new Paint();
        int color = ContextCompat.getColor(getContext(), selectedIndicatorColorID);
        mSelectedIndicatorPaint.setColor(color);
        mSelectedIndicatorPaint.setAntiAlias(true);
        mSelectedIndicatorPaint.setStrokeWidth(mSelectedCirclePhotoThickness);

        mPaintWithoutStroke = new Paint();
        mPaintWithoutStroke.setColor(color);
    }

    void onViewPagerPageChanged(int position, float positionOffset) {
        mSelectedPosition = position + 1;
        mSelectionOffset = positionOffset;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int height = getHeight();
        final int childCount = getChildCount();

        // Thick colored underline below the current selection
        if (childCount > 0) {
            View selectedTitle = getChildAt(mSelectedPosition);
            int left = selectedTitle.getLeft();
            int right = selectedTitle.getRight();

            if (mSelectionOffset > 0f && mSelectedPosition < (getChildCount() - 1)) {

                // Draw the selection partway between the tabs
                View nextTitle = getChildAt(mSelectedPosition + 1);
                left = (int) (mSelectionOffset * nextTitle.getLeft() +
                        (1.0f - mSelectionOffset) * left);
                right = (int) (mSelectionOffset * nextTitle.getRight() +
                        (1.0f - mSelectionOffset) * right);
            }

            int middleX = (left + right)/2;
            int middleY = (getHeight() - mCircleMargin - mCircleMarginTop) / 2 + mCircleMarginTop;

            int sideOffset = mCircleMargin - mSelectedCirclePhotoThickness;

            canvas.drawRect(left + sideOffset, middleY, right - sideOffset, height, mPaintWithoutStroke);
            canvas.drawCircle(middleX, middleY, mCirclePhotoRadius, mSelectedIndicatorPaint);

            int leftCirclePhotoCorner = middleX - mCirclePhotoRadius;
            int rightCirclePhotoCorner = middleX + mCirclePhotoRadius;

            drawSelectedCirclePhotoCorners(canvas, leftCirclePhotoCorner - mCirclePhotoRadius, middleY + mCircleBottomOffset, mCirclePhotoRadius, false);
            drawSelectedCirclePhotoCorners(canvas, rightCirclePhotoCorner + mCirclePhotoRadius, middleY + mCircleBottomOffset, mCirclePhotoRadius, true);

        }
    }


    private void drawSelectedCirclePhotoCorners(Canvas canvas, int center_x, int center_y, int radius, boolean isLeft) {

        RectF rect = createRectForLeftSide(center_x, center_y, radius);
        if(isLeft) {
            rect = createRectForRightSide(center_x, center_y, radius);
        }

        Path ovalPath = new Path();
        RectF oval = new RectF();

        oval.set(center_x - radius, center_y - radius, center_x + radius, center_y + radius);

        ovalPath.addOval(oval, Path.Direction.CW);
        canvas.clipPath(ovalPath, Region.Op.DIFFERENCE);

        mSelectedIndicatorPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rect, mSelectedIndicatorPaint);

        canvas.clipPath(ovalPath, Region.Op.UNION);

    }

    /**
     * Creates the helper rectangle, which is used for "difference" with arc in order to create the circle corners on the left side.
     * @param center_x
     * @param center_y
     * @param radius
     * @return
     */
    public RectF createRectForLeftSide(float center_x, float center_y, float radius) {
        RectF rectF = new RectF();

        int left = (int) (center_x);
        int right = (int) (center_x + radius);
        int bottom = (int) (center_y + radius);
        int top = (int) center_y;

        rectF.set(left, top, right, bottom);
        return rectF;
    }

    /**
     * Creates the helper rectangle, which is used for "difference" with arc in order to create the circle corners on the right side.
     * @param center_x
     * @param center_y
     * @param radius
     * @return
     */
    public RectF createRectForRightSide(float center_x, float center_y, float radius) {
        RectF rectF = new RectF();

        int left = (int) (center_x - radius);
        int right = (int) (center_x);
        int bottom = (int) (center_y + radius);
        int top = (int) center_y;

        rectF.set(left, top, right, bottom);
        return rectF;
    }


}