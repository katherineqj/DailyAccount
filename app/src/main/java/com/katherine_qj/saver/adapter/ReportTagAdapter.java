package com.katherine_qj.saver.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.katherine_qj.saver.R;
import com.katherine_qj.saver.fragment.ReportViewFragment;
import com.katherine_qj.saver.util.KKMoneyUtil;

import java.util.ArrayList;

/**
 * Created by katherineqj on 2018/1/30.
 */
public class ReportTagAdapter extends BaseAdapter {

    private ArrayList<double[]> tagExpense;

    public ReportTagAdapter(ArrayList<double[]> tagExpense) {
        this.tagExpense = tagExpense;
    }

    @Override
    public int getCount() {
        return min(tagExpense.size() - 1, ReportViewFragment.MAX_TAG_EXPENSE);
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_tag, null);

        ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
        TextView name = (TextView)convertView.findViewById(R.id.tag_name);
        TextView expense = (TextView)convertView.findViewById(R.id.tag_expense);
        TextView records = (TextView)convertView.findViewById(R.id.tag_sum);

        name.setTypeface(KKMoneyUtil.getInstance().typefaceLatoLight);
        expense.setTypeface(KKMoneyUtil.getInstance().typefaceLatoLight);
        records.setTypeface(KKMoneyUtil.getInstance().typefaceLatoLight);

        icon.setImageDrawable(KKMoneyUtil.getInstance().GetTagIconDrawable((int)tagExpense.get(position + 1)[2]));
        name.setText(KKMoneyUtil.getInstance().GetTagName((int)tagExpense.get(position + 1)[2]) + KKMoneyUtil.getInstance().GetPurePercentString(tagExpense.get(position + 1)[1] * 100));
        expense.setText(KKMoneyUtil.getInstance().GetInMoney((int)tagExpense.get(position + 1)[0]));
        records.setText(KKMoneyUtil.getInstance().GetInRecords((int)tagExpense.get(position + 1)[3]));

        return convertView;
    }

    private int min(int a, int b) {
        return (a < b ? a : b);
    }
}
