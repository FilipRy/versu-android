package com.filip.versu.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.filip.versu.R;


public class PostVSDivider extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private Context context;

    public PostVSDivider(Context context) {
        mDivider = ContextCompat.getDrawable(context, R.drawable.post_vs_divider);
        this.context = context;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);

            mDivider.draw(c);

            if(i == 0 && childCount == 2) {//draw the "VS" divider only in first child
                int radiusSize = context.getResources().getDimensionPixelSize(R.dimen.post_vs_radius_size);

                float cx = (left + right) / 2;
                float cy = (top + bottom) / 2;

                Paint paint = new Paint();
                paint.setColor(Color.WHITE);

                c.drawCircle(cx, cy, radiusSize, paint);

                float textSize = context.getResources().getDimensionPixelSize(R.dimen.post_vs_text_size);

                Paint grayPaint = new Paint();
                grayPaint.setColor(Color.GRAY);
                grayPaint.setTextSize(textSize);

                c.drawText("VS", cx - radiusSize + 10, cy + radiusSize/2, grayPaint);

            }
        }
    }
}