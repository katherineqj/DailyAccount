package com.katherine_qj.saver.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.katherine_qj.saver.BuildConfig;
import com.katherine_qj.saver.model.KKMoneyRecord;
import com.katherine_qj.saver.model.RecordManager;
import com.katherine_qj.saver.model.Tag;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

/**
 * Created by katherineqj on 2017/10/20.
 */

public class DB {

    public static final String DB_NAME_STRING = "KKMoney Database.db";
    public static final String RECORD_DB_NAME_STRING = "Record";
    public static final String TAG_DB_NAME_STRING = "Tag";

    public static final int VERSION = 1;

    private static DB db;
    private SQLiteDatabase sqliteDatabase;
    private DBHelper dbHelper;

    private DB(Context context) throws IOException {
        dbHelper = new DBHelper(context, DB_NAME_STRING, null, VERSION);
        sqliteDatabase = dbHelper.getWritableDatabase();
    }

    public synchronized static DB getInstance(Context context)
            throws IOException {
        if (db == null) db = new DB(context);
        return db;
    }

    public void getData() {
        RecordManager.RECORDS = new LinkedList<>();
        RecordManager.TAGS = new LinkedList<>();

        Cursor cursor = sqliteDatabase
                .query(TAG_DB_NAME_STRING, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Tag tag = new Tag();
                tag.setId(cursor.getInt(cursor.getColumnIndex("ID")) - 1);
                tag.setName(cursor.getString(cursor.getColumnIndex("NAME")));
                tag.setWeight(cursor.getInt(cursor.getColumnIndex("WEIGHT")));
                RecordManager.TAGS.add(tag);
            } while (cursor.moveToNext());
            if (cursor != null) cursor.close();
        }

