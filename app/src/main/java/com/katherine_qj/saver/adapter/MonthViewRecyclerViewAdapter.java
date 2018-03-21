package com.katherine_qj.saver.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.katherine_qj.saver.R;
import com.katherine_qj.saver.activity.KKMoneyApplication;
import com.katherine_qj.saver.fragment.RecordCheckDialogFragment;
import com.katherine_qj.saver.model.KKMoneyRecord;
import com.katherine_qj.saver.model.RecordManager;
import com.katherine_qj.saver.model.SettingManager;
import com.katherine_qj.saver.util.KKMoneyUtil;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by katherineqj on 2017/10/20.
 */

// Todo optimise this

public class MonthViewRecyclerViewAdapter
        extends RecyclerView.Adapter<MonthViewRecyclerViewAdapter.viewHolder> {

    private Context mContext;

    private List<KKMoneyRecord> list;

    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;

    private int fragmentPosition;

    private ArrayList<List<SliceValue>> sliceValuesList;
    private ArrayList<Map<Integer, Double>> TagExpanseList;
    private ArrayList<Map<Integer, ArrayList<KKMoneyRecord>>> ExpanseList;
    private ArrayList<PieChartData> pieChartDataList;
    private ArrayList<Double> SumList;
    private int records;
    private int tags;
    private ArrayList<Integer> selectedPositionList;
    private ArrayList<String> dateStringList;

    private ArrayList<String> dateShownStringList;
    private String dialogTitle;

    private int startYear;
    private int startMonth;

    private boolean IS_EMPTY = false;

    public MonthViewRecyclerViewAdapter(
            int start, int end, Context context, int position, int monthNumber) {
        list = new ArrayList<>();
        mContext = context;
        fragmentPosition = position;

        RecordManager recordManager = RecordManager.getInstance(mContext.getApplicationContext());

        if (start != -1) {
            for (int i = start; i >= end; i--) {
                list.add(recordManager.RECORDS.get(i));
            }
        }

        IS_EMPTY = list.isEmpty();

        if (!IS_EMPTY) {

            startYear = recordManager.RECORDS.get(0).getCalendar().get(Calendar.YEAR);
            startMonth = recordManager.RECORDS.get(0).getCalendar().get(Calendar.MONTH);

            sliceValuesList = new ArrayList<>();
            TagExpanseList = new ArrayList<>();
            ExpanseList = new ArrayList<>();
            pieChartDataList = new ArrayList<>();
            SumList = new ArrayList<>();
            selectedPositionList = new ArrayList<>();
            dateStringList = new ArrayList<>();
            dateShownStringList = new ArrayList<>();

            int nowYear = startYear + (startMonth + (monthNumber - fragmentPosition - 1)) / 12;
            int nowMonth = (startMonth + (monthNumber - fragmentPosition - 1)) % 12;

            Map<Integer, Double> TagExpanse;
            Map<Integer, ArrayList<KKMoneyRecord>> Expanse;
            List<SliceValue> sliceValues;
            PieChartData pieChartData;
            double Sum = 0;
            TagExpanse = new TreeMap<>();
            Expanse = new HashMap<>();
            sliceValues = new ArrayList<>();

            // for this month
            dateStringList.add(KKMoneyUtil.GetMonthShort(nowMonth + 1) + " " + nowYear);
            dateShownStringList.add(" in " + KKMoneyUtil.MONTHS_SHORT[nowMonth + 1] + " " + nowYear);
            selectedPositionList.add(0);
            for (int j = 2; j < recordManager.TAGS.size(); j++) {
                TagExpanse.put(recordManager.TAGS.get(j).getId(), Double.valueOf(0));
                Expanse.put(recordManager.TAGS.get(j).getId(), new ArrayList<KKMoneyRecord>());
            }
            for (KKMoneyRecord KKMoneyRecord : list) {
                if (KKMoneyRecord.getCalendar().get(Calendar.MONTH) == nowMonth) {
                    TagExpanse.put(KKMoneyRecord.getTag(),
                            TagExpanse.get(KKMoneyRecord.getTag()) + Double.valueOf(KKMoneyRecord.getMoney()));
                    Expanse.get(KKMoneyRecord.getTag()).add(KKMoneyRecord);
                    Sum += KKMoneyRecord.getMoney();
                    records++;
                }
            }

            TagExpanse = KKMoneyUtil.SortTreeMapByValues(TagExpanse);

            tags = 0;
            for (Map.Entry<Integer, Double> entry : TagExpanse.entrySet()) {
                if (entry.getValue() >= 1) {
                    // Todo optimize the GetTagColorResource
                    SliceValue sliceValue = new SliceValue(
                            (float) (double) entry.getValue(),
                            mContext.getResources().
                                    getColor(KKMoneyUtil.GetTagColorResource(entry.getKey())));
                    sliceValue.setLabel(String.valueOf(entry.getKey()));
                    sliceValues.add(sliceValue);
                    tags++;
                }
            }
            sliceValuesList.add(sliceValues);

            TagExpanseList.add(TagExpanse);
            ExpanseList.add(Expanse);
            SumList.add(Sum);
            pieChartData = new PieChartData(sliceValues);
            pieChartData.setHasLabels(false);
            pieChartData.setHasLabelsOnlyForSelected(false);
            pieChartData.setHasLabelsOutside(false);
            pieChartData.setHasCenterCircle(SettingManager.getInstance().getIsHollow());
            pieChartDataList.add(pieChartData);

            // for each week
            Calendar now = Calendar.getInstance();
            now.set(nowYear, nowMonth, 1, 0, 0, 0);
            now.add(Calendar.SECOND, 0);

            Calendar monthEnd = Calendar.getInstance();
            monthEnd.set(
                    nowYear, nowMonth, now.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
            monthEnd.add(Calendar.SECOND, 0);


            while (!now.after(monthEnd)) {
                Calendar leftWeekRange = KKMoneyUtil.GetThisWeekLeftRange(now);
                Calendar rightWeekRange = KKMoneyUtil.GetThisWeekRightRange(now);
                Calendar rightShownWeekRange = KKMoneyUtil.GetThisWeekRightShownRange(now);
                String dateString = KKMoneyUtil.GetMonthShort(leftWeekRange.get(Calendar.MONTH) + 1) + " " +
                        leftWeekRange.get(Calendar.DAY_OF_MONTH) + " " +
                        leftWeekRange.get(Calendar.YEAR) + " - " +
                        KKMoneyUtil.GetMonthShort(rightShownWeekRange.get(Calendar.MONTH) + 1) + " " +
                        rightShownWeekRange.get(Calendar.DAY_OF_MONTH) + " " +
                        rightShownWeekRange.get(Calendar.YEAR);
                dateStringList.add(dateString);
                dateShownStringList.add(" from " +
                        KKMoneyUtil.GetMonthShort(leftWeekRange.get(Calendar.MONTH) + 1) + " " +
                        leftWeekRange.get(Calendar.DAY_OF_MONTH) + " to " +
                        KKMoneyUtil.GetMonthShort(rightShownWeekRange.get(Calendar.MONTH) + 1) + " " +
                        rightShownWeekRange.get(Calendar.DAY_OF_MONTH));
                selectedPositionList.add(0);

                Sum = 0;
                TagExpanse = new TreeMap<>();
                Expanse = new HashMap<>();
                sliceValues = new ArrayList<>();

                for (int j = 2; j < recordManager.TAGS.size(); j++) {
                    TagExpanse.put(recordManager.TAGS.get(j).getId(), Double.valueOf(0));
                    Expanse.put(recordManager.TAGS.get(j).getId(), new ArrayList<KKMoneyRecord>());
                }
                for (KKMoneyRecord KKMoneyRecord : list) {
                    if (!KKMoneyRecord.getCalendar().before(leftWeekRange) &&
                            KKMoneyRecord.getCalendar().before(rightWeekRange)) {
                        TagExpanse.put(KKMoneyRecord.getTag(),
                                TagExpanse.get(KKMoneyRecord.getTag()) + Double.valueOf(KKMoneyRecord.getMoney()));
                        Expanse.get(KKMoneyRecord.getTag()).add(KKMoneyRecord);
                        Sum += KKMoneyRecord.getMoney();
                    }
                }

                TagExpanse = KKMoneyUtil.SortTreeMapByValues(TagExpanse);

                for (Map.Entry<Integer, Double> entry : TagExpanse.entrySet()) {
                    if (entry.getValue() >= 1) {
                        // Todo optimize the GetTagColorResource
                        SliceValue sliceValue = new SliceValue(
                                (float) (double) entry.getValue(),
                                mContext.getResources().
                                        getColor(KKMoneyUtil.GetTagColorResource(entry.getKey())));
                        sliceValue.setLabel(String.valueOf(entry.getKey()));
                        sliceValues.add(sliceValue);
                    }
                }
                sliceValuesList.add(sliceValues);

                TagExpanseList.add(TagExpanse);
                ExpanseList.add(Expanse);
                SumList.add(Sum);
                pieChartData = new PieChartData(sliceValues);
                pieChartData.setHasLabels(false);
                pieChartData.setHasLabelsOnlyForSelected(false);
                pieChartData.setHasLabelsOutside(false);
                pieChartData.setHasCenterCircle(SettingManager.getInstance().getIsHollow());
                pieChartDataList.add(pieChartData);

                now = KKMoneyUtil.GetNextWeekLeftRange(now);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_HEADER;
            default:
                return TYPE_CELL;
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public MonthViewRecyclerViewAdapter.viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            case TYPE_HEADER: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_month_list_view, parent, false);
                return new viewHolder(view) {
                };
            }
            case TYPE_CELL: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_month_list_view, parent, false);
                return new viewHolder(view) {
                };
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, final int position) {

        if (IS_EMPTY) {
            holder.expanseSum.setText("¥0");
            holder.expanseSum.setTypeface(KKMoneyUtil.typefaceLatoLight);
            holder.emptyTip.setText(mContext.getResources().getString(R.string.tag_empty));
            holder.emptyTip.setTypeface(KKMoneyUtil.GetTypeface());
            holder.tags.setVisibility(View.GONE);
            holder.date.setVisibility(View.INVISIBLE);
            holder.pie.setVisibility(View.INVISIBLE);
            holder.iconLeft.setVisibility(View.INVISIBLE);
            holder.iconRight.setVisibility(View.INVISIBLE);
            holder.all.setVisibility(View.GONE);
        } else {
            holder.date.setText(dateStringList.get(position));
            holder.expanseSum.setText(KKMoneyUtil.GetInMoney((int) (double) SumList.get(position)));

            holder.date.setTypeface(KKMoneyUtil.GetTypeface());
            holder.expanseSum.setTypeface(KKMoneyUtil.typefaceLatoLight);

            holder.tags.setTypeface(KKMoneyUtil.typefaceLatoLight);
            if ("zh".equals(KKMoneyUtil.GetLanguage())) {
                holder.tags.setText(" ● " + records + KKMoneyApplication.getAppContext().getResources().getString(R.string.report_view_records) + tags + KKMoneyApplication.getAppContext().getResources().getString(R.string.report_view_tags));
            } else {
                holder.tags.setText(" ● " + records + " " + KKMoneyApplication.getAppContext().getResources().getString(R.string.report_view_records) + " " + tags + " " + KKMoneyApplication.getAppContext().getResources().getString(R.string.report_view_tags));
            }

            if (SumList.get(position).equals(Double.valueOf(0))) {
                holder.emptyTip.setVisibility(View.VISIBLE);
                holder.emptyTip.setTypeface(KKMoneyUtil.typefaceLatoLight);
            } else {
                holder.emptyTip.setVisibility(View.GONE);
            }

            holder.pie.setPieChartData(pieChartDataList.get(position));
            holder.pie.setOnValueTouchListener(new PieValueTouchListener(position));
            holder.pie.setChartRotationEnabled(false);

            if (!SumList.get(position).equals(Double.valueOf(0))) {
                holder.iconRight.setVisibility(View.VISIBLE);
                holder.iconRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedPositionList.set(position,
                                (selectedPositionList.get(position) + 1)
                                        % sliceValuesList.get(position).size());
                        SelectedValue selectedValue =
                                new SelectedValue(
                                        selectedPositionList.get(position),
                                        0,
                                        SelectedValue.SelectedValueType.NONE);
                        holder.pie.selectValue(selectedValue);
                    }
                });
                holder.iconLeft.setVisibility(View.VISIBLE);
                holder.iconLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedPositionList.set(position,
                                (selectedPositionList.get(position) - 1
                                        + sliceValuesList.get(position).size())
                                        % sliceValuesList.get(position).size());
                        SelectedValue selectedValue =
                                new SelectedValue(
                                        selectedPositionList.get(position),
                                        0,
                                        SelectedValue.SelectedValueType.NONE);
                        holder.pie.selectValue(selectedValue);
                    }
                });
            } else {
                holder.iconLeft.setVisibility(View.GONE);
                holder.iconRight.setVisibility(View.GONE);
            }

            holder.all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("zh".equals(KKMoneyUtil.GetLanguage())) {
                        dialogTitle = mContext.getResources().getString(R.string.in)
                                + dateStringList.get(position) +
                                " " + KKMoneyUtil.GetSpendString((int) (double) SumList.get(position));
                    } else {
                        dialogTitle = KKMoneyUtil.GetSpendString((int) (double) SumList.get(position)) +
                                mContext.getResources().getString(R.string.in) + " "
                                + dateStringList.get(position);
                    }
                    ((FragmentActivity) mContext).getSupportFragmentManager()
                            .beginTransaction()
                            .add(new RecordCheckDialogFragment(
                                            mContext, list, dialogTitle),
                                    "MyDialog")
                            .commit();
                }
            });
        }

    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        @Optional
        @InjectView(R.id.date)
        TextView date;
        @Optional
        @InjectView(R.id.expanse)
        TextView expanseSum;
        @Optional
        @InjectView(R.id.tags)
        TextView tags;
        @Optional
        @InjectView(R.id.empty_tip)
        TextView emptyTip;
        @Optional
        @InjectView(R.id.chart_pie)
        PieChartView pie;
        @Optional
        @InjectView(R.id.icon_left)
        MaterialIconView iconLeft;
        @Optional
        @InjectView(R.id.icon_right)
        MaterialIconView iconRight;
        @Optional
        @InjectView(R.id.all)
        MaterialIconView all;

        viewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    private class PieValueTouchListener implements PieChartOnValueSelectListener {

        private int position;

        public PieValueTouchListener(int position) {
            this.position = position;
        }

        @Override
        public void onValueSelected(int i, SliceValue sliceValue) {
            String text = "";
            final int tagId = Integer.valueOf(String.valueOf(sliceValue.getLabelAsChars()));
            Double percent = sliceValue.getValue() / SumList.get(position) * 100;
            if ("zh".equals(KKMoneyUtil.GetLanguage())) {
                text = KKMoneyUtil.GetSpendString((int) sliceValue.getValue()) +
                        KKMoneyUtil.GetPercentString(percent) + "\n" +
                        "于" + KKMoneyUtil.GetTagName(tagId);
                dialogTitle = mContext.getResources().getString(R.string.in)
                        + dateStringList.get(position) +
                        " " + KKMoneyUtil.GetSpendString((int) sliceValue.getValue()) + "\n" +
                        "于" + KKMoneyUtil.GetTagName(tagId);

            } else {
                text = KKMoneyUtil.GetSpendString((int) sliceValue.getValue()) +
                        KKMoneyUtil.GetPercentString(percent) + "\n" +
                        "in " + KKMoneyUtil.GetTagName(RecordManager.TAGS.get(tagId).getId());
                dialogTitle = KKMoneyUtil.GetSpendString((int) sliceValue.getValue()) +
                        mContext.getResources().getString(R.string.in) + " "
                        + dateStringList.get(position) + "\n" +
                        "in " + KKMoneyUtil.GetTagName(RecordManager.TAGS.get(tagId).getId());
            }
            Snackbar snackbar =
                    Snackbar
                            .with(mContext)
                            .type(SnackbarType.MULTI_LINE)
                            .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                            .position(Snackbar.SnackbarPosition.BOTTOM)
                            .margin(15, 15)
                            .backgroundDrawable(KKMoneyUtil.GetSnackBarBackground(fragmentPosition - 2))
                            .text(text)
                            .textTypeface(KKMoneyUtil.GetTypeface())
                            .textColor(Color.WHITE)
                            .actionLabelTypeface(KKMoneyUtil.GetTypeface())
                            .actionLabel(mContext.getResources().getString(R.string.check))
                            .actionColor(Color.WHITE)
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(Snackbar snackbar) {
                                    List<KKMoneyRecord> shownKKMoneyRecords
                                            = ExpanseList.get(position).get(tagId);
                                    ((FragmentActivity) mContext).getSupportFragmentManager()
                                            .beginTransaction()
                                            .add(new RecordCheckDialogFragment(
                                                            mContext, shownKKMoneyRecords, dialogTitle),
                                                    "MyDialog")
                                            .commit();
                                }
                            });
            SnackbarManager.show(snackbar);
        }

        @Override
        public void onValueDeselected() {

        }
    }

}
