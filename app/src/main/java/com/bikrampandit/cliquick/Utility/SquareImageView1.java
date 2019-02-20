package com.bikrampandit.cliquick.Utility;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Rakesh Pandit on 8/29/2017.
 */

public class SquareImageView1 extends ImageView {
    public SquareImageView1(Context context) {
        super(context);
    }


    public SquareImageView1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView1(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(),getMeasuredHeight()); //Snap to width
    }
}
