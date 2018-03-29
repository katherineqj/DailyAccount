package com.katherine_qj.saver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.katherine_qj.saver.R;
import com.katherine_qj.saver.model.RecordManager;
import com.katherine_qj.saver.model.SettingManager;
import com.katherine_qj.saver.util.KKMoneyUtil;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;

/**
 * Created by katherineqj on 2017/10/16.
 * grid 输入钱和密码的自制键盘adapter
 */

public class ButtonGridViewAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context mContext;

    public ButtonGridViewAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return KKMoneyUtil.BUTTONS.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.button_gridview_item, null);
            holder.fl = (FrameLayout)convertView.findViewById(R.id.frame_layout);
            holder.iv = (MaterialIconView)convertView.findViewById(R.id.icon);
            holder.tv = (TextView) convertView.findViewById(R.id.textview);
            holder.ml = (MaterialRippleLayout)convertView.findViewById(R.id.material_ripple_layout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//gridview是从0开始的，这样往下算
        if (position == 11) {
            holder.tv.setTypeface(KKMoneyUtil.typefaceLatoHairline);
            holder.tv.setVisibility(View.INVISIBLE);
            holder.iv.setIcon(MaterialDrawableBuilder.IconValue.CHECK);
            holder.ml.setRippleAlpha(50);
        } else if (position == 9) {
            holder.iv.setIcon(MaterialDrawableBuilder.IconValue.ERASER);
            holder.tv.setTypeface(KKMoneyUtil.typefaceLatoHairline);
            holder.tv.setVisibility(View.INVISIBLE);
            holder.ml.setRippleAlpha(50);
        } else {
            holder.iv.setVisibility(View.INVISIBLE);
            holder.tv.setTypeface(KKMoneyUtil.typefaceLatoHairline);
            holder.tv.setText(KKMoneyUtil.BUTTONS[position]);
            holder.ml.setRippleDelayClick(false);
        }

        holder.ml.setRippleDuration(300);
        boolean shouldChange
                = SettingManager.getInstance().getIsMonthLimit()
                && SettingManager.getInstance().getIsColorRemind()
                && RecordManager.getCurrentMonthExpense()
                >= SettingManager.getInstance().getMonthWarning();
        if (shouldChange) {
            holder.fl.setBackgroundColor(
                    KKMoneyUtil.getAlphaColor(SettingManager.getInstance().getRemindColor()));
            holder.ml.setRippleColor(SettingManager.getInstance().getRemindColor());
            holder.iv.setColor(SettingManager.getInstance().getRemindColor());
            holder.tv.setTextColor(SettingManager.getInstance().getRemindColor());
        } else {
            holder.fl.setBackgroundColor(KKMoneyUtil.getAlphaColor(KKMoneyUtil.MY_Normally));
            holder.ml.setRippleColor(KKMoneyUtil.MY_Normally);
            holder.iv.setColor(KKMoneyUtil.MY_Normally);
            holder.tv.setTextColor(KKMoneyUtil.MY_Normally);
        }


        return convertView;
    }

    private class ViewHolder {
        FrameLayout fl;
        TextView tv;
        MaterialIconView iv;
        MaterialRippleLayout ml;
    }
}
