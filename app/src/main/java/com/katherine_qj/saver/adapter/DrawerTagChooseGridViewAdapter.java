package com.katherine_qj.saver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.katherine_qj.saver.R;
import com.katherine_qj.saver.model.RecordManager;
import com.katherine_qj.saver.util.KKMoneyUtil;

/**
 * Created by katherineqj on 2017/11/10.
 */

public class DrawerTagChooseGridViewAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    public DrawerTagChooseGridViewAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return RecordManager.TAGS.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.item_tag_choose, null);
            holder.tagImage = (ImageView) convertView.findViewById(R.id.tag_image);
            holder.tagName = (TextView) convertView.findViewById(R.id.tag_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tagImage.setImageResource(
                KKMoneyUtil.GetTagIcon(RecordManager.TAGS.get(position).getId()));
        holder.tagName.setText(KKMoneyUtil.GetTagName(RecordManager.TAGS.get(position).getId()));
        holder.tagName.setTypeface(KKMoneyUtil.GetTypeface());

        return convertView;
    }

    private class ViewHolder {
        ImageView tagImage;
        TextView tagName;
    }
}
