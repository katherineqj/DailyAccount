package com.katherine_qj.saver.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.katherine_qj.saver.R;
import com.katherine_qj.saver.adapter.ReportViewFragmentAdapter;
import com.katherine_qj.saver.fragment.ReportViewFragment;
import com.katherine_qj.saver.model.SettingManager;
import com.katherine_qj.saver.util.KKMoneyUtil;

public class AccountBookReportViewActivity extends AppCompatActivity
        implements
        ReportViewFragment.OnTitleChangedListener {

    private MaterialViewPager mViewPager;

    private Toolbar toolbar;

    private ReportViewFragmentAdapter reportViewFragmentAdapter = null;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        setContentView(R.layout.activity_account_book_report_view);

        mViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);

        View view = mViewPager.getRootView();
        TextView title = (TextView)view.findViewById(R.id.logo_white);
        title.setTypeface(KKMoneyUtil.typefaceLatoLight);
        title.setText(SettingManager.getInstance().getAccountBookName());

        mViewPager.getPagerTitleStrip().setTypeface(KKMoneyUtil.getInstance().typefaceLatoLight, Typeface.NORMAL);
        mViewPager.getPagerTitleStrip().setTextSize(45);
        mViewPager.getPagerTitleStrip().setUnderlineColor(Color.parseColor("#00000000"));
        mViewPager.getPagerTitleStrip().setIndicatorColor(Color.parseColor("#00000000"));
        mViewPager.getPagerTitleStrip().setUnderlineHeight(0);
        mViewPager.getPagerTitleStrip().setIndicatorHeight(0);

        mViewPager.getPagerTitleStrip().setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
//                if (KKMoneyFragmentManager.reportViewFragment != null)
//                    KKMoneyFragmentManager.reportViewFragment.showDataDialog();
            }
        });

        setTitle("");

        toolbar = mViewPager.getToolbar();

        if (toolbar != null) {
            setSupportActionBar(toolbar);

            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayUseLogoEnabled(false);
                actionBar.setHomeButtonEnabled(true);
            }
        }

        View logo = findViewById(R.id.logo_white);
        if (logo != null) {
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.notifyHeaderChanged();
                }
            });
        }

        reportViewFragmentAdapter = new ReportViewFragmentAdapter(getSupportFragmentManager());
        mViewPager.getViewPager().setOffscreenPageLimit(1);
        mViewPager.getViewPager().setAdapter(reportViewFragmentAdapter);
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

        mViewPager.getPagerTitleStrip().invalidate();

        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                return HeaderDesign.fromColorAndDrawable(
                        KKMoneyUtil.GetTagColor(-3),
                        KKMoneyUtil.GetTagDrawable(-3)
                );
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        if (KKMoneyFragmentManager.reportViewFragment != null)
//            KKMoneyFragmentManager.reportViewFragment.onDestroy();
//        KKMoneyFragmentManager.reportViewFragment = null;
        MaterialViewPagerHelper.unregister(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onTitleChanged() {
        mViewPager.getPagerTitleStrip().notifyDataSetChanged();
        mViewPager.getPagerTitleStrip().invalidate();
    }
}
