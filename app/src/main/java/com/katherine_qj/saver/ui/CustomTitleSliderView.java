package com.katherine_qj.saver.ui;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.katherine_qj.saver.R;
import com.katherine_qj.saver.activity.KKMoneyApplication;
import com.katherine_qj.saver.fragment.KKMoneyFragmentManager;
import com.katherine_qj.saver.util.KKMoneyUtil;

/**
 * Created by katherineqj on 2018/1/23.
 */
public class CustomTitleSliderView extends BaseSliderView {
    private static Typeface font = null;
    private String content;
    private int type;
    private TextView title;

    public CustomTitleSliderView(String content, int type) {
        super(KKMoneyApplication.getAppContext());
        this.content = content;
        this.type = type;
        if (type == KKMoneyFragmentManager.NUMBER_SLIDER) {
            KKMoneyFragmentManager.numberCustomTitleSliderView = this;
        } else if (type == KKMoneyFragmentManager.EXPENSE_SLIDER) {
            KKMoneyFragmentManager.expenseCustomTitleSliderView = this;
        }
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.custom_title_slider_view,null);

        LinearLayout description = (LinearLayout)v.findViewById(R.id.description_layout);
        description.setVisibility(View.GONE);

        title = (TextView)v.findViewById(R.id.title);
        title.setText(content);
        title.setTypeface(KKMoneyUtil.typefaceLatoLight);

        ImageView target = (ImageView)v.findViewById(R.id.daimajia_slider_image);
        bindEventAndShow(v, target);
        return v;
    }

    public void setTitle(String string) {
        title.setText(string);
    }
}
