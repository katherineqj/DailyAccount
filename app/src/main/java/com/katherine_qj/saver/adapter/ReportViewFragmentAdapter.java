package com.katherine_qj.saver.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.katherine_qj.saver.fragment.ReportViewFragment;

/**
 * Created by katherineqj on 2017/10/20.
 */

// Todo optimize this

public class ReportViewFragmentAdapter extends FragmentStatePagerAdapter {

    public ReportViewFragmentAdapter(android.support.v4.app.FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return ReportViewFragment.newInstance();
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ReportViewFragment.REPORT_TITLE;
    }
}
