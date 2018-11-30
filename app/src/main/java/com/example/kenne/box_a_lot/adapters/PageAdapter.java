package com.example.kenne.box_a_lot.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.ViewGroup;

import com.example.kenne.box_a_lot.R;
import com.example.kenne.box_a_lot.fragments.CreateStoragePage1Fragment;
import com.example.kenne.box_a_lot.fragments.CreateStoragePage2Fragment;
import com.example.kenne.box_a_lot.fragments.MapsFragment;
import com.example.kenne.box_a_lot.fragments.MapsRootFragment;
import com.example.kenne.box_a_lot.fragments.UserFragment;

import java.util.HashMap;
import java.util.Map;

//sources:
// https://github.com/codepath/android_guides/wiki/ViewPager-with-FragmentPagerAdapter

public class PageAdapter extends FragmentPagerAdapter {

    private static int NUM_ITEMS = 4;
    private Context context;
    private Map<Integer,String> fragmentTags;
    private FragmentManager fm;

    private int[] tabIcons = {
            android.R.drawable.ic_menu_search,
            android.R.drawable.ic_menu_info_details,
            android.R.drawable.ic_menu_agenda,
            android.R.drawable.ic_menu_my_calendar
    };

    public PageAdapter(FragmentManager fragmentManager, Context c) {
        super(fragmentManager);
        context = c;
        fm = fragmentManager;
        fragmentTags = new HashMap<>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if (obj instanceof Fragment) {
            // record the fragment tag here.
            Fragment f = (Fragment) obj;
            String tag = f.getTag();
            fragmentTags.put(position, tag);
            //UiUpdateInterface t = (UiUpdateInterface) f;
            //t.updateEvents();
        }
        return obj;
    }

    public Fragment getFragment(int position) {
        String tag = fragmentTags.get(position);
        if (tag == null)
            return null;
        return fm.findFragmentByTag(tag);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return new MapsRootFragment();
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return new CreateStoragePage2Fragment();
            case 2: // Fragment # 0 - This will show FirstFragment different title
                return new CreateStoragePage1Fragment();
            case 3: // Fragment # 0 - This will show FirstFragment different title
                return new UserFragment();
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {

   //     if (position == 0) {
            SpannableStringBuilder sb = new SpannableStringBuilder(" "); // space added before text for convenience

            Drawable drawable = context.getResources().getDrawable(tabIcons[position]);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
            sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
 /*       }
        if (position == 1) {
            return context.getResources().getString(R.string.userTitle);
        }
        return null;*/
    }
}
