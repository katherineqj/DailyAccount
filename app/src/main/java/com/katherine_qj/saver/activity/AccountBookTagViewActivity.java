package com.katherine_qj.saver.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.katherine_qj.saver.BuildConfig;
import com.katherine_qj.saver.R;
import com.katherine_qj.saver.adapter.DrawerTagChooseGridViewAdapter;
import com.katherine_qj.saver.adapter.TagViewFragmentAdapter;
import com.katherine_qj.saver.model.Logo;
import com.katherine_qj.saver.model.RecordManager;
import com.katherine_qj.saver.model.SettingManager;
import com.katherine_qj.saver.model.User;
import com.katherine_qj.saver.ui.CustomSliderView;
import com.katherine_qj.saver.ui.MyGridView;
import com.katherine_qj.saver.util.KKMoneyUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountBookTagViewActivity extends AppCompatActivity {

    private MaterialViewPager mViewPager;

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;

    private TagViewFragmentAdapter tagModeAdapter = null;

    private Context mContext;

    private MyGridView myGridView;
    private DrawerTagChooseGridViewAdapter drawerTagChooseAdapter;

    private TextView userName;
    private TextView userEmail;

    private CircleImageView profileImage;

    private SliderLayout mDemoSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        setContentView(R.layout.activity_account_book_tag_view);

        mViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);

        View view = mViewPager.getRootView();
        TextView title = (TextView)view.findViewById(R.id.logo_white);
        title.setTypeface(KKMoneyUtil.typefaceLatoLight);
        title.setText(SettingManager.getInstance().getAccountBookName());

        mViewPager.getPagerTitleStrip().setTypeface(KKMoneyUtil.typefaceLatoLight, Typeface.NORMAL);

        setTitle("");

        toolbar = mViewPager.getToolbar();
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        userName = (TextView)findViewById(R.id.user_name);
        userEmail = (TextView)findViewById(R.id.user_email);
        userName.setTypeface(KKMoneyUtil.typefaceLatoRegular);
        userEmail.setTypeface(KKMoneyUtil.typefaceLatoLight);

        User user = BmobUser.getCurrentUser(KKMoneyApplication.getAppContext(), User.class);
        if (user != null) {
            userName.setText(user.getUsername());
            userEmail.setText(user.getEmail());
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

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, 0, 0);
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

        tagModeAdapter = new TagViewFragmentAdapter(getSupportFragmentManager());
        mViewPager.getViewPager().setOffscreenPageLimit(tagModeAdapter.getCount());
        mViewPager.getViewPager().setAdapter(tagModeAdapter);
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

        mViewPager.clearAnimation();
        if (SettingManager.getInstance().getShowPicture()) {
            mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
                @Override
                public HeaderDesign getHeaderDesign(int page) {
                    return HeaderDesign.fromColorAndUrl(
                            KKMoneyUtil.GetTagColor(RecordManager.TAGS.get(page).getId()),
                            KKMoneyUtil.GetTagUrl(RecordManager.TAGS.get(page).getId()));
                }
            });
        } else {
            mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
                @Override
                public HeaderDesign getHeaderDesign(int page) {
                    return HeaderDesign.fromColorAndDrawable(
                            KKMoneyUtil.GetTagColor(RecordManager.TAGS.get(page).getId()),
                            KKMoneyUtil.GetTagDrawable(-3));
                }
            });
        }

        myGridView = (MyGridView)mDrawer.findViewById(R.id.grid_view);
        drawerTagChooseAdapter = new DrawerTagChooseGridViewAdapter(mContext);
        myGridView.setAdapter(drawerTagChooseAdapter);

        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                YoYo.with(Techniques.Bounce).delay(0).duration(700).playOn(view);
                mViewPager.getViewPager().setCurrentItem(position);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDrawer.closeDrawers();
                    }
                }, 700);
            }
        });

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
    protected void onResume() {
        super.onResume();
        mDemoSlider.startAutoCycle();
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
    protected void onDestroy() {
        super.onDestroy();

        MaterialViewPagerHelper.unregister(this);
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

    private MaterialDialog progressDialog;
    public class LoadViews extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            if (progressDialog != null) progressDialog.cancel();
        }
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
                    bmobQuery.addWhereEqualTo("objectId", user.getLogoObjectId());
                    bmobQuery.findObjects(KKMoneyApplication.getAppContext()
                            , new FindListener<Logo>() {
                                @Override
                                public void onSuccess(List<Logo> object) {
                                    // there has been an old logo in the server/////////////////////////////////////////////////////////
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

}
