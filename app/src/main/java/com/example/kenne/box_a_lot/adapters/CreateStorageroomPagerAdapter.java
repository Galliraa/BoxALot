package com.example.kenne.box_a_lot.adapters;

import android.content.Context;
import android.view.ViewGroup;

import com.example.kenne.box_a_lot.fragments.CreateStoragePage1Fragment;
import com.example.kenne.box_a_lot.fragments.CreateStoragePage2Fragment;
import com.example.kenne.box_a_lot.fragments.CreateStoragePage3Fragment;
import com.example.kenne.box_a_lot.fragments.CreateStoragePage4Fragment;
import com.example.kenne.box_a_lot.interfaces.UpdateAble;

import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

//sources:
// https://github.com/codepath/android_guides/wiki/ViewPager-with-FragmentPagerAdapter

public class CreateStorageroomPagerAdapter extends FragmentPagerAdapter {

    private static int NUM_ITEMS = 4;
    private Context context;
    private Map<Integer,String> fragmentTags;
    private FragmentManager fm;

    public CreateStorageroomPagerAdapter(FragmentManager fragmentManager, Context c) {
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
                return new CreateStoragePage1Fragment();
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return new CreateStoragePage2Fragment();
            case 2: // Fragment # 0 - This will show FirstFragment different title
                return new CreateStoragePage3Fragment();
            case 3: // Fragment # 0 - This will show FirstFragment different title
                return new CreateStoragePage4Fragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        UpdateAble f = (UpdateAble) object;
        if (f != null) {
            f.update();
        }
        return super.getItemPosition(object);
    }
}