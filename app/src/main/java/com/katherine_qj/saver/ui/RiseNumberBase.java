package com.katherine_qj.saver.ui;

/**
 * Created by katherineqj on 2017/11/16.
 */

public interface RiseNumberBase {
    public void start();
    public RiseNumberTextView withNumber(float number);
    public RiseNumberTextView withNumber(int number);
    public RiseNumberTextView setDuration(long duration);
    public void setOnEnd(RiseNumberTextView.EndListener callback);
}
