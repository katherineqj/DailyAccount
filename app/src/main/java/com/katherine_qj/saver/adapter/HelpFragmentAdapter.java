package com.katherine_qj.saver.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.katherine_qj.saver.R;
import com.katherine_qj.saver.activity.KKMoneyApplication;
import com.katherine_qj.saver.fragment.HelpAboutFragment;
import com.katherine_qj.saver.fragment.HelpKKMoneyFragment;
import com.katherine_qj.saver.fragment.HelpFeedbackFragment;

/**
 * Created by katherineqj on 2018/2/2.
 */

public class HelpFragmentAdapter extends FragmentStatePagerAdapter {

    private int position = 0;

    public HelpFragmentAdapter(android.support.v4.app.FragmentManager fm) {
        super(fm);
    }

    public HelpFragmentAdapter(android.support.v4.app.FragmentManager fm, int position) {
        super(fm);
        this.position = position;
    }

    @Override
    public Fragment getItem(int position) {
        switch (this.position) {
            case 0: return HelpKKMoneyFragment.newInstance();
            case 1: return HelpFeedbackFragment.newInstance();
            case 2: return HelpAboutFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (this.position) {
            case 0: return KKMoneyApplication.getAppContext().getResources().getString(R.string.app_name);
            case 1: return KKMoneyApplication.getAppContext().getResources().getString(R.string.feedback);
            case 2: return KKMoneyApplication.getAppContext().getResources().getString(R.string.about);
        }
        return "";
    }
}
