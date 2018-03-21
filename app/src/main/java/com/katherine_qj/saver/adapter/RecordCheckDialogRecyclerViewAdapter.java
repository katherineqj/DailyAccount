package com.katherine_qj.saver.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.katherine_qj.saver.R;
import com.katherine_qj.saver.model.KKMoneyRecord;
import com.katherine_qj.saver.model.RecordManager;
import com.katherine_qj.saver.util.KKMoneyUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by katherineqj on 2017/11/1.
 */
public class RecordCheckDialogRecyclerViewAdapter extends RecyclerView.Adapter<RecordCheckDialogRecyclerViewAdapter.viewHolder> {

    private OnItemClickListener onItemClickListener;

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<KKMoneyRecord> KKMoneyRecords;

    public RecordCheckDialogRecyclerViewAdapter(Context context, List<KKMoneyRecord> list) {
        KKMoneyRecords = new ArrayList<>();
        KKMoneyRecords = list;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public RecordCheckDialogRecyclerViewAdapter(Context context, List<KKMoneyRecord> list, OnItemClickListener onItemClickListener) {
        KKMoneyRecords = new ArrayList<>();
        KKMoneyRecords = list;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new viewHolder(mLayoutInflater.inflate(R.layout.record_check_item, parent, false));
    }

    @Override
    public void onBindViewHolder(viewHolder holder, final int position) {
        holder.imageView.setImageResource(
                KKMoneyUtil.GetTagIcon(KKMoneyRecords.get(position).getTag()));
        holder.date.setText(KKMoneyRecords.get(position).getCalendarString());
        holder.date.setTypeface(KKMoneyUtil.typefaceLatoLight);
        holder.money.setTypeface(KKMoneyUtil.typefaceLatoLight);
        holder.money.setText(String.valueOf((int) KKMoneyRecords.get(position).getMoney()));
        holder.money.setTextColor(
                KKMoneyUtil.GetTagColorResource(RecordManager.TAGS.get(KKMoneyRecords.get(position).getTag()).getId()));
        holder.index.setText((position + 1) + "");
        holder.index.setTypeface(KKMoneyUtil.typefaceLatoLight);
        holder.remark.setText(KKMoneyRecords.get(position).getRemark());
        holder.remark.setTypeface(KKMoneyUtil.typefaceLatoLight);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (KKMoneyRecords == null) {
            return 0;
        }
        return KKMoneyRecords.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @InjectView(R.id.image_view)
        ImageView imageView;
        @InjectView(R.id.date)
        TextView date;
        @InjectView(R.id.remark)
        TextView remark;
        @InjectView(R.id.money)
        TextView money;
        @InjectView(R.id.index)
        TextView index;
        @InjectView(R.id.material_ripple_layout)
        MaterialRippleLayout layout;

        viewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }

        @Override
        public void onClick(View v) {
//            onItemClickListener.onItemClick(v, getPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }
}