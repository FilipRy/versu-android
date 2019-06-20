package com.filip.versu.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;



public class MImageView extends ImageView {

    private DoubleClickListener doubleClickListener;

    public MImageView(Context context) {
        super(context);
    }

    public MImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);

    }

    public void setOnDoubleClickListener(DoubleClickListener dl) {
        this.doubleClickListener = dl;
    }

    public interface DoubleClickListener {
        void onDoubleClick();
    }


}