        cursor = sqliteDatabase
                .query(RECORD_DB_NAME_STRING, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                KKMoneyRecord KKMoneyRecord = new KKMoneyRecord();
                KKMoneyRecord.setId(cursor.getLong(cursor.getColumnIndex("ID")));
                KKMoneyRecord.setMoney(cursor.getFloat(cursor.getColumnIndex("MONEY")));
                KKMoneyRecord.setCurrency(cursor.getString(cursor.getColumnIndex("CURRENCY")));
                KKMoneyRecord.setTag(cursor.getInt(cursor.getColumnIndex("TAG")));
                KKMoneyRecord.setCalendar(cursor.getString(cursor.getColumnIndex("TIME")));
                KKMoneyRecord.setRemark(cursor.getString(cursor.getColumnIndex("REMARK")));
                KKMoneyRecord.setUserId(cursor.getString(cursor.getColumnIndex("USER_ID")));
                KKMoneyRecord.setLocalObjectId(cursor.getString(cursor.getColumnIndex("OBJECT_ID")));
                KKMoneyRecord.setIsUploaded(
                        cursor.getInt(cursor.getColumnIndex("IS_UPLOADED")) == 0 ? false : true);

                if (BuildConfig.DEBUG) Log.d("KKMoney Debugger", "Load " + KKMoneyRecord.toString() + " S");

                RecordManager.RECORDS.add(KKMoneyRecord);
                RecordManager.SUM += (int) KKMoneyRecord.getMoney();
            } while (cursor.moveToNext());
            if (cursor != null) cursor.close();
        }
    }

    // return the row ID of the newly inserted row, or -1 if an error occurred，将本条账单插入数据库
    public long saveRecord(KKMoneyRecord KKMoneyRecord) {
        ContentValues values = new ContentValues();
        values.put("MONEY", KKMoneyRecord.getMoney());
        values.put("CURRENCY", KKMoneyRecord.getCurrency());
        values.put("TAG", KKMoneyRecord.getTag());
        values.put("TIME", new SimpleDateFormat("yyyy-MM-dd HH:mm")
                .format(KKMoneyRecord.getCalendar().getTime()));
        values.put("REMARK", KKMoneyRecord.getRemark());
        values.put("USER_ID", KKMoneyRecord.getUserId());
        values.put("OBJECT_ID", KKMoneyRecord.getLocalObjectId());
        values.put("IS_UPLOADED", KKMoneyRecord.getIsUploaded().equals(Boolean.FALSE) ? 0 : 1);
        long insertId = sqliteDatabase.insert(RECORD_DB_NAME_STRING, null, values);
        KKMoneyRecord.setId(insertId);
        if (BuildConfig.DEBUG)
            Log.d("KKMoney Debugger", "db.saveRecord " + KKMoneyRecord.toString() + " S");
        return insertId;
    }

    // return the row ID of the newly inserted row, or -1 if an error occurred
    public int saveTag(Tag tag) {
        ContentValues values = new ContentValues();
        values.put("NAME", tag.getName());
        values.put("WEIGHT", tag.getWeight());
        int insertId = (int)sqliteDatabase.insert(TAG_DB_NAME_STRING, null, values);
        tag.setId(insertId);
        if (BuildConfig.DEBUG) Log.d("KKMoney Debugger", "db.saveTag " + tag.toString() + " S");
        return insertId - 1;
    }

    // return the id of the record deleted
    public long deleteRecord(long id) {
        long deletedNumber = sqliteDatabase.delete(RECORD_DB_NAME_STRING,
                "ID = ?",
                new String[]{id + ""});
        if (BuildConfig.DEBUG)
            Log.d("KKMoney Debugger", "db.deleteRecord id = " + id + " S");
        if (BuildConfig.DEBUG)
            Log.d("KKMoney Debugger", "db.deleteRecord number = " + deletedNumber + " S");
        return id;
    }

    // return the id of the tag deleted
    public int deleteTag(int id) {
        int deletedNumber = sqliteDatabase.delete(TAG_DB_NAME_STRING,
                "ID = ?",
                new String[]{(id + 1) + ""});
        if (BuildConfig.DEBUG)
            Log.d("KKMoney Debugger", "db.deleteTag id = " + id + " S");
        if (BuildConfig.DEBUG)
            Log.d("KKMoney Debugger", "db.deleteTag number = " + deletedNumber + " S");
        return id;
    }

    // return the id of the KKMoneyRecord update
    public long updateRecord(KKMoneyRecord KKMoneyRecord) {
        ContentValues values = new ContentValues();
        values.put("ID", KKMoneyRecord.getId());
        values.put("MONEY", KKMoneyRecord.getMoney());
        values.put("CURRENCY", KKMoneyRecord.getCurrency());
        values.put("TAG", KKMoneyRecord.getTag());
        values.put("TIME", new SimpleDateFormat("yyyy-MM-dd HH:mm")
                .format(KKMoneyRecord.getCalendar().getTime()));
        values.put("REMARK", KKMoneyRecord.getRemark());
        values.put("USER_ID", KKMoneyRecord.getUserId());
        values.put("OBJECT_ID", KKMoneyRecord.getLocalObjectId());
        values.put("IS_UPLOADED", KKMoneyRecord.getIsUploaded().equals(Boolean.FALSE) ? 0 : 1);
        sqliteDatabase.update(RECORD_DB_NAME_STRING, values,
                "ID = ?",
                new String[]{KKMoneyRecord.getId() + ""});
        if (BuildConfig.DEBUG)
            Log.d("KKMoney Debugger", "db.updateRecord " + KKMoneyRecord.toString() + " S");
        return KKMoneyRecord.getId();
    }

    // return the id of the tag update
    public int updateTag(Tag tag) {
        ContentValues values = new ContentValues();
        values.put("NAME", tag.getName());
        values.put("WEIGHT", tag.getWeight());
        sqliteDatabase.update(TAG_DB_NAME_STRING, values,
                "ID = ?",
                new String[]{(tag.getId() + 1) + ""});
        if (BuildConfig.DEBUG)
            Log.d("KKMoney Debugger", "db.updateTag " + tag.toString() + " S");
        return tag.getId();
    }

    // delete all the records
    public int deleteAllRecords() {
        int deleteNum = sqliteDatabase.delete(RECORD_DB_NAME_STRING, null, null);
        Log.d("KKMoney Debugger", "db.deleteAllRecords " + deleteNum + " S");
        return deleteNum;
    }
}
