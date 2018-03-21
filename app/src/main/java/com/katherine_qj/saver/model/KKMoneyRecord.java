package com.katherine_qj.saver.model;

import com.katherine_qj.saver.util.KKMoneyUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.BmobObject;

/**
 * Created by katherineqj on 2017/10/20.
 */

public class KKMoneyRecord extends BmobObject {

    private Long id;
    private Float money;
    private String currency;
    private Integer tag;
    private Calendar calendar;
    private String remark = "";
    private String userId;
    private String localObjectId;
    private Boolean isUploaded = false;

    public String toString() {
        return "KKMoneyRecord(" +
                "id = " + id + ", " +
                "money = " + money + ", " +
                "currency = " + currency + ", " +
                "tag = " + tag + ", " +
                "calendar = " +
                new SimpleDateFormat("yyyy-MM-dd HH:mm").format(calendar.getTime()) + ", " +
                "remark = " + remark + ", " +
                "userId = " + userId + ", " +
                "localObjectId = " + localObjectId + ", " +
                "isUploaded = " + isUploaded + ")";
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public KKMoneyRecord() {
        this.id = Long.valueOf(-1);
    }

    public KKMoneyRecord(long id, float money, String currency, int tag, Calendar calendar) {
        this.id = id;
        this.money = money;
        this.currency = currency;
        this.tag = tag;
        this.calendar = calendar;
    }

    public KKMoneyRecord(long id, float money, String currency, int tag, Calendar calendar, String remark) {
        this.id = id;
        this.money = money;
        this.currency = currency;
        this.tag = tag;
        this.calendar = calendar;
        this.remark = remark;
    }

    public void set(KKMoneyRecord KKMoneyRecord) {
        this.id = KKMoneyRecord.id;
        this.money = KKMoneyRecord.money;
        this.currency = KKMoneyRecord.currency;
        this.tag = KKMoneyRecord.tag;
        this.calendar = KKMoneyRecord.calendar;
        this.remark = KKMoneyRecord.remark;
        this.userId = KKMoneyRecord.userId;
        this.localObjectId = KKMoneyRecord.localObjectId;
        this.isUploaded = KKMoneyRecord.isUploaded;
    }

    public boolean isInTime(Calendar c1, Calendar c2) {
        return (!this.calendar.before(c1)) && this.calendar.before(c2);
    }

    public boolean isInMoney(double money1, double money2, String currency) {
        return KKMoneyUtil.ToDollas(money1, currency) <= KKMoneyUtil.ToDollas(this.money, this.currency)
                && KKMoneyUtil.ToDollas(money2, currency) > KKMoneyUtil.ToDollas(this.money, this.currency);
    }

    public String getCalendarString() {
        return String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":"
                + String.format("%02d", calendar.get(Calendar.MINUTE)) + " "
                + KKMoneyUtil.GetMonthShort(calendar.get(Calendar.MONTH) + 1) + " "
                + calendar.get(Calendar.DAY_OF_MONTH) + " "
                + calendar.get(Calendar.YEAR);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(String calendarString) {
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date =sdf.parse(calendarString);
            this.calendar = Calendar.getInstance();
            this.calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLocalObjectId() {
        return localObjectId;
    }

    public void setLocalObjectId(String objectId) {
        this.localObjectId = objectId;
    }

    public Boolean getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(Boolean isUploaded) {
        this.isUploaded = isUploaded;
    }
}


