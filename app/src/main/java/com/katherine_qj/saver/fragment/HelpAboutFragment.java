package com.katherine_qj.saver.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.katherine_qj.saver.R;
import com.katherine_qj.saver.util.KKMoneyUtil;

/**
 * Created by katherineqj on 2018/2/2.
 */
public class HelpAboutFragment extends Fragment {

    private ObservableScrollView mScrollView;

    public static HelpAboutFragment newInstance() {
        HelpAboutFragment fragment = new HelpAboutFragment();
        return fragment;
    }

    private Activity activity;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            activity = (Activity)context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_about_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mScrollView = (ObservableScrollView) view.findViewById(R.id.scrollView);
        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView, null);

        ((TextView)view.findViewById(R.id.content_0)).setTypeface(KKMoneyUtil.getInstance().typefaceLatoLight);
        ((TextView)view.findViewById(R.id.content_1)).setTypeface(KKMoneyUtil.getInstance().typefaceLatoLight);
        ((TextView)view.findViewById(R.id.content_2)).setTypeface(KKMoneyUtil.getInstance().typefaceLatoLight);
        ((TextView)view.findViewById(R.id.content_3)).setTypeface(KKMoneyUtil.getInstance().typefaceLatoLight);
        ((TextView)view.findViewById(R.id.content_4)).setTypeface(KKMoneyUtil.getInstance().typefaceLatoLight);
        ((TextView)view.findViewById(R.id.content_5)).setTypeface(KKMoneyUtil.getInstance().typefaceLatoLight);
        ((TextView)view.findViewById(R.id.content_6)).setTypeface(KKMoneyUtil.getInstance().typefaceLatoLight);
        ((TextView)view.findViewById(R.id.content_7)).setTypeface(KKMoneyUtil.getInstance().typefaceLatoLight);
        ((TextView)view.findViewById(R.id.content_8)).setTypeface(KKMoneyUtil.getInstance().typefaceLatoLight);
        ((TextView)view.findViewById(R.id.content_9)).setTypeface(KKMoneyUtil.getInstance().typefaceLatoLight);
        ((TextView)view.findViewById(R.id.content_10)).setTypeface(KKMoneyUtil.getInstance().typefaceLatoLight);

        ((MaterialRippleLayout)view.findViewById(R.id.layout_2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/katherineqj")));
            }
        });
        ((MaterialRippleLayout)view.findViewById(R.id.layout_3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://blog.csdn.net/katherine_qj")));
            }
        });
        ((MaterialRippleLayout)view.findViewById(R.id.layout_4)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KKMoneyUtil.getInstance().copyToClipboard("kathqin@gmail.com", mContext);
                KKMoneyUtil.getInstance().showToast(mContext, mContext.getResources().getString(R.string.copy_to_clipboard));
            }
        });
    }

}
