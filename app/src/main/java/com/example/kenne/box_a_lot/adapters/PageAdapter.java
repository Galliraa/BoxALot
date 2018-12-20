package com.example.kenne.box_a_lot.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

//sources:
// https://github.com/codepath/android_guides/wiki/ViewPager-with-FragmentPagerAdapter

public class PageAdapter extends FragmentPagerAdapter {

    private static int NUM_ITEMS = 4;
    private Context context;
    private Map<Integer,String> fragmentTags;
    private FragmentManager fm;

    private final List<Fragment> mFragmentList = new ArrayList<>();

    private int[] tabIcons = {
            android.R.drawable.ic_menu_search,
            android.R.drawable.ic_menu_info_details,
            android.R.drawable.ic_menu_agenda,
            android.R.drawable.ic_menu_my_calendar
    };

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    public PageAdapter(FragmentManager manager, Context c) {
        super(manager);
        context = c;
    }

    public Fragment getFragment(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, int position) {
        mFragmentList.add(position, fragment);
    }

    public void removeFragment( int position) {
        mFragmentList.remove(position);
    }


    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {

            SpannableStringBuilder sb = new SpannableStringBuilder(" "); // space added before text for convenience

            Drawable drawable = context.getResources().getDrawable(tabIcons[position]);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
            sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
    }
}
