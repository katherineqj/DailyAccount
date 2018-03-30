package com.katherine_qj.saver.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.MaterialMenuView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.katherine_qj.saver.R;
import com.katherine_qj.saver.adapter.ButtonGridViewAdapter;
import com.katherine_qj.saver.adapter.EditMoneyRemarkFragmentAdapter;
import com.katherine_qj.saver.adapter.TagChooseFragmentAdapter;
import com.katherine_qj.saver.fragment.KKMoneyFragmentManager;
import com.katherine_qj.saver.fragment.TagChooseFragment;
import com.katherine_qj.saver.model.AppUpdateManager;
import com.katherine_qj.saver.model.KKMoneyRecord;
import com.katherine_qj.saver.model.RecordManager;
import com.katherine_qj.saver.model.SettingManager;
import com.katherine_qj.saver.model.User;
import com.katherine_qj.saver.ui.KKMoneyScrollableViewPager;
import com.katherine_qj.saver.ui.DummyOperation;
import com.katherine_qj.saver.ui.MyGridView;
import com.katherine_qj.saver.ui.guillotine.animation.GuillotineAnimation;
import com.katherine_qj.saver.ui.guillotine.interfaces.GuillotineListener;
import com.katherine_qj.saver.util.KKMoneyToast;
import com.katherine_qj.saver.util.KKMoneyUtil;
import com.rey.material.widget.RadioButton;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobUser;

public class MainActivity extends AppCompatActivity implements TagChooseFragment. OnTagItemSelectedListener {

    private final int SETTING_TAG = 0;

    private Context mContext;

    private View guillotineBackground;

    private TextView toolBarTitle;
    private TextView menuToolBarTitle;

    private TextView passwordTip;

    private SuperToast superToast;
    private SuperActivityToast superActivityToast;

    private MyGridView myGridView;
    private ButtonGridViewAdapter myGridViewAdapter;

    private LinearLayout transparentLy;
    private LinearLayout guillotineColorLy;

    private boolean isPassword = false;

    private long RIPPLE_DURATION = 250;

    private GuillotineAnimation animation;

    private String inputPassword = "";

    private float x1, y1, x2, y2;

    private RadioButton radioButton0;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;

    private MaterialMenuView statusButton;

    private LinearLayout radioButtonLy;

    private View guillotineMenu;

    private ViewPager tagViewPager;
    private KKMoneyScrollableViewPager editViewPager;
    private FragmentPagerAdapter tagAdapter;
    private FragmentPagerAdapter editAdapter;

    private boolean isLoading;

    private DummyOperation dummyOperation;

    private final int NO_TAG_TOAST = 0;
    private final int NO_MONEY_TOAST = 1;
    private final int PASSWORD_WRONG_TOAST = 2;
    private final int PASSWORD_CORRECT_TOAST = 3;
    private final int SAVE_SUCCESSFULLY_TOAST = 4;
    private final int SAVE_FAILED_TOAST = 5;
    private final int PRESS_AGAIN_TO_EXIT = 6;
    private final int WELCOME_BACK = 7;

    boolean doubleBackToExitPressedOnce = false;

    private Toolbar guillotineToolBar;

