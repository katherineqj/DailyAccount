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

import com.katherine_qj.saver.R;
import com.katherine_qj.saver.activity.KKMoneyApplication;
import com.katherine_qj.saver.model.RecordManager;
import com.katherine_qj.saver.model.SettingManager;
import com.katherine_qj.saver.util.KKMoneyUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by katherineqj on 2017/10/27.
 */

public class EditRemarkFragment extends Fragment {

    private int fragmentPosition;
    private int tagId = -1;

    public MaterialEditText editView;

    private View mView;

    Activity activity;

    static public EditRemarkFragment newInstance(int position, int type) {
        EditRemarkFragment fragment = new EditRemarkFragment();

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putInt("type", type);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.edit_remark_fragment, container, false);
        editView = (MaterialEditText)mView.findViewById(R.id.remark);

        if (getArguments().getInt("type") == KKMoneyFragmentManager.MAIN_ACTIVITY_FRAGMENT) {
            KKMoneyFragmentManager.mainActivityEditRemarkFragment = this;
        } else if (getArguments().getInt("type") == KKMoneyFragmentManager.EDIT_RECORD_ACTIVITY_FRAGMENT) {
            KKMoneyFragmentManager.editRecordActivityEditRemarkFragment = this;
        }

        boolean shouldChange
                = SettingManager.getInstance().getIsMonthLimit()
                && SettingManager.getInstance().getIsColorRemind()
                && RecordManager.getCurrentMonthExpense()
                >= SettingManager.getInstance().getMonthWarning();

        setEditColor(shouldChange);

        if (getArguments().getInt("type") == KKMoneyFragmentManager.EDIT_RECORD_ACTIVITY_FRAGMENT
                && KKMoneyUtil.editRecordPosition != -1) {
            KKMoneyFragmentManager.editRecordActivityEditRemarkFragment
                    .setRemark(RecordManager.SELECTED_RECORDS.get(KKMoneyUtil.editRecordPosition).getRemark());
            KKMoneyFragmentManager.editRecordActivityEditRemarkFragment.setLastSelection();
        }

        return mView;
    }

    public interface OnTagItemSelectedListener {
        void onTagItemPicked(int position);
    }

    public void updateTags() {

    }

    public int getTagId() {
        return tagId;
    }

    public void setTag(int p) {
        tagId = RecordManager.TAGS.get(p).getId();
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

    public void setLastSelection() {
        editView.setSelection(editView.getText().length());
    }

    public void editRequestFocus() {
        editView.requestFocus();
        InputMethodManager keyboard = (InputMethodManager)
                KKMoneyApplication.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(editView, InputMethodManager.SHOW_IMPLICIT);
    }

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

    public String getRemark() {
        return editView.getText().toString();
    }

    public void setRemark(String string) {
        editView.setText(string);
    }

}
