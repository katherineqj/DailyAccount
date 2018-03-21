package com.katherine_qj.saver.activity;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by katherineqj on 2017/11/2.
 */

public class KKMoneyApplication extends Application {

    public static final int VERSION = 120;

    private static Context mContext;

    public static RefWatcher getRefWatcher(Context context) {
        KKMoneyApplication application = (KKMoneyApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;

    @Override public void onCreate() {
        super.onCreate();

        refWatcher = LeakCanary.install(this);
        KKMoneyApplication.mContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return KKMoneyApplication.mContext;
    }

    public static String getAndroidId() {
        return Settings.Secure.getString(
                getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