    private AppUpdateManager appUpdateManager;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.root)
    FrameLayout root;
    @InjectView(R.id.content_hamburger)
    View contentHamburger;

    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mContext = this;
        appUpdateManager = new AppUpdateManager(mContext);
        appUpdateManager.checkUpdateInfo(false);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);//磁力传感器
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//加速度传感器

        sensorManager.registerListener(listener, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(listener, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);

        superToast = new SuperToast(this);
        superActivityToast = new SuperActivityToast(this, SuperToast.Type.PROGRESS_HORIZONTAL);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        Log.d("Saver", "Version number: " + currentapiVersion);

        if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(mContext, R.color.statusBarColor));
        } else{
            // do something for phones running an SDK before lollipop
        }

        User user = BmobUser.getCurrentUser(KKMoneyApplication.getAppContext(), User.class);
        if (user != null) {
            SettingManager.getInstance().setLoggenOn(true);
            SettingManager.getInstance().setUserName(user.getUsername());
            SettingManager.getInstance().setUserEmail(user.getEmail());
            showToast(WELCOME_BACK);
            // 允许用户使用应用
        } else {
            SettingManager.getInstance().setLoggenOn(false);
            //缓存用户对象为空时， 可打开用户注册界面…
        }

        guillotineBackground = findViewById(R.id.guillotine_background);

        toolBarTitle = (TextView)findViewById(R.id.guillotine_title);
        toolBarTitle.setTypeface(KKMoneyUtil.typefaceLatoLight);
        toolBarTitle.setText(SettingManager.getInstance().getAccountBookName());//在进入activity的时候对他的账本名称进行更改
        // 对Editviewpager进行操作，editviewpager01区别是一个是备注，一个是记录页面
        editViewPager = (KKMoneyScrollableViewPager) findViewById(R.id.edit_pager);
        //进入这个adapter
        editAdapter = new EditMoneyRemarkFragmentAdapter(getSupportFragmentManager(),
                KKMoneyFragmentManager.MAIN_ACTIVITY_FRAGMENT);
        
        editViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled (int position, float positionOffset, int positionOffsetPixels) {
                if (position == 1) {
                    if (KKMoneyFragmentManager.mainActivityEditRemarkFragment != null)
                        KKMoneyFragmentManager.mainActivityEditRemarkFragment.editRequestFocus();
                } else {
                    //将系统的键盘隐藏
                    if (KKMoneyFragmentManager.mainActivityEditMoneyFragment != null)
                        KKMoneyFragmentManager.mainActivityEditMoneyFragment.editRequestFocus();
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        editViewPager.setAdapter(editAdapter);

         // 对标签viewpager进行操作
        tagViewPager = (ViewPager)findViewById(R.id.viewpager);

        if (RecordManager.getInstance(mContext).TAGS.size() % 8 == 0)
            tagAdapter = new TagChooseFragmentAdapter(getSupportFragmentManager(), RecordManager.TAGS.size() / 8);
        else
            tagAdapter = new TagChooseFragmentAdapter(getSupportFragmentManager(), RecordManager.TAGS.size() / 8 + 1);
        tagViewPager.setAdapter(tagAdapter);

// button grid view/////////////////////////////////////////////////////////////////////////////////
        myGridView = (MyGridView)findViewById(R.id.gridview);
        myGridViewAdapter = new ButtonGridViewAdapter(this);
        myGridView.setAdapter(myGridViewAdapter);

        myGridView.setOnItemClickListener(gridViewClickListener);
        myGridView.setOnItemLongClickListener(gridViewLongClickListener);
//为断头菜单的高度做准备
        myGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        myGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        View lastChild = myGridView.getChildAt(myGridView.getChildCount() - 1);
                        myGridView.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.FILL_PARENT, lastChild.getBottom()));

                        ViewGroup.LayoutParams params = transparentLy.getLayoutParams();
                        params.height = myGridView.getMeasuredHeight();
                    }
                });

        ButterKnife.inject(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        toolbar.hideOverflowMenu();

        guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        root.addView(guillotineMenu);
        //这里控制他的高度
        transparentLy = (LinearLayout)guillotineMenu.findViewById(R.id.transparent_ly);
        guillotineColorLy = (LinearLayout)guillotineMenu.findViewById(R.id.guillotine_color_ly);
        guillotineToolBar = (Toolbar)guillotineMenu.findViewById(R.id.toolbar);

        menuToolBarTitle = (TextView)guillotineMenu.findViewById(R.id.guillotine_title);
        menuToolBarTitle.setTypeface(KKMoneyUtil.typefaceLatoLight);
        menuToolBarTitle.setText(SettingManager.getInstance().getAccountBookName());

        radioButton0 = (RadioButton)guillotineMenu.findViewById(R.id.radio_button_0);
        radioButton1 = (RadioButton)guillotineMenu.findViewById(R.id.radio_button_1);
        radioButton2 = (RadioButton)guillotineMenu.findViewById(R.id.radio_button_2);
        radioButton3 = (RadioButton)guillotineMenu.findViewById(R.id.radio_button_3);

        passwordTip = (TextView)guillotineMenu.findViewById(R.id.password_tip);
        passwordTip.setText(mContext.getResources().getString(R.string.password_tip));
        passwordTip.setTypeface(KKMoneyUtil.typefaceLatoLight);

        radioButtonLy = (LinearLayout)guillotineMenu.findViewById(R.id.radio_button_ly);

        statusButton = (MaterialMenuView)guillotineMenu.findViewById(R.id.status_button);
        statusButton.setState(MaterialMenuDrawable.IconState.ARROW);

        statusButton.setOnClickListener(statusButtonOnClickListener);

        animation = new GuillotineAnimation.GuillotineBuilder(guillotineMenu,
                        guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .setGuillotineListener(new GuillotineListener() {
                    @Override
                    public void onGuillotineOpened() {
                        isPassword = true;//改变这个标志位的值，就可以在键盘点击的时候对不同的应用场景做区别
                    }

                    @Override
                    public void onGuillotineClosed() {
                        isPassword = false;
                        KKMoneyFragmentManager.mainActivityEditMoneyFragment.editRequestFocus();
                        radioButton0.setChecked(false);
                        radioButton1.setChecked(false);
                        radioButton2.setChecked(false);
                        radioButton3.setChecked(false);
                        inputPassword = "";
                        statusButton.setState(MaterialMenuDrawable.IconState.ARROW);
                    }
                })
                .build();
//开启断头台菜单的入口 toolbar
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animation.open();
            }
        });

        if (SettingManager.getInstance().getFirstTime()) {
            Intent intent = new Intent(mContext, ShowActivity.class);
            startActivity(intent);
        }

        if (SettingManager.getInstance().getShowMainActivityGuide()) {
            boolean wrapInScrollView = true;
            new MaterialDialog.Builder(this)
                    .title(R.string.guide)
                    .typeface(KKMoneyUtil.GetTypeface(), KKMoneyUtil.GetTypeface())
                    .customView(R.layout.main_activity_guide, wrapInScrollView)
                    .positiveText(R.string.ok)
                    .show();
            SettingManager.getInstance().setShowMainActivityGuide(false);
        }
    }

    private AdapterView.OnItemLongClickListener gridViewLongClickListener
            = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (!isLoading) {
                //点击键盘部分
                buttonClickOperation(true, position);
            }
            return true;
        }
    };


    private void checkPassword() {
        if (inputPassword.length() != 4) {
            return;
        }//只有在输入密码够四位之后才会进行验证
        if (SettingManager.getInstance().getPassword().equals(inputPassword)) {//判断是否相等
            isLoading = true;
            YoYo.with(Techniques.Bounce).delay(0).duration(1000).playOn(radioButton3);
            statusButton.animateState(MaterialMenuDrawable.IconState.CHECK);
            statusButton.setClickable(false);
            showToast(PASSWORD_CORRECT_TOAST);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //密码验证成功跳到下一个界面，（指原型图中的输入密码完界面）
                    Intent intent = new Intent(mContext, AccountBookTodayViewActivity.class);
                    startActivityForResult(intent, SETTING_TAG);
                    isLoading = false;
                }
            }, 1000);
            final Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    animation.close();
                }
            }, 3000);
        } else {//密码不正确就初始化密码信息和界面
            showToast(PASSWORD_WRONG_TOAST);
            YoYo.with(Techniques.Shake).duration(700).playOn(radioButtonLy);
            radioButton0.setChecked(false);
            radioButton1.setChecked(false);
            radioButton2.setChecked(false);
            radioButton3.setChecked(false);
            inputPassword = "";
            statusButton.animateState(MaterialMenuDrawable.IconState.X);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SETTING_TAG:
                if (resultCode == RESULT_OK) {
                    if (data.getBooleanExtra("IS_CHANGED", false)) {
                        for (int i = 0; i < tagAdapter.getCount() && i < KKMoneyFragmentManager.tagChooseFragments.size(); i++) {
                            if (KKMoneyFragmentManager.tagChooseFragments.get(i) != null)
                                KKMoneyFragmentManager.tagChooseFragments.get(i).updateTags();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private View.OnClickListener statusButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            animation.close();
        }
    };

    private AdapterView.OnItemClickListener gridViewClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!isLoading) {
                buttonClickOperation(false, position);
            }
        }
    };
