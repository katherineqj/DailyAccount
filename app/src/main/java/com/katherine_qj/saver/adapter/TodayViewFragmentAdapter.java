package com.katherine_qj.saver.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.katherine_qj.saver.fragment.TodayViewFragment;
import com.katherine_qj.saver.util.KKMoneyUtil;

/**
 * Created by katherineqj on 2017/10/20.
 */

public class TodayViewFragmentAdapter extends FragmentStatePagerAdapter {

    private static int TODAY_VIEW_FRAGMENT_NUMBER = 8;

    public TodayViewFragmentAdapter(android.support.v4.app.FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return TodayViewFragment.newInstance(i);
    }

    @Override
    public int getCount() {
        return TODAY_VIEW_FRAGMENT_NUMBER;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return KKMoneyUtil.GetTodayViewTitle(position % TODAY_VIEW_FRAGMENT_NUMBER);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
