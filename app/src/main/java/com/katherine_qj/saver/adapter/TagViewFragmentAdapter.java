package com.katherine_qj.saver.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.katherine_qj.saver.activity.KKMoneyApplication;
import com.katherine_qj.saver.fragment.TagViewFragment;
import com.katherine_qj.saver.model.RecordManager;
import com.katherine_qj.saver.util.KKMoneyUtil;

/**
 * Created by katherineqj on 2017/10/20.
 */
public class TagViewFragmentAdapter extends FragmentStatePagerAdapter {

    public TagViewFragmentAdapter(android.support.v4.app.FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return TagViewFragment.newInstance(i);
    }

    @Override
    public int getCount() {
        return RecordManager.getInstance(KKMoneyApplication.getAppContext()).TAGS.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return KKMoneyUtil.GetTagName(
                RecordManager.getInstance(KKMoneyApplication.getAppContext()).TAGS.get(position % RecordManager.TAGS.size()).getId());
    }
}