// 虚拟键盘的点击
    private void buttonClickOperation(boolean longClick, int position) {
        if (editViewPager.getCurrentItem() == 1) return;
        if (!isPassword) {//对钱的处理
            if (KKMoneyFragmentManager.mainActivityEditMoneyFragment.getNumberText().toString().equals("0")
                    && !KKMoneyUtil.ClickButtonCommit(position)) {
                if (KKMoneyUtil.ClickButtonDelete(position)
                        || KKMoneyUtil.ClickButtonIsZero(position)) {
                    //点0或者删除不处理（输入第一个数字的时候）
                } else {
                    //如果现在显示是0 并且点击的按钮不是删除键，那就显示（也就是输入钱数的第一个数字）
                    KKMoneyFragmentManager.mainActivityEditMoneyFragment.setNumberText(KKMoneyUtil.BUTTONS[position]);
                }
            } else {//已经不是第一次输入
                if (KKMoneyUtil.ClickButtonDelete(position)) {//点击删除键
                    if (longClick) {//长按删除键
                        KKMoneyFragmentManager.mainActivityEditMoneyFragment.setNumberText("0");
                        KKMoneyFragmentManager.mainActivityEditMoneyFragment.setHelpText(
                                KKMoneyUtil.FLOATINGLABELS[KKMoneyFragmentManager.mainActivityEditMoneyFragment
                                        .getNumberText().toString().length()]);
                    } else {//点击一下删除键
                        KKMoneyFragmentManager.mainActivityEditMoneyFragment.setNumberText(
                                KKMoneyFragmentManager.mainActivityEditMoneyFragment.getNumberText().toString()
                                .substring(0, KKMoneyFragmentManager.mainActivityEditMoneyFragment
                                        .getNumberText().toString().length() - 1));
                        if (KKMoneyFragmentManager.mainActivityEditMoneyFragment//如果删到最后一个了，就设置0
                                .getNumberText().toString().length() == 0) {
                            KKMoneyFragmentManager.mainActivityEditMoneyFragment.setNumberText("0");
                            KKMoneyFragmentManager.mainActivityEditMoneyFragment.setHelpText(" ");
                        }
                    }
                } else if (KKMoneyUtil.ClickButtonCommit(position)) {
                    commit();//如果点击的是提交按钮
                } else {
                    //把现在有的数字后面加一位数字
                    KKMoneyFragmentManager.mainActivityEditMoneyFragment.setNumberText(
                            KKMoneyFragmentManager.mainActivityEditMoneyFragment.getNumberText().toString()
                                    + KKMoneyUtil.BUTTONS[position]);
                }
            }
            KKMoneyFragmentManager.mainActivityEditMoneyFragment
                    .setHelpText(KKMoneyUtil.FLOATINGLABELS[//根据输入的位置显示单位
                            KKMoneyFragmentManager.mainActivityEditMoneyFragment.getNumberText().toString().length()]);
        } else {//对密码的处理
            if (KKMoneyUtil.ClickButtonDelete(position)) {//点击橡皮擦图标
                if (longClick) {//长按全部删除
                    radioButton0.setChecked(false);
                    radioButton1.setChecked(false);
                    radioButton2.setChecked(false);
                    radioButton3.setChecked(false);
                    inputPassword = "";
                } else {//不是长按就一个一个删除
                    if (inputPassword.length() == 0) {
                        inputPassword = "";
                    } else {//改变radiobutton的显示状态
                        if (inputPassword.length() == 1) {
                            radioButton0.setChecked(false);
                        } else if (inputPassword.length() == 2) {
                            radioButton1.setChecked(false);
                        } else if (inputPassword.length() == 3) {
                            radioButton2.setChecked(false);
                        } else {
                            radioButton3.setChecked(false);
                        }
                        inputPassword = inputPassword.substring(0, inputPassword.length() - 1);
                    }
                }
            } else if (KKMoneyUtil.ClickButtonCommit(position)) {//点击提交图标不做处理
            } else {//如果是其他的数字就去做处理
                if (statusButton.getState() == MaterialMenuDrawable.IconState.X) {
                    statusButton.animateState(MaterialMenuDrawable.IconState.ARROW);
                }
                if (inputPassword.length() == 0) {
                    radioButton0.setChecked(true);
                    YoYo.with(Techniques.Bounce).delay(0).duration(1000).playOn(radioButton0);
                } else if (inputPassword.length() == 1) {
                    radioButton1.setChecked(true);
                    YoYo.with(Techniques.Bounce).delay(0).duration(1000).playOn(radioButton1);
                } else if (inputPassword.length() == 2) {
                    radioButton2.setChecked(true);
                    YoYo.with(Techniques.Bounce).delay(0).duration(1000).playOn(radioButton2);
                } else if (inputPassword.length() == 3) {
                    radioButton3.setChecked(true);
                }
                if (inputPassword.length() < 4) {
                    inputPassword += KKMoneyUtil.BUTTONS[position];//改变密码
                }
            }
            checkPassword();
        }
    }
