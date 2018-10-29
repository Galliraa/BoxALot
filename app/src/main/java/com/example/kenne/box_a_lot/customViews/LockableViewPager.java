package com.example.kenne.box_a_lot.customViews;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class LockableViewPager extends ViewPager {

    public LockableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }
}
