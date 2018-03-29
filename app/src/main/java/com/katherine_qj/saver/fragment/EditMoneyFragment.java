package com.katherine_qj.saver.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.katherine_qj.saver.R;
import com.katherine_qj.saver.activity.KKMoneyApplication;
import com.katherine_qj.saver.model.RecordManager;
import com.katherine_qj.saver.model.SettingManager;
import com.katherine_qj.saver.util.KKMoneyUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by katherineqj on 2017/10/27.
 * 编辑花费的viewpager0页面
 */

public class EditMoneyFragment extends Fragment {

    private int fragmentPosition;
    private int tagId = -1;

    public MaterialEditText editView;

    public ImageView tagImage;
    public TextView tagName;

    private View mView;

    Activity activity;

    static public EditMoneyFragment newInstance(int position, int type) {
        EditMoneyFragment fragment = new EditMoneyFragment();

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putInt("type", type);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.edit_money_fragment, container, false);

        if (getArguments().getInt("type") == KKMoneyFragmentManager.MAIN_ACTIVITY_FRAGMENT) {
            KKMoneyFragmentManager.mainActivityEditMoneyFragment = this;
        } else if (getArguments().getInt("type") == KKMoneyFragmentManager.EDIT_RECORD_ACTIVITY_FRAGMENT) {
            KKMoneyFragmentManager.editRecordActivityEditMoneyFragment = this;
        }

        fragmentPosition = getArguments().getInt("position");
        editView = (MaterialEditText)mView.findViewById(R.id.money);
        tagImage = (ImageView)mView.findViewById(R.id.tag_image);
        tagName = (TextView)mView.findViewById(R.id.tag_name);
        tagName.setTypeface(KKMoneyUtil.typefaceLatoLight);

        editView.setTypeface(KKMoneyUtil.typefaceLatoHairline);
        editView.setText("0");
        editView.requestFocus();
        editView.setHelperText(" ");
        editView.setKeyListener(null);
        editView.setOnClickListener(null);
        editView.setOnTouchListener(null);
//这个shoudlechange的意思就是改变颜色的辣个，判断开关都开并且本月花费大于警告预值
        boolean shouldChange
                = SettingManager.getInstance().getIsMonthLimit()
                && SettingManager.getInstance().getIsColorRemind()
                && RecordManager.getCurrentMonthExpense()
                >= SettingManager.getInstance().getMonthWarning();

        setEditColor(shouldChange);

        if (getArguments().getInt("type") == KKMoneyFragmentManager.EDIT_RECORD_ACTIVITY_FRAGMENT
                && KKMoneyUtil.editRecordPosition != -1) {
            KKMoneyFragmentManager.editRecordActivityEditMoneyFragment
                    .setTagImage(KKMoneyUtil.GetTagIcon(
                            (int)RecordManager.SELECTED_RECORDS.get(KKMoneyUtil.editRecordPosition).getTag()));
            KKMoneyFragmentManager.editRecordActivityEditMoneyFragment
                    .setTagName(KKMoneyUtil.GetTagName(
                            (int)RecordManager.SELECTED_RECORDS.get(KKMoneyUtil.editRecordPosition).getTag()));
            KKMoneyFragmentManager.editRecordActivityEditMoneyFragment
                    .setTagId(RecordManager.SELECTED_RECORDS.get(KKMoneyUtil.editRecordPosition).getTag());
            KKMoneyFragmentManager.editRecordActivityEditMoneyFragment
                    .setNumberText(String.format("%.0f", RecordManager.SELECTED_RECORDS.get(KKMoneyUtil.editRecordPosition).getMoney()));
        }

        return mView;
    }

    public interface OnTagItemSelectedListener {
        void onTagItemPicked(int position);
    }


    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public void setTag(int p) {
        tagId = RecordManager.TAGS.get(p).getId();
        tagName.setText(KKMoneyUtil.GetTagName(RecordManager.TAGS.get(p).getId()));
        tagImage.setImageResource(KKMoneyUtil.GetTagIcon(RecordManager.TAGS.get(p).getId()));
    }

    public String getNumberText() {
        return editView.getText().toString();
    }

    public void setNumberText(String string) {
        editView.setText(string);
    }

    public String getHelpText() {
        return editView.getHelperText();
    }

    public void setHelpText(String string) {
        editView.setHelperText(string);
    }

    public void editRequestFocus() {
        editView.requestFocus();
        InputMethodManager imm = (InputMethodManager)
                KKMoneyApplication.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
    }
    //改变的是记录钱界面的edittext 的所有颜色
    public void setEditColor(boolean shouldChange) {
        if (shouldChange) {
            editView.setTextColor(SettingManager.getInstance().getRemindColor());
            editView.setPrimaryColor(SettingManager.getInstance().getRemindColor());
            editView.setHelperTextColor(SettingManager.getInstance().getRemindColor());
        } else {
            editView.setTextColor(KKMoneyUtil.getInstance().MY_Normally);
            editView.setPrimaryColor(KKMoneyUtil.getInstance().MY_Normally);
            editView.setHelperTextColor(KKMoneyUtil.getInstance().MY_Normally);
        }
    }

    public void setTagName(String name) {
        tagName.setText(name);
    }

    public void setTagImage(int resource) {
        tagImage.setImageResource(resource);
    }

    public void getTagPosition(int[] position) {
        tagImage.getLocationOnScreen(position);
        position[0] += tagImage.getWidth() / 2;
        position[1] += tagImage.getHeight() / 2;
    }

}