//提交账单
    private void commit() {
        if (KKMoneyFragmentManager.mainActivityEditMoneyFragment.getTagId() == -1) {
            showToast(NO_TAG_TOAST);
        } else if (KKMoneyFragmentManager.mainActivityEditMoneyFragment.getNumberText().toString().equals("0")) {
            showToast(NO_MONEY_TOAST);
        } else  {
            Calendar calendar = Calendar.getInstance();
            //创建当次账单实例（-1，钱数，货币类型，消费tag，日期）
            KKMoneyRecord KKMoneyRecord = new KKMoneyRecord(
                    -1,
                    Float.valueOf(KKMoneyFragmentManager.mainActivityEditMoneyFragment.getNumberText().toString()),
                    "RMB",
                    KKMoneyFragmentManager.mainActivityEditMoneyFragment.getTagId(), calendar);
            //记录账单备注
            KKMoneyRecord.setRemark(KKMoneyFragmentManager.mainActivityEditRemarkFragment.getRemark());
            long saveId = RecordManager.saveRecord(KKMoneyRecord);//存入数据库 并得到操作结果
            if (saveId == -1) {
                //这里加上操作失败的提示

            } else {
                if (!superToast.isShowing()) {
                    changeColor();
                }
                KKMoneyFragmentManager.mainActivityEditMoneyFragment.setTagImage(R.color.transparent);
                KKMoneyFragmentManager.mainActivityEditMoneyFragment.setTagName("");
            }
            KKMoneyFragmentManager.mainActivityEditMoneyFragment.setNumberText("0");
            KKMoneyFragmentManager.mainActivityEditMoneyFragment.setHelpText(" ");
        }
    }

    private void tagAnimation() {
        YoYo.with(Techniques.Shake).duration(1000).playOn(tagViewPager);
    }

    private void showToast(int toastType) {
        switch (toastType) {
            case NO_TAG_TOAST:
                KKMoneyToast.getInstance().showToast(R.string.toast_no_tag, SuperToast.Background.RED);
                tagAnimation();
                break;
            case NO_MONEY_TOAST:
                KKMoneyToast.getInstance().showToast(R.string.toast_no_money, SuperToast.Background.RED);
                break;
            case PASSWORD_WRONG_TOAST:
                KKMoneyToast.getInstance().showToast(R.string.toast_password_wrong, SuperToast.Background.RED);
                break;
            case PASSWORD_CORRECT_TOAST:
                KKMoneyToast.getInstance().showToast(R.string.toast_password_correct, SuperToast.Background.GRAY);
                break;
            case SAVE_SUCCESSFULLY_TOAST:
                break;
            case SAVE_FAILED_TOAST:
                break;
            case PRESS_AGAIN_TO_EXIT:
                KKMoneyToast.getInstance().showToast(R.string.toast_press_again_to_exit, SuperToast.Background.GRAY);
                break;
            case WELCOME_BACK:
                KKMoneyToast.getInstance().showToast(KKMoneyApplication.getAppContext()
                        .getResources().getString(R.string.welcome_back)
                        + "\n" + SettingManager.getInstance().getUserName(), SuperToast.Background.GRAY);
            default:
                break;
        }
    }

    private void changeColor() {
        boolean shouldChange
                = SettingManager.getInstance().getIsMonthLimit()
                && SettingManager.getInstance().getIsColorRemind()
                && RecordManager.getCurrentMonthExpense()
                >= SettingManager.getInstance().getMonthWarning();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (shouldChange) {
                window.setStatusBarColor(
                        KKMoneyUtil.getInstance().getDeeperColor(SettingManager.getInstance().getRemindColor()));
            } else {
                window.setStatusBarColor(ContextCompat.getColor(mContext, R.color.statusBarColor));
            }

        } else{
            // do something for phones running an SDK before lollipop
        }

        if (shouldChange) {
            root.setBackgroundColor(SettingManager.getInstance().getRemindColor());
            toolbar.setBackgroundColor(SettingManager.getInstance().getRemindColor());
            guillotineBackground.setBackgroundColor(SettingManager.getInstance().getRemindColor());
            guillotineColorLy.setBackgroundColor(SettingManager.getInstance().getRemindColor());
            guillotineToolBar.setBackgroundColor(SettingManager.getInstance().getRemindColor());
        } else {
            root.setBackgroundColor(KKMoneyUtil.getInstance().MY_Normally);
            toolbar.setBackgroundColor(KKMoneyUtil.getInstance().MY_Normally);
            guillotineBackground.setBackgroundColor(KKMoneyUtil.getInstance().MY_Normally);
            guillotineColorLy.setBackgroundColor(KKMoneyUtil.getInstance().MY_Normally);
            guillotineToolBar.setBackgroundColor(KKMoneyUtil.getInstance().MY_Normally);
        }
        if (KKMoneyFragmentManager.mainActivityEditMoneyFragment != null)
            KKMoneyFragmentManager.mainActivityEditMoneyFragment.setEditColor(shouldChange);
        if (KKMoneyFragmentManager.mainActivityEditRemarkFragment != null)
            KKMoneyFragmentManager.mainActivityEditRemarkFragment.setEditColor(shouldChange);
        myGridViewAdapter.notifyDataSetInvalidated();
    }


    //手势控制的输入密码页面的出现和消失

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = ev.getX();
                y1 = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                x2 = ev.getX();
                y2 = ev.getY();
                if (Math.abs(y2 - y1) > Math.abs(x2 - x1)) {
                    if (y2 - y1 > 300) {
                        if (!isPassword) {
                            animation.open();
                        }
                    }
                    if (y1 - y2 > 300) {
                        if (isPassword) {
                            animation.close();
                        }
                    }
                } else {
                    if (editViewPager.getCurrentItem() == 0
                            && KKMoneyUtil.isPointInsideView(x2, y2, editViewPager)
                            && KKMoneyUtil.GetScreenWidth(mContext) - x2 <= 60) {
                        return true;
                    }
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if (isPassword) {
            animation.close();
            return;
        }

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            SuperToast.cancelAllSuperToasts();
            return;
        }

        showToast(PRESS_AGAIN_TO_EXIT);

        doubleBackToExitPressedOnce = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onResume() {
        super.onResume();

        // if the tags' order has been changed
        if (SettingManager.getInstance().getMainActivityTagShouldChange()) {
            // change the tag fragment
            for (int i = 0; i < tagAdapter.getCount() && i < KKMoneyFragmentManager.tagChooseFragments.size(); i++) {
                if (KKMoneyFragmentManager.tagChooseFragments.get(i) != null)
                    KKMoneyFragmentManager.tagChooseFragments.get(i).updateTags();
            }
            // and tell others that main activity has changed
            SettingManager.getInstance().setMainActivityTagShouldChange(false);
        }

        // if the title should be changed
        if (SettingManager.getInstance().getMainViewTitleShouldChange()) {
            menuToolBarTitle.setText(SettingManager.getInstance().getAccountBookName());
            toolBarTitle.setText(SettingManager.getInstance().getAccountBookName());
            SettingManager.getInstance().setMainViewTitleShouldChange(false);
        }

        changeColor();//检查颜色是否需要改变

        radioButton0.setChecked(false);
        radioButton1.setChecked(false);
        radioButton2.setChecked(false);
        radioButton3.setChecked(false);

        isLoading = false;
        inputPassword = "";
        System.gc();
    }

    @Override
    public void onDestroy() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(listener);
        }
        super.onDestroy();
    }
    //点击Tagview的点击事件
    @Override
    public void onTagItemPicked(int position) {
        if (KKMoneyFragmentManager.mainActivityEditMoneyFragment != null)
            KKMoneyFragmentManager.mainActivityEditMoneyFragment.setTag(tagViewPager.getCurrentItem() * 8 + position + 2);
    }

    @Override
    public void onAnimationStart(int id) {
        // Todo add animation for changing tag
    }

    private static final float SHAKE_ACCELERATED_SPEED = 15;
    private SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                if ((Math.abs(event.values[0]) > SHAKE_ACCELERATED_SPEED
                        || Math.abs(event.values[1]) > SHAKE_ACCELERATED_SPEED
                        || Math.abs(event.values[2]) > SHAKE_ACCELERATED_SPEED)) {
                    if (!isPassword) {
                        animation.open();
                    } else {
                        animation.close();
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

}
