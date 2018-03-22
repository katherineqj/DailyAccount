package com.katherine_qj.saver.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.katherine_qj.saver.R;
import com.katherine_qj.saver.model.KKMoney;
import com.katherine_qj.saver.model.RecordManager;
import com.katherine_qj.saver.util.KKMoneyUtil;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

public class SplashActivity extends Activity {

    private Context mContext;

    private LineChartView chart;
    private PieChartView mPieChartView;
    private LineChartData data;

    private RevealFrameLayout reveal;
    private LinearLayout ly;

    private ImageView image;
    private TextView appName;
    private TextView loadingText;

    private boolean loadDataCompleted = false;
    private boolean showAnimationCompleted = false;
    private boolean activityStarted = false;

    private final int NUMBER_OF_LINES = 1;


    /*========= 状态相关 =========*/
    private boolean isExploded = false;                 //每块之间是否分离
    private boolean isHasLabelsInside = false;          //标签在内部
    private boolean isHasLabelsOutside = false;         //标签在外部
    private boolean isHasCenterCircle = false;          //空心圆环
    private boolean isPiesHasSelected = false;          //块选中标签样式
    private boolean isHasCenterSingleText = false;      //圆环中心单行文字
    private boolean isHasCenterDoubleText = false;      //圆环中心双行文字

    /*========= 数据相关 =========*/
    private PieChartData mPieChartData;                 //饼状图数据


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mContext = this;

        chart = (LineChartView) findViewById(R.id.chart);
        mPieChartView = (PieChartView)findViewById(R.id.pie);
        List<Line> lines = new ArrayList<Line>();
        for (int i = 0; i < NUMBER_OF_LINES; ++i) {

            List<PointValue> values = new ArrayList<PointValue>();
            values.add(new PointValue(0, 0));
            values.add(new PointValue(1, 15));
            values.add(new PointValue(2, 10));
            values.add(new PointValue(3, 23));
            values.add(new PointValue(3.5f, 48));
            values.add(new PointValue(5, 60));

            Line line = new Line(values);
            line.setColor(Color.WHITE);
            line.setShape(ValueShape.CIRCLE);
            line.setCubic(false);
            line.setFilled(false);
            line.setHasLabels(false);
            line.setHasLabelsOnlyForSelected(false);
            line.setHasLines(true);
            line.setHasPoints(true);
            lines.add(line);
        }
        data = new LineChartData(lines);
        data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart.setLineChartData(data);
        initData();

        image = (ImageView)findViewById(R.id.image);
        appName = (TextView)findViewById(R.id.app_name);
        appName.setTypeface(KKMoneyUtil.getInstance().typefaceLatoLight);
        loadingText = (TextView)findViewById(R.id.loading_text);
        loadingText.setTypeface(KKMoneyUtil.getInstance().typefaceLatoLight);

        reveal = (RevealFrameLayout)findViewById(R.id.reveal);
        ly = (LinearLayout)findViewById(R.id.ly);

        new InitData().execute();
        initData();
    }
    public void initData()
    {
        setPieDatas();
    }
    private void setPieDatas() {
        int numValues = 6;                //把一张饼切成6块

        /*===== 随机设置每块的颜色和数据 =====*/
        List<SliceValue> values = new ArrayList<>();
        for (int i = 0; i < numValues; ++i) {
            SliceValue sliceValue = new SliceValue((float) Math.random() * 30 + 15, ChartUtils.pickColor());
            values.add(sliceValue);
        }

        /*===== 设置相关属性 类似Line Chart =====*/
        mPieChartData = new PieChartData(values);
        mPieChartData.setHasLabels(isHasLabelsInside);
        mPieChartData.setHasLabelsOnlyForSelected(isPiesHasSelected);
        mPieChartData.setHasLabelsOutside(isHasLabelsOutside);
        mPieChartData.setHasCenterCircle(isHasCenterCircle);

        //是否分离
        if (isExploded) {
            mPieChartData.setSlicesSpacing(18);                 //分离间距为18
        }

        //是否显示单行文本
        if (isHasCenterSingleText) {
            mPieChartData.setCenterText1("Hello");             //文本内容
        }

        //是否显示双行文本

        mPieChartView.setPieChartData(mPieChartData);         //设置控件
    }


    private void startCircularReveal() {
        // get the center for the clipping circle
        int[] location = new int[2];
        image.getLocationOnScreen(location);
        int cx = location[0] + KKMoneyUtil.dpToPx(24);
        int cy = location[1] + KKMoneyUtil.dpToPx(24);

        // get the final radius for the clipping circle
        int dx = Math.max(cx, ly.getWidth() - cx);
        int dy = Math.max(cy, ly.getHeight() - cy);
        float finalRadius = (float) Math.hypot(dx, dy);

        SupportAnimator animator =
                ViewAnimationUtils.createCircularReveal(ly, cx, cy, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(4000);
        animator.start();
        animator.addListener(new SupportAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                Log.d("KKMoney", "Showing animation completed");
                showAnimationCompleted = true;
                if (loadDataCompleted && showAnimationCompleted && !activityStarted) {
                    activityStarted = true;
                    startActivity(new Intent(mContext, MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onAnimationCancel() {

            }

            @Override
            public void onAnimationRepeat() {

            }
        });
        hasAnimationStarted = true;
    }

    private boolean hasAnimationStarted;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !hasAnimationStarted) {
            startCircularReveal();
        }
    }

    public class InitData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Looper.prepare();
            Bmob.initialize(KKMoneyApplication.getAppContext(), KKMoney.Bomb_APPLICATION_ID);
            CrashReport.initCrashReport(KKMoneyApplication.getAppContext(), KKMoney.Bugly_APP_ID, false);
            RecordManager.getInstance(KKMoneyApplication.getAppContext());
            KKMoneyUtil.init(KKMoneyApplication.getAppContext());
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.d("KKMoney", "Loading Data completed");
            loadingText.setText(mContext.getResources().getString(R.string.loaded));
            loadDataCompleted = true;
            if (loadDataCompleted && showAnimationCompleted && !activityStarted) {
                activityStarted = true;
                startActivity(new Intent(mContext, MainActivity.class));
                finish();
            }
        }
    }
}
