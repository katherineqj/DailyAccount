package com.katherine_qj.saver.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.katherine_qj.saver.fragment.EditMoneyFragment;
import com.katherine_qj.saver.fragment.EditRemarkFragment;

/**
 * Created by katherineqj on 2018/1/19.
 */
public class EditMoneyRemarkFragmentAdapter extends FragmentPagerAdapter {

    private int type;

    public EditMoneyRemarkFragmentAdapter(FragmentManager fm, int type) {
        super(fm);
        this.type = type;
    }
    //item0  是记录花费的页面，item1是详情备注页面

    @Override
    public Fragment getItem(int position) {
        if (position == 0) return EditMoneyFragment.newInstance(0, type);
        else return EditRemarkFragment.newInstance(1, type);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
