package com.katherine_qj.saver.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.katherine_qj.saver.activity.CoCoinApplication;
import com.katherine_qj.saver.fragment.TagViewFragment;
import com.katherine_qj.saver.model.RecordManager;
import com.katherine_qj.saver.util.CoCoinUtil;

/**
 * Created by 伟平 on 2015/10/20.
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
        return RecordManager.getInstance(CoCoinApplication.getAppContext()).TAGS.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return CoCoinUtil.GetTagName(
                RecordManager.getInstance(CoCoinApplication.getAppContext()).TAGS.get(position % RecordManager.TAGS.size()).getId());
    }
}
