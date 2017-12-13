package com.willy.ratingbar;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class AnimationRatingBar extends BaseRatingBar {

    protected Handler mHandler;
    protected boolean mStopFillingFlag = false;
    protected int mDelay = 0;

    protected AnimationRatingBar(Context context) {
        super(context);
        init();
    }

    protected AnimationRatingBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    protected AnimationRatingBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mHandler = new Handler();
    }
}

