package com.katherine_qj.saver.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.balysv.materialripple.MaterialRippleLayout;
/*import com.bmob.BmobProFile;
import com.bmob.btp.callback.DeleteFileListener;
import com.bmob.btp.callback.DownloadListener;
import com.bmob.btp.callback.UploadListener;*/
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.github.johnpersano.supertoasts.SuperToast;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.katherine_qj.saver.BuildConfig;
import com.katherine_qj.saver.R;
import com.katherine_qj.saver.adapter.TodayViewFragmentAdapter;
import com.katherine_qj.saver.model.Logo;
import com.katherine_qj.saver.model.RecordManager;
import com.katherine_qj.saver.model.SettingManager;
import com.katherine_qj.saver.model.TaskManager;
import com.katherine_qj.saver.model.UploadInfo;
import com.katherine_qj.saver.model.User;
import com.katherine_qj.saver.ui.CustomSliderView;
import com.katherine_qj.saver.ui.MyQuery;
import com.katherine_qj.saver.ui.RiseNumberTextView;
import com.katherine_qj.saver.util.KKMoneyUtil;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountBookTodayViewActivity extends AppCompatActivity {

    private static final String FILE_SEPARATOR = "/";
    private static final String FILE_PATH = Environment.getExternalStorageDirectory() + FILE_SEPARATOR +"KKMoney" + FILE_SEPARATOR;
    private static final String FILE_NAME = FILE_PATH + "KKMoney Database.db";

    private MaterialViewPager mViewPager;

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;

    private TodayViewFragmentAdapter todayModeAdapter = null;

    private Context mContext;

    private MaterialRippleLayout custom;
    private MaterialRippleLayout tags;
    private MaterialRippleLayout months;
    private MaterialRippleLayout list;
    private MaterialRippleLayout report;
    private MaterialRippleLayout sync;
    private MaterialRippleLayout settings;
    private MaterialRippleLayout help;
    private MaterialRippleLayout feedback;
    private MaterialRippleLayout about;

    private MaterialIconView syncIcon;

    private TextView userName;
    private TextView userEmail;

    private TextView title;

    private TextView monthExpenseTip;
    private RiseNumberTextView monthExpense;

    private CircleImageView profileImage;
    private SliderLayout mDemoSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_book_today_view);
        SuperToast.cancelAllSuperToasts();

        mContext = this;

        mViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);
        userName = (TextView)findViewById(R.id.user_name);
        userEmail = (TextView)findViewById(R.id.user_email);
        userName.setTypeface(KKMoneyUtil.typefaceLatoRegular);
        userEmail.setTypeface(KKMoneyUtil.typefaceLatoLight);
        //获取用户信息
        User user = BmobUser.getCurrentUser(KKMoneyApplication.getAppContext(), User.class);
        if (user != null) {
            userName.setText(user.getUsername());
            userEmail.setText(user.getEmail());
        }
        setFonts();
        View view = mViewPager.getRootView();
        title = (TextView)view.findViewById(R.id.logo_white);
        title.setTypeface(KKMoneyUtil.typefaceLatoLight);
        title.setText(SettingManager.getInstance().getAccountBookName());//设置自定义账本名称

        mViewPager.getPagerTitleStrip().setTypeface(KKMoneyUtil.GetTypeface(), Typeface.NORMAL);

        setTitle("");

        toolbar = mViewPager.getToolbar();
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        custom = (MaterialRippleLayout)mDrawer.findViewById(R.id.custom_layout);
        tags = (MaterialRippleLayout)mDrawer.findViewById(R.id.tag_layout);
        months = (MaterialRippleLayout)mDrawer.findViewById(R.id.month_layout);
        list = (MaterialRippleLayout)mDrawer.findViewById(R.id.list_layout);
        report = (MaterialRippleLayout)mDrawer.findViewById(R.id.report_layout);
        sync = (MaterialRippleLayout)mDrawer.findViewById(R.id.sync_layout);
        settings = (MaterialRippleLayout)mDrawer.findViewById(R.id.settings_layout);
        help = (MaterialRippleLayout)mDrawer.findViewById(R.id.help_layout);
        feedback = (MaterialRippleLayout)mDrawer.findViewById(R.id.feedback_layout);
        about = (MaterialRippleLayout)mDrawer.findViewById(R.id.about_layout);
        syncIcon = (MaterialIconView)mDrawer.findViewById(R.id.sync_icon);
        setIconEnable(syncIcon, SettingManager.getInstance().getLoggenOn());
        monthExpenseTip = (TextView)mDrawer.findViewById(R.id.month_expense_tip);
        monthExpenseTip.setTypeface(KKMoneyUtil.GetTypeface());
        monthExpense = (RiseNumberTextView)mDrawer.findViewById(R.id.month_expense);
        monthExpense.setTypeface(KKMoneyUtil.typefaceLatoLight);

        if (SettingManager.getInstance().getIsMonthLimit()) {
            monthExpenseTip.setVisibility(View.VISIBLE);
            monthExpense.setText("0");
        } else {
            monthExpenseTip.setVisibility(View.INVISIBLE);
            monthExpense.setVisibility(View.INVISIBLE);
        }

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

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, 0, 0) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                monthExpense.setText("0");
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                monthExpense.withNumber(
                        RecordManager.getCurrentMonthExpense()).setDuration(500).start();
            }
        };
        mDrawer.setDrawerListener(mDrawerToggle);


        View logo = findViewById(R.id.logo_white);
        if (logo != null) {
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.notifyHeaderChanged();
                }
            });
        }

        todayModeAdapter = new TodayViewFragmentAdapter(getSupportFragmentManager());
        mViewPager.getViewPager().setOffscreenPageLimit(todayModeAdapter.getCount());
        mViewPager.getViewPager().setAdapter(todayModeAdapter);
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                return HeaderDesign.fromColorAndDrawable(
                        KKMoneyUtil.GetTagColor(page - 2),
                        KKMoneyUtil.GetTagDrawable(-3)
                );
            }
        });

        setListeners();

        profileImage= (CircleImageView)mDrawer.findViewById(R.id.profile_image);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SettingManager.getInstance().getLoggenOn()) {
                    KKMoneyUtil.showToast(mContext, R.string.change_logo_tip);
                } else {
                    KKMoneyUtil.showToast(mContext, R.string.login_tip);
                }
            }
        });

        mDemoSlider = (SliderLayout)findViewById(R.id.slider);

        HashMap<String, Integer> urls = KKMoneyUtil.GetDrawerTopUrl();

        for(String name : urls.keySet()){
            CustomSliderView customSliderView = new CustomSliderView(this);
            // initialize a SliderLayout
            customSliderView
                    .image(urls.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);
            mDemoSlider.addSlider(customSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.ZoomOut);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));

        loadLogo();

    }

    @Override
    protected void onStop() {
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MaterialViewPagerHelper.unregister(this);
    }

    private void loadRangeMode() {

        Log.d("Saver", "RANGE_MODE");

        Intent intent = new Intent(mContext, AccountBookCustomViewActivity.class);
        startActivity(intent);

    }

    private void loadTagMode() {

        Log.d("Saver", "TAG_MODE");

        Intent intent = new Intent(mContext, AccountBookTagViewActivity.class);
        startActivity(intent);

    }

    private void loadMonthMode() {

        Log.d("Saver", "MONTH_MODE");

        Intent intent = new Intent(mContext, AccountBookMonthViewActivity.class);
        startActivity(intent);

    }

    private void loadListMode() {

        Log.d("Saver", "LIST_MODE");

        Intent intent = new Intent(mContext, AccountBookListViewActivity.class);
        startActivity(intent);

    }

    private int syncSuccessNumber = 0;
    private int syncFailedNumber = 0;
    private int cloudRecordNumber = 0;
    private String cloudOldDatabaseUrl = null;
    private String cloudOldDatabaseFileName = null;
    private String uploadObjectId = null;
    MaterialDialog syncQueryDialog;
    MaterialDialog syncChooseDialog;
    MaterialDialog syncProgressDialog;
    private void sync() {
        if (!SettingManager.getInstance().getLoggenOn()) {
            KKMoneyUtil.showToast(mContext, R.string.login_tip);
        } else {
            syncSuccessNumber = 0;
            syncFailedNumber = 0;
            syncQueryDialog = new MaterialDialog.Builder(this)
                    .title(R.string.sync_querying_title)
                    .content(R.string.sync_querying_content)
                    .negativeText(R.string.cancel)
                    .progress(true, 0)
                    .onAny(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (which == DialogAction.NEGATIVE) {

                            }
                        }
                    })
                    .show();
            final User user = BmobUser
                    .getCurrentUser(KKMoneyApplication.getAppContext(), User.class);
            final MyQuery myQuery = new MyQuery();
            myQuery.setTask(++TaskManager.QUERY_UPDATE_TASK);
            myQuery.query = new BmobQuery<>();
            myQuery.query.addWhereEqualTo("userId", user.getObjectId());
            myQuery.query.setLimit(1);
            myQuery.query.findObjects(KKMoneyApplication.getAppContext(), new FindListener<UploadInfo>() {
                @Override
                public void onSuccess(List<UploadInfo> object) {
                    if (myQuery.getTask() != TaskManager.QUERY_UPDATE_TASK) return;
                    else {
                        syncQueryDialog.dismiss();
                        cloudRecordNumber = 0;
                        Calendar cal = null;
                        if (object.size() == 0) {

                        } else {
                            cloudRecordNumber = object.get(0).getRecordNumber();
                            cloudOldDatabaseUrl = object.get(0).getDatabaseUrl();
                            cloudOldDatabaseFileName = object.get(0).getFileName();
                            Log.e("down","empty"+cloudOldDatabaseFileName);
                            uploadObjectId = object.get(0).getObjectId();
                            cal = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                cal.setTime(sdf.parse(object.get(0).getUpdatedAt()));
                            } catch (ParseException p) {

                            }
                        }
                        String content
                                = KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.sync_info_cloud_record_0)
                                + cloudRecordNumber
                                + KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.sync_info_cloud_record_1)
                                + (cal == null ? KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.sync_info_cloud_time_2) : KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.sync_info_cloud_time_0) + KKMoneyUtil.GetCalendarString(KKMoneyApplication.getAppContext(), cal) + KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.sync_info_cloud_time_1))
                                + KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.sync_info_mobile_record_0)
                                + RecordManager.getInstance(KKMoneyApplication.getAppContext()).RECORDS.size()
                                + KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.sync_info_mobile_record_1)
                                + (SettingManager.getInstance().getRecentlySyncTime() == null ? KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.sync_info_mobile_time_2) : KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.sync_info_mobile_time_0) + KKMoneyUtil.GetCalendarString(KKMoneyApplication.getAppContext(), SettingManager.getInstance().getRecentlySyncTime()) + KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.sync_info_mobile_time_1))
                                + KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.sync_choose_content);
                        syncChooseDialog = new MaterialDialog.Builder(mContext)
                                .title(R.string.sync_choose_title)
                                .content(content)
                                .positiveText(R.string.sync_to_cloud)
                                .negativeText(R.string.sync_to_mobile)
                                .neutralText(R.string.cancel)
                                .onAny(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        syncChooseDialog.dismiss();
                                        if (which == DialogAction.POSITIVE) {
                                            // sync to cloud
                                            String subContent = "";
                                            if (RecordManager.getInstance(KKMoneyApplication.getAppContext()).RECORDS.size() == 0) {
                                                subContent = KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.mobile_record_empty);
                                                new MaterialDialog.Builder(mContext)
                                                        .title(R.string.sync)
                                                        .content(subContent)
                                                        .positiveText(R.string.ok_1)
                                                        .show();
                                                return;
                                            } else {
                                                subContent
                                                        = KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.sure_to_cloud_0)
                                                        + RecordManager.getInstance(KKMoneyApplication.getAppContext()).RECORDS.size()
                                                        + KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.sure_to_cloud_1);
                                            }
                                            new MaterialDialog.Builder(mContext)
                                                    .title(R.string.sync)
                                                    .content(subContent)
                                                    .positiveText(R.string.ok_1)
                                                    .negativeText(R.string.cancel)
                                                    .onAny(new MaterialDialog.SingleButtonCallback() {
                                                        @Override
                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                            if (which == DialogAction.POSITIVE) {
                                                                syncProgressDialog = new MaterialDialog.Builder(mContext)
                                                                        .title(R.string.syncing)
                                                                        .content(KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.uploading_0) + "1" + KKMoneyUtil.GetString(mContext, R.string.uploading_1))
                                                                        .progress(false, RecordManager.getInstance(KKMoneyApplication.getAppContext()).RECORDS.size(), true)
                                                                        .cancelable(false)
                                                                        .show();
                                                                final String databasePath = KKMoneyUtil.GetRecordDatabasePath(KKMoneyApplication.getAppContext());

                                                                final BmobFile bmobFile = new BmobFile(new File(databasePath));
                                                                bmobFile.uploadblock(KKMoneyApplication.getAppContext(), new UploadFileListener() {
                                                                    @Override
                                                                    public void onSuccess() {
                                                                        // the new database is uploaded successfully
                                                                        // delete the old database(if there is)
                                                                        if (cloudOldDatabaseUrl != null) {
                                                                            deleteOldDatabaseOnCloud(cloudOldDatabaseUrl);
                                                                        }
                                                                        // update the UploadInfo record for the new url
                                                                        if (uploadObjectId == null) {
                                                                            // first time
                                                                            UploadInfo uploadInfo = new UploadInfo();
                                                                            uploadInfo.setUserId(user.getObjectId());
                                                                            uploadInfo.setFileName(bmobFile.getFilename());
                                                                            uploadInfo.setRecordNumber(RecordManager.getInstance(mContext).RECORDS.size());
                                                                            uploadInfo.setDatabaseUrl(bmobFile.getFileUrl(mContext));
                                                                            uploadInfo.save(mContext, new SaveListener() {
                                                                                @Override
                                                                                public void onSuccess() {
                                                                                    // upload successfully
                                                                                    syncProgressDialog.dismiss();
                                                                                    new MaterialDialog.Builder(mContext)
                                                                                            .title(R.string.sync_completely_title)
                                                                                            .content(RecordManager.getInstance(mContext).RECORDS.size() + KKMoneyUtil.GetString(mContext, R.string.uploading_fail_1))
                                                                                            .positiveText(R.string.ok_1)
                                                                                            .show();
                                                                                }
                                                                                @Override
                                                                                public void onFailure(int code, String arg0) {
                                                                                    // 添加失败
                                                                                    Log.e("save","1"+code+arg0);
                                                                                    uploadFailed(code, arg0);
                                                                                }
                                                                            });
                                                                        } else {
                                                                            UploadInfo uploadInfo = new UploadInfo();
                                                                            uploadInfo.setUserId(user.getObjectId());
                                                                            uploadInfo.setRecordNumber(RecordManager.getInstance(mContext).RECORDS.size());
                                                                            uploadInfo.setDatabaseUrl(bmobFile.getFileUrl(mContext));
                                                                            uploadInfo.update(mContext, uploadObjectId, new UpdateListener() {
                                                                                @Override
                                                                                public void onSuccess() {
                                                                                    // upload successfully
                                                                                    syncProgressDialog.dismiss();
                                                                                    new MaterialDialog.Builder(mContext)
                                                                                            .title(R.string.sync_completely_title)
                                                                                            .content(RecordManager.getInstance(mContext).RECORDS.size() + KKMoneyUtil.GetString(mContext, R.string.uploading_fail_1))
                                                                                            .positiveText(R.string.ok_1)
                                                                                            .show();
                                                                                }
                                                                                @Override
                                                                                public void onFailure(int code, String msg) {
                                                                                    // upload failed
                                                                                   Log.i("bmob","更新失败："+msg);
                                                                                }
                                                                            });
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onFailure(int i, String s) {
                                                                        Log.e("save","2    "+i+s);
// upload failed
                                                                        uploadFailed(i, s);
                                                                    }



                                                                    @Override
                                                                    public void onProgress(Integer value) {

                                                                        syncProgressDialog.setProgress((int)(value * 1.0 / 100 * RecordManager.getInstance(KKMoneyApplication.getAppContext()).RECORDS.size()));

                                                                    }
                                                                });
//                                                                final BmobFile bmobFile = new BmobFile(new File(databasePath));
//                                                                bmobFile.uploadblock(mContext, new UploadFileListener() {
//
//                                                                    @Override
//                                                                    public void onSuccess() {
//                                                                        if (BuildConfig.DEBUG) {
//                                                                            Log.d("KKMoney", "Upload successfully fileName: " + databasePath);
//                                                                            Log.d("KKMoney", "Upload successfully url: " + bmobFile.getFileUrl(mContext));
//                                                                        }
//                                                                        // the new database is uploaded successfully
//                                                                        // delete the old database(if there is)
//                                                                        if (cloudOldDatabaseUrl != null) {
//                                                                            deleteOldDatabaseOnCloud(cloudOldDatabaseUrl);
//                                                                        }
//                                                                        // update the UploadInfo record for the new url
//                                                                        if (uploadObjectId == null) {
//                                                                            // first time
//                                                                            UploadInfo uploadInfo = new UploadInfo();
//                                                                            uploadInfo.setUserId(user.getObjectId());
//                                                                            uploadInfo.setRecordNumber(RecordManager.getInstance(mContext).RECORDS.size());
//                                                                            uploadInfo.setDatabaseUrl(bmobFile.getFileUrl(mContext));
//                                                                            uploadInfo.save(mContext, new SaveListener() {
//                                                                                @Override
//                                                                                public void onSuccess() {
//                                                                                    // upload successfully
//                                                                                    syncProgressDialog.dismiss();
//                                                                                    new MaterialDialog.Builder(mContext)
//                                                                                            .title(R.string.sync_completely_title)
//                                                                                            .content(RecordManager.getInstance(mContext).RECORDS.size() + KKMoneyUtil.GetString(mContext, R.string.uploading_fail_1))
//                                                                                            .positiveText(R.string.ok_1)
//                                                                                            .show();
//                                                                                }
//                                                                                @Override
//                                                                                public void onFailure(int code, String arg0) {
//                                                                                    // 添加失败
//                                                                                }
//                                                                            });
//                                                                        } else {
//                                                                            UploadInfo uploadInfo = new UploadInfo();
//                                                                            uploadInfo.setUserId(user.getObjectId());
//                                                                            uploadInfo.setRecordNumber(RecordManager.getInstance(mContext).RECORDS.size());
//                                                                            uploadInfo.setDatabaseUrl(bmobFile.getFileUrl(mContext));
//                                                                            uploadInfo.update(mContext, uploadObjectId, new UpdateListener() {
//                                                                                @Override
//                                                                                public void onSuccess() {
//                                                                                    // upload successfully
//                                                                                    syncProgressDialog.dismiss();
//                                                                                    new MaterialDialog.Builder(mContext)
//                                                                                            .title(R.string.sync_completely_title)
//                                                                                            .content(RecordManager.getInstance(mContext).RECORDS.size() + KKMoneyUtil.GetString(mContext, R.string.uploading_fail_1))
//                                                                                            .positiveText(R.string.ok_1)
//                                                                                            .show();
//                                                                                }
//                                                                                @Override
//                                                                                public void onFailure(int code, String msg) {
//                                                                                    // upload failed
//                                                                                    Log.i("bmob","更新失败："+msg);
//                                                                                }
//                                                                            });
//                                                                        }
//                                                                    }
//
//                                                                    @Override
//                                                                    public void onProgress(Integer value) {
//                                                                        syncProgressDialog.setProgress(value);
//                                                                    }
//
//                                                                    @Override
//                                                                    public void onFailure(int code, String msg) {
//                                                                        // upload failed
//                                                                        if (BuildConfig.DEBUG) Log.d("KKMoney", "Upload database failed " + code + " " + msg);
//                                                                        syncProgressDialog.dismiss();
//                                                                        new MaterialDialog.Builder(mContext)
//                                                                                .title(R.string.sync_failed)
//                                                                                .content(R.string.uploading_fail_0)
//                                                                                .positiveText(R.string.ok_1)
//                                                                                .show();
//                                                                    }
//                                                                });
                                                                /*BmobProFile.getInstance(KKMoneyApplication.getAppContext()).upload(databasePath, new UploadListener() {
                                                                    @Override
                                                                    public void onSuccess(String fileName, String url, BmobFile file) {
                                                                        KKMoneyUtil.deleteBmobUploadCach(KKMoneyApplication.getAppContext());
                                                                        if (BuildConfig.DEBUG) {
                                                                            Log.d("KKMoney", "Upload successfully fileName: " + fileName);
                                                                            Log.d("KKMoney", "Upload successfully url: " + url);
                                                                        }
                                                                        // the new database is uploaded successfully
                                                                        // delete the old database(if there is)
                                                                        if (cloudOldDatabaseFileName != null) {
                                                                            deleteOldDatabaseOnCloud(cloudOldDatabaseFileName);
                                                                        }
                                                                        // update the UploadInfo record for the new url
                                                                        UploadInfo uploadInfo = new UploadInfo();
                                                                        uploadInfo.setUserId(user.getObjectId());
                                                                        uploadInfo.setRecordNumber(RecordManager.getInstance(KKMoneyApplication.getAppContext()).RECORDS.size());
                                                                        uploadInfo.setDatabaseUrl(file.getFileUrl(KKMoneyApplication.getAppContext()));
                                                                        uploadInfo.setFileName(fileName);
                                                                        if (uploadObjectId == null) {
                                                                            // insert
                                                                            uploadInfo.save(KKMoneyApplication.getAppContext(), new SaveListener() {
                                                                                @Override
                                                                                public void onSuccess() {
                                                                                    // upload successfully
                                                                                    syncProgressDialog.dismiss();
                                                                                    new MaterialDialog.Builder(mContext)
                                                                                            .title(R.string.sync_completely_title)
                                                                                            .content(RecordManager.getInstance(KKMoneyApplication.getAppContext()).RECORDS.size() + KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.uploading_fail_1))
                                                                                            .positiveText(R.string.ok_1)
                                                                                            .cancelable(false)
                                                                                            .show();
                                                                                }
                                                                                @Override
                                                                                public void onFailure(int code, String arg0) {
                                                                                    uploadFailed(code, arg0);
                                                                                }
                                                                            });
                                                                        } else {
                                                                            // update
                                                                            uploadInfo.update(KKMoneyApplication.getAppContext(), uploadObjectId, new UpdateListener() {
                                                                                @Override
                                                                                public void onSuccess() {
                                                                                    // upload successfully
                                                                                    syncProgressDialog.dismiss();
                                                                                    new MaterialDialog.Builder(mContext)
                                                                                            .title(R.string.sync_completely_title)
                                                                                            .content(RecordManager.getInstance(KKMoneyApplication.getAppContext()).RECORDS.size() + KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.uploading_fail_1))
                                                                                            .positiveText(R.string.ok_1)
                                                                                            .cancelable(false)
                                                                                            .show();
                                                                                }
                                                                                @Override
                                                                                public void onFailure(int code, String msg) {
                                                                                    uploadFailed(code, msg);
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                    @Override
                                                                    public void onProgress(int progress) {
                                                                        syncProgressDialog.setProgress((int)(progress * 1.0 / 100 * RecordManager.getInstance(KKMoneyApplication.getAppContext()).RECORDS.size()));
                                                                    }

                                                                    @Override
                                                                    public void onError(int statuscode, String errormsg) {
                                                                        // upload failed
                                                                        uploadFailed(statuscode, errormsg);
                                                                    }
                                                                });*/
                                                            }
                                                        }
                                                    }).show();
                                        } else if (which == DialogAction.NEGATIVE) {
                                            // sync to mobile
                                            String subContent = "";
                                            if (cloudRecordNumber == 0) {
                                                subContent = KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.cloud_record_empty);
                                                new MaterialDialog.Builder(mContext)
                                                        .title(R.string.sync)
                                                        .content(subContent)
                                                        .positiveText(R.string.ok_1)
                                                        .show();
                                                return;
                                            } else {
                                                subContent
                                                        = KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.sure_to_mobile_0)
                                                        + cloudRecordNumber
                                                        + KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.sure_to_mobile_1);
                                            }
                                            new MaterialDialog.Builder(mContext)
                                                    .title(R.string.sync)
                                                    .content(subContent)
                                                    .positiveText(R.string.ok_1)
                                                    .negativeText(R.string.cancel)
                                                    .onAny(new MaterialDialog.SingleButtonCallback() {
                                                        @Override
                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                            if (which == DialogAction.POSITIVE) {
                                                                syncProgressDialog = new MaterialDialog.Builder(mContext)
                                                                        .title(R.string.syncing)
                                                                        .content(KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.downloading_0) + "1" + KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.downloading_1))
                                                                        .progress(false, cloudRecordNumber, true)
                                                                        .cancelable(false)
                                                                        .show();

                                                                BmobFile bmobFile = new BmobFile(cloudOldDatabaseFileName,"",cloudOldDatabaseUrl);
                                                              //  File saveFile = new File(Environment.getExternalStorageDirectory(),bmobFile.getFilename());
                                                                bmobFile.download(KKMoneyApplication.getAppContext(), new DownloadFileListener() {
                                                                    @Override
                                                                    public void onSuccess(String fullPath) {
                                                                        try {
                                                                            Log.d("KKMoney", "Download successfully " + fullPath);
                                                                            syncProgressDialog.setContent(R.string.sync_completely_content);
                                                                            byte[] buffer = new byte[1024];
                                                                            File file = new File(fullPath);
                                                                            InputStream inputStream = new FileInputStream(file);
                                                                            String outFileNameString = KKMoneyUtil.GetRecordDatabasePath(KKMoneyApplication.getAppContext());
                                                                            OutputStream outputStream = new FileOutputStream(outFileNameString);
                                                                            int length;
                                                                            while ((length = inputStream.read(buffer)) > 0) {
                                                                                outputStream.write(buffer, 0, length);
                                                                            }
                                                                            Log.d("KKMoney", "Download successfully copy completely");
                                                                            outputStream.flush();
                                                                            outputStream.close();
                                                                            inputStream.close();
                                                                            file.delete();
                                                                            Log.d("KKMoney", "Download successfully delete completely");
                                                                            // refresh data
                                                                            RecordManager.getInstance(KKMoneyApplication.getAppContext()).RECORDS.clear();
                                                                            RecordManager.getInstance(KKMoneyApplication.getAppContext()).RECORDS = null;
                                                                            RecordManager.getInstance(KKMoneyApplication.getAppContext());
                                                                            todayModeAdapter.notifyDataSetChanged();
                                                                            Log.d("KKMoney", "Download successfully refresh completely");
                                                                            syncProgressDialog.dismiss();
                                                                            new MaterialDialog.Builder(mContext)
                                                                                    .title(R.string.sync_completely_title)
                                                                                    .content(cloudRecordNumber + KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.downloading_fail_1))
                                                                                    .positiveText(R.string.ok_1)
                                                                                    .cancelable(false)
                                                                                    .show();
                                                                        } catch (IOException i) {
                                                                            i.printStackTrace();
                                                                        }

                                                                    }

                                                                    @Override
                                                                    public void onProgress(Integer progress, long total) {
                                                                        syncProgressDialog.setProgress((int) (((float) total / 100) * RecordManager.getInstance(KKMoneyApplication.getAppContext()).RECORDS.size()));

                                                                    }

                                                                    @Override
                                                                    public void onFailure(int i, String s) {
                                                                        Log.e("down"+i,"  "+s);
                                                                        downloadFailed(i, s);
                                                                    }
                                                                });


                                                                // download the database file to mobile
                             /*                                   BmobProFile.getInstance(KKMoneyApplication.getAppContext()).download(cloudOldDatabaseFileName, new DownloadListener() {
                                                                    @Override
                                                                    public void onSuccess(String fullPath) {
                                                                        // download completely
                                                                        // delete the original database in mobile
                                                                        // copy the new database to mobile
                                                                        try {
                                                                            Log.d("KKMoney", "Download successfully " + fullPath);
                                                                            syncProgressDialog.setContent(R.string.sync_completely_content);
                                                                            byte[] buffer = new byte[1024];
                                                                            File file = new File(fullPath);
                                                                            InputStream inputStream = new FileInputStream(file);
                                                                            String outFileNameString = KKMoneyUtil.GetRecordDatabasePath(KKMoneyApplication.getAppContext());
                                                                            OutputStream outputStream = new FileOutputStream(outFileNameString);
                                                                            int length;
                                                                            while ((length = inputStream.read(buffer)) > 0) {
                                                                                outputStream.write(buffer, 0, length);
                                                                            }
                                                                            Log.d("KKMoney", "Download successfully copy completely");
                                                                            outputStream.flush();
                                                                            outputStream.close();
                                                                            inputStream.close();
                                                                            file.delete();
                                                                            Log.d("KKMoney", "Download successfully delete completely");
                                                                            // refresh data
                                                                            RecordManager.getInstance(KKMoneyApplication.getAppContext()).RECORDS.clear();
                                                                            RecordManager.getInstance(KKMoneyApplication.getAppContext()).RECORDS = null;
                                                                            RecordManager.getInstance(KKMoneyApplication.getAppContext());
                                                                            todayModeAdapter.notifyDataSetChanged();
                                                                            Log.d("KKMoney", "Download successfully refresh completely");
                                                                            syncProgressDialog.dismiss();
                                                                            new MaterialDialog.Builder(mContext)
                                                                                    .title(R.string.sync_completely_title)
                                                                                    .content(cloudRecordNumber + KKMoneyUtil.GetString(KKMoneyApplication.getAppContext(), R.string.downloading_fail_1))
                                                                                    .positiveText(R.string.ok_1)
                                                                                    .cancelable(false)
                                                                                    .show();
                                                                        } catch (IOException i) {
                                                                            i.printStackTrace();
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onProgress(String localPath, int percent) {
                                                                        syncProgressDialog.setProgress((int) (((float) percent / 100) * RecordManager.getInstance(KKMoneyApplication.getAppContext()).RECORDS.size()));
                                                                    }

                                                                    @Override
                                                                    public void onError(int statuscode, String errormsg) {
                                                                        downloadFailed(statuscode, errormsg);
                                                                    }
                                                                });*/
                                                            }
                                                        }
                                                    })
                                                    .show();
                                        } else {
                                        }
                                    }
                                })
                                .show();
                    }
                }
                @Override
                public void onError(int code, String msg) {
                    syncQueryDialog.dismiss();
                    if (BuildConfig.DEBUG) Log.d("KKMoney", "Query: " + msg);
                    if (syncQueryDialog != null) syncQueryDialog.dismiss();
                    new MaterialDialog.Builder(mContext)
                            .title(R.string.sync_querying_fail_title)
                            .content(R.string.sync_querying_fail_content)
                            .positiveText(R.string.ok_1)
                            .show();
                }
            });
        }
    }

    private void deleteOldDatabaseOnCloud(final String fileUrl) {
        BmobFile file = new BmobFile();
        file.setUrl(fileUrl);
        file.delete(KKMoneyApplication.getAppContext(), new DeleteListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Log.e("delete","ok");
                if (BuildConfig.DEBUG) Log.d("KKMoney", "Delete old cloud database successfully " + cloudOldDatabaseUrl);
            }

            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
                Log.e("delete","no");
                if (BuildConfig.DEBUG) Log.d("KKMoney", "Delete old cloud database failed " + cloudOldDatabaseUrl);
            }
        });
    }

    private void uploadFailed(int code, String msg) {
        // upload failed
        if (BuildConfig.DEBUG) Log.d("KKMoney", "Upload database failed " + code + " " + msg);
        syncProgressDialog.dismiss();
        new MaterialDialog.Builder(mContext)
                .title(R.string.sync_failed)
                .content(R.string.uploading_fail_0)
                .positiveText(R.string.ok_1)
                .cancelable(false)
                .show();
    }

    private void downloadFailed(int code, String msg) {
        // upload failed
        if (BuildConfig.DEBUG) Log.d("KKMoney", "Download database failed " + code + " " + msg);
        syncProgressDialog.dismiss();
        new MaterialDialog.Builder(mContext)
                .title(R.string.sync_failed)
                .content(R.string.downloading_fail_0)
                .positiveText(R.string.ok_1)
                .cancelable(false)
                .show();
    }

    private SaveListener uploadCounter = new SaveListener() {
        @Override
        public void onSuccess() {
            syncSuccessNumber++;
            syncProgressDialog.incrementProgress(1);
            if (syncSuccessNumber == RecordManager.getInstance(mContext).RECORDS.size()) {
                syncProgressDialog.setContent(R.string.sync_completely_content);
            } else {
                syncProgressDialog.setContent(KKMoneyUtil.GetString(mContext, R.string.uploading_0) + (syncSuccessNumber + 1) + KKMoneyUtil.GetString(mContext, R.string.uploading_1));
            }
            if (syncSuccessNumber + syncFailedNumber == RecordManager.getInstance(mContext).RECORDS.size()) {
                syncProgressDialog.dismiss();
                new MaterialDialog.Builder(mContext)
                        .title(R.string.sync_completely_title)
                        .content(syncSuccessNumber + KKMoneyUtil.GetString(mContext, R.string.uploading_fail_1))
                        .positiveText(R.string.ok_1)
                        .show();
            }
        }
        @Override
        public void onFailure(int code, String arg0) {
            syncFailedNumber++;
            syncProgressDialog.incrementProgress(1);
            if (syncSuccessNumber + syncFailedNumber == RecordManager.getInstance(mContext).RECORDS.size()) {
                syncProgressDialog.dismiss();
                new MaterialDialog.Builder(mContext)
                        .title(R.string.sync_completely_title)
                        .content(syncSuccessNumber + KKMoneyUtil.GetString(mContext, R.string.uploading_fail_1))
                        .positiveText(R.string.ok_1)
                        .show();
            }
        }
    };

    private void loadSettings() {

        Log.d("Saver", "SETTINGS");

        Intent intent = new Intent(mContext, AccountBookSettingActivity.class);
        startActivity(intent);

    }

    @Override
    public void onResume() {

        if (mDemoSlider != null) mDemoSlider.startAutoCycle();

        super.onResume();

        if (SettingManager.getInstance().getTodayViewPieShouldChange()) {
            todayModeAdapter.notifyDataSetChanged();
            SettingManager.getInstance().setTodayViewPieShouldChange(Boolean.FALSE);
        }

        if (SettingManager.getInstance().getTodayViewTitleShouldChange()) {
            title.setText(SettingManager.getInstance().getAccountBookName());
            SettingManager.getInstance().setTodayViewTitleShouldChange(false);
        }

        if (SettingManager.getInstance().getRecordIsUpdated()) {
            todayModeAdapter.notifyDataSetChanged();
            SettingManager.getInstance().setRecordIsUpdated(false);
        }

        if (SettingManager.getInstance().getTodayViewMonthExpenseShouldChange()) {
            if (SettingManager.getInstance().getIsMonthLimit()) {
                monthExpenseTip.setVisibility(View.VISIBLE);
                monthExpense.withNumber(
                        RecordManager.getCurrentMonthExpense()).setDuration(500).start();
            } else {
                monthExpenseTip.setVisibility(View.INVISIBLE);
                monthExpense.setVisibility(View.INVISIBLE);
            }
        }

        if (SettingManager.getInstance().getTodayViewLogoShouldChange()) {
            loadLogo();
            SettingManager.getInstance().setTodayViewLogoShouldChange(false);
        }

        if (SettingManager.getInstance().getTodayViewInfoShouldChange()) {
            setIconEnable(syncIcon, SettingManager.getInstance().getLoggenOn());
            User user = BmobUser.getCurrentUser(KKMoneyApplication.getAppContext(), User.class);
            if (user != null) {
                userName.setText(user.getUsername());
                userEmail.setText(user.getEmail());
                loadLogo();
            } else {
                userName.setText("");
                userEmail.setText("");
                loadLogo();
            }
            SettingManager.getInstance().setTodayViewInfoShouldChange(false);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) ||
                super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    private void setFonts() {
        userName.setTypeface(KKMoneyUtil.typefaceLatoRegular);
        userEmail.setTypeface(KKMoneyUtil.typefaceLatoLight);
        ((TextView)findViewById(R.id.custom_text)).setTypeface(KKMoneyUtil.GetTypeface());
        ((TextView)findViewById(R.id.tag_text)).setTypeface(KKMoneyUtil.GetTypeface());
        ((TextView)findViewById(R.id.month_text)).setTypeface(KKMoneyUtil.GetTypeface());
        ((TextView)findViewById(R.id.list_text)).setTypeface(KKMoneyUtil.GetTypeface());
        ((TextView)findViewById(R.id.report_text)).setTypeface(KKMoneyUtil.GetTypeface());
        ((TextView)findViewById(R.id.sync_text)).setTypeface(KKMoneyUtil.GetTypeface());
        ((TextView)findViewById(R.id.settings_text)).setTypeface(KKMoneyUtil.GetTypeface());
        ((TextView)findViewById(R.id.help_text)).setTypeface(KKMoneyUtil.GetTypeface());
        ((TextView)findViewById(R.id.feedback_text)).setTypeface(KKMoneyUtil.GetTypeface());
        ((TextView)findViewById(R.id.about_text)).setTypeface(KKMoneyUtil.GetTypeface());
    }

    private void setListeners() {
        custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRangeMode();
            }
        });
        tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTagMode();
            }
        });
        months.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMonthMode();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSettings();
            }
        });
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadListMode();
            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, AccountBookReportViewActivity.class));
            }
        });
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sync();
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, HelpActivity.class));
            }
        });
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, FeedbackActivity.class));
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, AboutActivity.class));
            }
        });
    }

    private void loadLogo() {
        User user = BmobUser.getCurrentUser(KKMoneyApplication.getAppContext(), User.class);
        if (user != null) {
            try {
                File logoFile = new File(KKMoneyApplication.getAppContext().getFilesDir() + KKMoneyUtil.LOGO_NAME);
                if (!logoFile.exists()) {
                    // the local logo file is missed
                    // try to get from the server
                    BmobQuery<Logo> bmobQuery = new BmobQuery();
                    Log.d("KKMoney", user.getLogoObjectId());
                    bmobQuery.addWhereEqualTo("objectId", user.getLogoObjectId());
                    bmobQuery.findObjects(KKMoneyApplication.getAppContext()
                            , new FindListener<Logo>() {
                        @Override
                        public void onSuccess(List<Logo> object) {
                            // there has been an old logo in the server/////////////////////////////////////////////////////////
                            if (object.size() == 0) {

                            } else {
                                String url = object.get(0).getFile().getFileUrl(KKMoneyApplication.getAppContext());
                                if (BuildConfig.DEBUG) Log.d("KKMoney", "Logo in server: " + url);
                                Ion.with(KKMoneyApplication.getAppContext()).load(url)
                                        .write(new File(KKMoneyApplication.getAppContext().getFilesDir()
                                                + KKMoneyUtil.LOGO_NAME))
                                        .setCallback(new FutureCallback<File>() {
                                            @Override
                                            public void onCompleted(Exception e, File file) {
                                                profileImage.setImageBitmap(BitmapFactory.decodeFile(
                                                        KKMoneyApplication.getAppContext().getFilesDir()
                                                                + KKMoneyUtil.LOGO_NAME));
                                            }
                                        });
                            }
                        }
                        @Override
                        public void onError(int code, String msg) {
                            // the picture is lost
                            if (BuildConfig.DEBUG) Log.d("KKMoney", "Can't find the old logo in server.");
                        }
                    });
                } else {
                    // the user logo is in the storage
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(logoFile));
                    profileImage.setImageBitmap(b);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            // use the default logo
            profileImage.setImageResource(R.drawable.default_user_logo);
        }
    }

    private void setIconEnable(MaterialIconView icon, boolean enable) {
        if (enable) icon.setColor(mContext.getResources().getColor(R.color.my_blue));
        else icon.setColor(mContext.getResources().getColor(R.color.my_gray));
    }
}
