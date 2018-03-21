package com.katherine_qj.saver.util;

import android.graphics.Color;

import com.github.johnpersano.supertoasts.SuperToast;
import com.katherine_qj.saver.activity.KKMoneyApplication;

/**
 * Created by Weiping on 2015/11/30.
 */
public class KKMoneyToast {
    private static KKMoneyToast ourInstance = new KKMoneyToast();

    public static KKMoneyToast getInstance() {
        return ourInstance;
    }

    private KKMoneyToast() {
    }

    public void showToast(int text, int color) {
        SuperToast.cancelAllSuperToasts();
        SuperToast superToast = new SuperToast(KKMoneyApplication.getAppContext());
        superToast.setAnimations(KKMoneyUtil.TOAST_ANIMATION);
        superToast.setDuration(SuperToast.Duration.SHORT);
        superToast.setTextColor(Color.parseColor("#ffffff"));
        superToast.setTextSize(SuperToast.TextSize.SMALL);
        superToast.setText(KKMoneyApplication.getAppContext().getResources().getString(text));
        superToast.setBackground(color);
        superToast.getTextView().setTypeface(KKMoneyUtil.typefaceLatoLight);
        superToast.show();
    }

    public void showToast(String text, int color) {
        SuperToast.cancelAllSuperToasts();
        SuperToast superToast = new SuperToast(KKMoneyApplication.getAppContext());
        superToast.setAnimations(KKMoneyUtil.TOAST_ANIMATION);
        superToast.setDuration(SuperToast.Duration.SHORT);
        superToast.setTextColor(Color.parseColor("#ffffff"));
        superToast.setTextSize(SuperToast.TextSize.SMALL);
        superToast.setText(text);
        superToast.setBackground(color);
        superToast.getTextView().setTypeface(KKMoneyUtil.typefaceLatoLight);
        superToast.show();
    }
}
