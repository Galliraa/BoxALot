package com.example.kenne.box_a_lot.customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

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
