package com.katherine_qj.saver.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.github.johnpersano.supertoasts.SuperToast;
import com.katherine_qj.saver.BuildConfig;
import com.katherine_qj.saver.R;
import com.katherine_qj.saver.activity.KKMoneyApplication;
import com.katherine_qj.saver.db.DB;
import com.katherine_qj.saver.util.KKMoneyToast;
import com.katherine_qj.saver.util.KKMoneyUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by katherineqj on 2017/10/20.
 */

public class RecordManager {

    private static RecordManager recordManager = null;

    private static DB db;

    // the selected values in list activity
    public static Double SELECTED_SUM;
    public static List<KKMoneyRecord> SELECTED_RECORDS;

    public static Integer SUM;
    public static List<KKMoneyRecord> RECORDS;
    public static List<Tag> TAGS;
    public static Map<Integer, String> TAG_NAMES;

    public static boolean RANDOM_DATA = false;
    private final int RANDOM_DATA_NUMBER_ON_EACH_DAY = 3;
    private final int RANDOM_DATA_EXPENSE_ON_EACH_DAY = 30;

    private static boolean FIRST_TIME = true;

    public static int SAVE_TAG_ERROR_DATABASE_ERROR = -1;
    public static int SAVE_TAG_ERROR_DUPLICATED_NAME = -2;

    public static int DELETE_TAG_ERROR_DATABASE_ERROR = -1;
    public static int DELETE_TAG_ERROR_TAG_REFERENCE = -2;

// constructor//////////////////////////////////////////////////////////////////////////////////////
    private RecordManager(Context context) {
        try {
            db = db.getInstance(context);
            if (BuildConfig.DEBUG) if (BuildConfig.DEBUG) Log.d("KKMoney", "db.getInstance(context) S");
        } catch(IOException e) {
            e.printStackTrace();
        }
        if (FIRST_TIME) {
// if the app starts firstly, create tags///////////////////////////////////////////////////////////
            SharedPreferences preferences =
                    context.getSharedPreferences("Values", Context.MODE_PRIVATE);
            if (preferences.getBoolean("FIRST_TIME", true)) {
                createTags();
                SharedPreferences.Editor editor =
                        context.getSharedPreferences("Values", Context.MODE_PRIVATE).edit();
                editor.putBoolean("FIRST_TIME", false);
                editor.commit();
            }
        }
        if (RANDOM_DATA) {

            SharedPreferences preferences =
                    context.getSharedPreferences("Values", Context.MODE_PRIVATE);
            if (preferences.getBoolean("RANDOM", false)) {
                return;
            }

            randomDataCreater();

            SharedPreferences.Editor editor =
                    context.getSharedPreferences("Values", Context.MODE_PRIVATE).edit();
            editor.putBoolean("RANDOM", true);
            editor.commit();

        }
    }

// getInstance//////////////////////////////////////////////////////////////////////////////////////
    public synchronized static RecordManager getInstance(Context context) {
        if (RECORDS == null || TAGS == null || TAG_NAMES == null || SUM == null || recordManager == null) {
            SUM = 0;
            RECORDS = new LinkedList<>();
            TAGS = new LinkedList<>();
            TAG_NAMES = new HashMap<>();
            recordManager = new RecordManager(context);

            db.getData();

            if (BuildConfig.DEBUG) {
                if (BuildConfig.DEBUG) Log.d("KKMoney", "Load " + RECORDS.size() + " records S");
                if (BuildConfig.DEBUG) Log.d("KKMoney", "Load " + TAGS.size() + " tags S");
            }

            TAGS.add(0, new Tag(-1, "Sum Histogram", -4));
            TAGS.add(0, new Tag(-2, "Sum Pie", -5));

            for (Tag tag : TAGS) TAG_NAMES.put(tag.getId(), tag.getName());

            sortTAGS();
        }
        return recordManager;
    }

// saveRecord///////////////////////////////////////////////////////////////////////////////////////
    public static long saveRecord(final KKMoneyRecord KKMoneyRecord) {
        long insertId = -1;
        KKMoneyRecord.setIsUploaded(false);
        if (BuildConfig.DEBUG)
            if (BuildConfig.DEBUG) Log.d("KKMoney", "recordManager.saveRecord: Save " + KKMoneyRecord.toString() + " S");
        insertId = db.saveRecord(KKMoneyRecord);
        if (insertId == -1) {
            if (BuildConfig.DEBUG)
                if (BuildConfig.DEBUG) Log.d("KKMoney", "recordManager.saveRecord: Save the above KKMoneyRecord FAIL!");
            KKMoneyToast.getInstance()
                    .showToast(R.string.save_failed_locale, SuperToast.Background.RED);
        } else {
            if (BuildConfig.DEBUG)
                if (BuildConfig.DEBUG) Log.d("KKMoney", "recordManager.saveRecord: Save the above KKMoneyRecord SUCCESSFULLY!");
            RECORDS.add(KKMoneyRecord);
            SUM += (int) KKMoneyRecord.getMoney();
            KKMoneyToast.getInstance()
                    .showToast(R.string.save_successfully_locale, SuperToast.Background.BLUE);
        }
        return insertId;
    }

// save tag/////////////////////////////////////////////////////////////////////////////////////////
    public static int saveTag(Tag tag) {
        int insertId = -1;
        if (BuildConfig.DEBUG) {
            if (BuildConfig.DEBUG) Log.d("KKMoney", "recordManager.saveTag: " + tag.toString());
        }
        boolean duplicatedName = false;
        for (Tag t : TAGS) {
            if (t.getName().equals(tag.getName())) {
                duplicatedName = true;
                break;
            }
        }
        if (duplicatedName) {
            return SAVE_TAG_ERROR_DUPLICATED_NAME;
        }
        insertId = db.saveTag(tag);
        if (insertId == -1) {
            if (BuildConfig.DEBUG) {
                if (BuildConfig.DEBUG) Log.d("KKMoney", "Save the above tag FAIL!");
                return SAVE_TAG_ERROR_DATABASE_ERROR;
            }
        } else {
            if (BuildConfig.DEBUG) {
                if (BuildConfig.DEBUG) Log.d("KKMoney", "Save the above tag SUCCESSFULLY!");
            }
            TAGS.add(tag);
            TAG_NAMES.put(tag.getId(), tag.getName());
            sortTAGS();
        }
        return insertId;
    }

// delete a KKMoneyRecord//////////////////////////////////////////////////////////////////////////////////
    public static long deleteRecord(final KKMoneyRecord KKMoneyRecord, boolean deleteInList) {
        long deletedNumber = db.deleteRecord(KKMoneyRecord.getId());
        if (deletedNumber > 0) {
            if (BuildConfig.DEBUG) Log.d("KKMoney",
                    "recordManager.deleteRecord: Delete " + KKMoneyRecord.toString() + " S");
            User user = BmobUser.getCurrentUser(KKMoneyApplication.getAppContext(), User.class);
            // if we can delete the KKMoneyRecord from server
//            if (user != null && KKMoneyRecord.getLocalObjectId() != null) {
//                KKMoneyRecord.delete(KKMoneyApplication.getAppContext(), new DeleteListener() {
//                    @Override
//                    public void onSuccess() {
//                        if (BuildConfig.DEBUG) {
//                            if (BuildConfig.DEBUG) Log.d("KKMoney",
//                                    "recordManager.deleteRecord: Delete online " + KKMoneyRecord.toString() + " S");
//                        }
//                        KKMoneyToast.getInstance()
//                                .showToast(R.string.delete_successfully_online, SuperToast.Background.BLUE);
//                    }
//                    @Override
//                    public void onFailure(int code, String msg) {
//                        if (BuildConfig.DEBUG) {
//                            if (BuildConfig.DEBUG) Log.d("KKMoney",
//                                    "recordManager.deleteRecord: Delete online " + KKMoneyRecord.toString() + " F");
//                        }
//                        KKMoneyToast.getInstance()
//                                .showToast(R.string.delete_failed_online, SuperToast.Background.RED);
//                    }
//                });
//            } else {
//                KKMoneyToast.getInstance()
//                        .showToast(R.string.delete_successfully_locale, SuperToast.Background.BLUE);
//            }
            KKMoneyToast.getInstance()
                    .showToast(R.string.delete_successfully_locale, SuperToast.Background.BLUE);
            // update RECORDS list and SUM
            SUM -= (int) KKMoneyRecord.getMoney();
            if (deleteInList) {
                int size = RECORDS.size();
                for (int i = 0; i < RECORDS.size(); i++) {
                    if (RECORDS.get(i).getId() == KKMoneyRecord.getId()) {
                        RECORDS.remove(i);
                        if (BuildConfig.DEBUG) Log.d("KKMoney",
                                "recordManager.deleteRecord: Delete in RECORD " + KKMoneyRecord.toString() + " S");
                        break;
                    }
                }
            }
        } else {
            if (BuildConfig.DEBUG) Log.d("KKMoney",
                    "recordManager.deleteRecord: Delete " + KKMoneyRecord.toString() + " F");
            KKMoneyToast.getInstance()
                    .showToast(R.string.delete_failed_locale, SuperToast.Background.RED);
        }


        return KKMoneyRecord.getId();
    }

    public static int deleteTag(int id) {
        int deletedId = -1;
        if (BuildConfig.DEBUG) Log.d("KKMoney",
                "Manager: Delete tag: " + "Tag(id = " + id + ", deletedId = " + deletedId + ")");
        boolean tagReference = false;
        for (KKMoneyRecord KKMoneyRecord : RECORDS) {
            if (KKMoneyRecord.getTag() == id) {
                tagReference = true;
                break;
            }
        }
        if (tagReference) {
            return DELETE_TAG_ERROR_TAG_REFERENCE;
        }
        deletedId = db.deleteTag(id);
        if (deletedId == -1) {
            if (BuildConfig.DEBUG) Log.d("KKMoney", "Delete the above tag FAIL!");
            return DELETE_TAG_ERROR_DATABASE_ERROR;
        } else {
            if (BuildConfig.DEBUG) Log.d("KKMoney", "Delete the above tag SUCCESSFULLY!");
            for (Tag tag : TAGS) {
                if (tag.getId() == deletedId) {
                    TAGS.remove(tag);
                    break;
                }
            }
            TAG_NAMES.remove(id);
            sortTAGS();
        }
        return deletedId;
    }

    private static int p;
    public static long updateRecord(final KKMoneyRecord KKMoneyRecord) {
        long updateNumber = db.updateRecord(KKMoneyRecord);
        if (updateNumber <= 0) {
            if (BuildConfig.DEBUG) {
                if (BuildConfig.DEBUG) Log.d("KKMoney", "recordManager.updateRecord " + KKMoneyRecord.toString() + " F");
            }
            KKMoneyToast.getInstance().showToast(R.string.update_failed_locale, SuperToast.Background.RED);
        } else {
            if (BuildConfig.DEBUG) {
                if (BuildConfig.DEBUG) Log.d("KKMoney", "recordManager.updateRecord " + KKMoneyRecord.toString() + " S");
            }
            p = RECORDS.size() - 1;
            for (; p >= 0; p--) {
                if (RECORDS.get(p).getId() == KKMoneyRecord.getId()) {
                    SUM -= (int)RECORDS.get(p).getMoney();
                    SUM += (int) KKMoneyRecord.getMoney();
                    RECORDS.get(p).set(KKMoneyRecord);
                    break;
                }
            }
            KKMoneyRecord.setIsUploaded(false);
//            User user = BmobUser
//                    .getCurrentUser(KKMoneyApplication.getAppContext(), User.class);
//            if (user != null) {
//                // already login
//                if (KKMoneyRecord.getLocalObjectId() != null) {
//                    // this KKMoneyRecord has been push to the server
//                    KKMoneyRecord.setUserId(user.getObjectId());
//                    KKMoneyRecord.update(KKMoneyApplication.getAppContext(),
//                            KKMoneyRecord.getLocalObjectId(), new UpdateListener() {
//                                @Override
//                                public void onSuccess() {
//                                    if (BuildConfig.DEBUG) {
//                                        if (BuildConfig.DEBUG) Log.d("KKMoney", "recordManager.updateRecord update online " + KKMoneyRecord.toString() + " S");
//                                    }
//                                    KKMoneyRecord.setIsUploaded(true);
//                                    RECORDS.get(p).setIsUploaded(true);
//                                    db.updateRecord(KKMoneyRecord);
//                                    KKMoneyToast.getInstance().showToast(R.string.update_successfully_online, SuperToast.Background.BLUE);
//                                }
//
//                                @Override
//                                public void onFailure(int code, String msg) {
//                                    if (BuildConfig.DEBUG) {
//                                        if (BuildConfig.DEBUG) Log.d("KKMoney", "recordManager.updateRecord update online " + KKMoneyRecord.toString() + " F");
//                                    }
//                                    if (BuildConfig.DEBUG) {
//                                        if (BuildConfig.DEBUG) Log.d("KKMoney", "recordManager.updateRecord update online code" + code + " msg " + msg );
//                                    }
//                                    KKMoneyToast.getInstance().showToast(R.string.update_failed_online, SuperToast.Background.RED);
//                                }
//                            });
//                } else {
//                    // this KKMoneyRecord has not been push to the server
//                    KKMoneyRecord.setUserId(user.getObjectId());
//                    KKMoneyRecord.save(KKMoneyApplication.getAppContext(), new SaveListener() {
//                                @Override
//                                public void onSuccess() {
//                                    if (BuildConfig.DEBUG) {
//                                        if (BuildConfig.DEBUG) Log.d("KKMoney", "recordManager.updateRecord save online " + KKMoneyRecord.toString() + " S");
//                                    }
//                                    KKMoneyRecord.setIsUploaded(true);
//                                    KKMoneyRecord.setLocalObjectId(KKMoneyRecord.getObjectId());
//                                    RECORDS.get(p).setIsUploaded(true);
//                                    RECORDS.get(p).setLocalObjectId(KKMoneyRecord.getObjectId());
//                                    db.updateRecord(KKMoneyRecord);
//                                    KKMoneyToast.getInstance().showToast(R.string.update_successfully_online, SuperToast.Background.BLUE);
//                                }
//                                @Override
//                                public void onFailure(int code, String msg) {
//                                    if (BuildConfig.DEBUG) {
//                                        if (BuildConfig.DEBUG) Log.d("KKMoney", "recordManager.updateRecord save online " + KKMoneyRecord.toString() + " F");
//                                    }
//                                    if (BuildConfig.DEBUG) {
//                                        if (BuildConfig.DEBUG) Log.d("KKMoney", "recordManager.updateRecord save online code" + code + " msg " + msg );
//                                    }
//                                    KKMoneyToast.getInstance().showToast(R.string.update_failed_online, SuperToast.Background.RED);
//                                }
//                            });
//                }
//            } else {
//                // has not login
//                db.updateRecord(KKMoneyRecord);
//                KKMoneyToast.getInstance().showToast(R.string.update_successfully_locale, SuperToast.Background.BLUE);
//            }
            db.updateRecord(KKMoneyRecord);
            KKMoneyToast.getInstance().showToast(R.string.update_successfully_locale, SuperToast.Background.BLUE);
        }
        return updateNumber;
    }

// update the records changed to server/////////////////////////////////////////////////////////////
    private static boolean isLastOne = false;
    public static long updateOldRecordsToServer() {
        long counter = 0;
        User user = BmobUser
                .getCurrentUser(KKMoneyApplication.getAppContext(), User.class);
        if (user != null) {
// already login////////////////////////////////////////////////////////////////////////////////////
            isLastOne = false;
            for (int i = 0; i < RECORDS.size(); i++) {
                if (i == RECORDS.size() - 1) isLastOne = true;
                final KKMoneyRecord KKMoneyRecord = RECORDS.get(i);
                if (!KKMoneyRecord.getIsUploaded()) {
// has been changed/////////////////////////////////////////////////////////////////////////////////
                    if (KKMoneyRecord.getLocalObjectId() != null) {
// there is an old KKMoneyRecord in server, we should update this KKMoneyRecord///////////////////////////////////
                        KKMoneyRecord.setUserId(user.getObjectId());
                        KKMoneyRecord.update(KKMoneyApplication.getAppContext(),
                                KKMoneyRecord.getLocalObjectId(), new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        if (BuildConfig.DEBUG) {
                                            if (BuildConfig.DEBUG) Log.d("KKMoney", "recordManager.updateOldRecordsToServer update online " + KKMoneyRecord.toString() + " S");
                                        }
                                        KKMoneyRecord.setIsUploaded(true);
                                        KKMoneyRecord.setLocalObjectId(KKMoneyRecord.getObjectId());
                                        db.updateRecord(KKMoneyRecord);
// after updating, get the old records from server//////////////////////////////////////////////////
                                        if (isLastOne) getRecordsFromServer();
                                    }

                                    @Override
                                    public void onFailure(int code, String msg) {
                                        if (BuildConfig.DEBUG) {
                                            if (BuildConfig.DEBUG) Log.d("KKMoney", "recordManager.updateOldRecordsToServer update online " + KKMoneyRecord.toString() + " F");
                                        }
                                        if (BuildConfig.DEBUG) {
                                            if (BuildConfig.DEBUG) Log.d("KKMoney", "recordManager.updateOldRecordsToServer update online code" + code + " msg " + msg );
                                        }
                                    }
                                });
                    } else {
                        counter++;
                        KKMoneyRecord.setUserId(user.getObjectId());
                        KKMoneyRecord.save(KKMoneyApplication.getAppContext(), new SaveListener() {
                            @Override
                            public void onSuccess() {
                                if (BuildConfig.DEBUG) {
                                    if (BuildConfig.DEBUG) Log.d("KKMoney", "recordManager.updateOldRecordsToServer save online " + KKMoneyRecord.toString() + " S");
                                }
                                KKMoneyRecord.setIsUploaded(true);
                                KKMoneyRecord.setLocalObjectId(KKMoneyRecord.getObjectId());
                                db.updateRecord(KKMoneyRecord);
// after updating, get the old records from server//////////////////////////////////////////////////
                                if (isLastOne) getRecordsFromServer();
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                if (BuildConfig.DEBUG) {
                                    if (BuildConfig.DEBUG) Log.d("KKMoney", "recordManager.updateOldRecordsToServer save online " + KKMoneyRecord.toString() + " F");
                                }
                                if (BuildConfig.DEBUG) {
                                    if (BuildConfig.DEBUG) Log.d("KKMoney", "recordManager.updateOldRecordsToServer save online code" + code + " msg " + msg );
                                }
                            }
                        });
                    }
                }
            }
        } else {

        }

        if (BuildConfig.DEBUG) {
            if (BuildConfig.DEBUG) Log.d("KKMoney", "recordManager.updateOldRecordsToServer update " + counter + " records to server.");
        }

        if (RECORDS.size() == 0) getRecordsFromServer();

        return counter;
    }

    public static long updateTag(Tag tag) {
        int updateId = -1;
        if (BuildConfig.DEBUG) Log.d("KKMoney",
                "Manager: Update tag: " + tag.toString());
        updateId = db.updateTag(tag);
        if (updateId == -1) {
            if (BuildConfig.DEBUG) Log.d("KKMoney", "Update the above tag FAIL!");
        } else {
            if (BuildConfig.DEBUG) Log.d("KKMoney", "Update the above tag SUCCESSFULLY!" + " - " + updateId);
            for (Tag t : TAGS) {
                if (t.getId() == tag.getId()) {
                    t.set(tag);
                    break;
                }
            }
            sortTAGS();
        }
        return updateId;
    }

//get records from server to local//////////////////////////////////////////////////////////////////
    private static long updateNum;
    public static long getRecordsFromServer() {
        updateNum = 0;
        BmobQuery<KKMoneyRecord> query = new BmobQuery<KKMoneyRecord>();
        query.addWhereEqualTo("userId",
                BmobUser.getCurrentUser(KKMoneyApplication.getAppContext(), User.class).getObjectId());
        query.setLimit(Integer.MAX_VALUE);
        query.findObjects(KKMoneyApplication.getAppContext(), new FindListener<KKMoneyRecord>() {
            @Override
            public void onSuccess(List<KKMoneyRecord> object) {
                if (BuildConfig.DEBUG) {
                    if (BuildConfig.DEBUG) Log.d("KKMoney", "recordManager.getRecordsFromServer get " + object.size() + " records from server");
                }
                updateNum = object.size();
                for (KKMoneyRecord KKMoneyRecord : object) {
                    boolean exist = false;
                    for (int i = RECORDS.size() - 1; i >= 0; i--) {
                        if (KKMoneyRecord.getObjectId().equals(RECORDS.get(i).getLocalObjectId())) {
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) {
                        KKMoneyRecord newKKMoneyRecord = new KKMoneyRecord();
                        newKKMoneyRecord.set(KKMoneyRecord);
                        newKKMoneyRecord.setId(-1);
                        RECORDS.add(newKKMoneyRecord);
                    }
                }

                Collections.sort(RECORDS, new Comparator<KKMoneyRecord>() {
                    @Override
                    public int compare(KKMoneyRecord lhs, KKMoneyRecord rhs) {
                        if (lhs.getCalendar().before(rhs.getCalendar())) {
                            return -1;
                        } else if (lhs.getCalendar().after(rhs.getCalendar())) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                });

                db.deleteAllRecords();

                SUM = 0;
                for (int i = 0; i < RECORDS.size(); i++) {
                    RECORDS.get(i).setLocalObjectId(RECORDS.get(i).getObjectId());
                    RECORDS.get(i).setIsUploaded(true);
                    db.saveRecord(RECORDS.get(i));
                    SUM += (int)RECORDS.get(i).getMoney();
                }

                if (BuildConfig.DEBUG) {
                    if (BuildConfig.DEBUG) Log.d("KKMoney", "recordManager.getRecordsFromServer save " + RECORDS.size() + " records");
                }
            }
            @Override
            public void onError(int code, String msg) {
                if (BuildConfig.DEBUG) {
                    if (BuildConfig.DEBUG) Log.d("KKMoney", "recordManager.getRecordsFromServer error " + msg);
                }
            }
        });

        return updateNum;
    }

    public static int getCurrentMonthExpense() {
        Calendar calendar = Calendar.getInstance();
        Calendar left = KKMoneyUtil.GetThisMonthLeftRange(calendar);
        int monthSum = 0;
        for (int i = RECORDS.size() - 1; i >= 0; i--) {
            if (RECORDS.get(i).getCalendar().before(left)) break;
            monthSum += RECORDS.get(i).getMoney();
        }
        return monthSum;
    }

    public static List<KKMoneyRecord> queryRecordByTime(Calendar c1, Calendar c2) {
        List<KKMoneyRecord> list = new LinkedList<>();
        for (KKMoneyRecord KKMoneyRecord : RECORDS) {
            if (KKMoneyRecord.isInTime(c1, c2)) {
                list.add(KKMoneyRecord);
            }
        }
        return list;
    }

    public static List<KKMoneyRecord> queryRecordByCurrency(String currency) {
        List<KKMoneyRecord> list = new LinkedList<>();
        for (KKMoneyRecord KKMoneyRecord : RECORDS) {
            if (KKMoneyRecord.getCurrency().equals(currency)) {
                list.add(KKMoneyRecord);
            }
        }
        return list;
    }

    public static List<KKMoneyRecord> queryRecordByTag(int tag) {
        List<KKMoneyRecord> list = new LinkedList<>();
        for (KKMoneyRecord KKMoneyRecord : RECORDS) {
            if (KKMoneyRecord.getTag() == tag) {
                list.add(KKMoneyRecord);
            }
        }
        return list;
    }

    public static List<KKMoneyRecord> queryRecordByMoney(double money1, double money2, String currency) {
        List<KKMoneyRecord> list = new LinkedList<>();
        for (KKMoneyRecord KKMoneyRecord : RECORDS) {
            if (KKMoneyRecord.isInMoney(money1, money2, currency)) {
                list.add(KKMoneyRecord);
            }
        }
        return list;
    }

    public static List<KKMoneyRecord> queryRecordByRemark(String remark) {
        List<KKMoneyRecord> list = new LinkedList<>();
        for (KKMoneyRecord KKMoneyRecord : RECORDS) {
            if (KKMoneyUtil.IsStringRelation(KKMoneyRecord.getRemark(), remark)) {
                list.add(KKMoneyRecord);
            }
        }
        return list;
    }

    private void createTags() {
        saveTag(new Tag(-1, "Meal",                -1));
        saveTag(new Tag(-1, "Clothing & Footwear", 1));
        saveTag(new Tag(-1, "Home",                2));
        saveTag(new Tag(-1, "Traffic",             3));
        saveTag(new Tag(-1, "Vehicle Maintenance", 4));
        saveTag(new Tag(-1, "Book",                5));
        saveTag(new Tag(-1, "Hobby",               6));
        saveTag(new Tag(-1, "Internet",            7));
        saveTag(new Tag(-1, "Friend",              8));
        saveTag(new Tag(-1, "Education",           9));
        saveTag(new Tag(-1, "Entertainment",      10));
        saveTag(new Tag(-1, "Medical",            11));
        saveTag(new Tag(-1, "Insurance",          12));
        saveTag(new Tag(-1, "Donation",           13));
        saveTag(new Tag(-1, "Sport",              14));
        saveTag(new Tag(-1, "Snack",              15));
        saveTag(new Tag(-1, "Music",              16));
        saveTag(new Tag(-1, "Fund",               17));
        saveTag(new Tag(-1, "Drink",              18));
        saveTag(new Tag(-1, "Fruit",              19));
        saveTag(new Tag(-1, "Film",               20));
        saveTag(new Tag(-1, "Baby",               21));
        saveTag(new Tag(-1, "Partner",            22));
        saveTag(new Tag(-1, "Housing Loan",       23));
        saveTag(new Tag(-1, "Pet",                24));
        saveTag(new Tag(-1, "Telephone Bill",     25));
        saveTag(new Tag(-1, "Travel",             26));
        saveTag(new Tag(-1, "Lunch",              -2));
        saveTag(new Tag(-1, "Breakfast",          -3));
        saveTag(new Tag(-1, "MidnightSnack",      0));
        sortTAGS();
    }

    private void randomDataCreater() {

        Random random = new Random();

        List<KKMoneyRecord> createdKKMoneyRecords = new ArrayList<>();

        Calendar now = Calendar.getInstance();
        Calendar c = Calendar.getInstance();
        c.set(2015, 0, 1, 0, 0, 0);
        c.add(Calendar.SECOND, 1);

        while (c.before(now)) {
            for (int i = 0; i < RANDOM_DATA_NUMBER_ON_EACH_DAY; i++) {
                Calendar r = (Calendar)c.clone();
                int hour = random.nextInt(24);
                int minute = random.nextInt(60);
                int second = random.nextInt(60);

                r.set(Calendar.HOUR_OF_DAY, hour);
                r.set(Calendar.MINUTE, minute);
                r.set(Calendar.SECOND, second);
                r.add(Calendar.SECOND, 0);

                int tag = random.nextInt(TAGS.size());
                int expense = random.nextInt(RANDOM_DATA_EXPENSE_ON_EACH_DAY) + 1;

                KKMoneyRecord KKMoneyRecord = new KKMoneyRecord();
                KKMoneyRecord.setCalendar(r);
                KKMoneyRecord.setMoney(expense);
                KKMoneyRecord.setTag(tag);
                KKMoneyRecord.setCurrency("RMB");
                KKMoneyRecord.setRemark("备注：这里显示备注~");

                createdKKMoneyRecords.add(KKMoneyRecord);
            }
            c.add(Calendar.DATE, 1);
        }

        Collections.sort(createdKKMoneyRecords, new Comparator<KKMoneyRecord>() {
            @Override
            public int compare(KKMoneyRecord lhs, KKMoneyRecord rhs) {
                if (lhs.getCalendar().before(rhs.getCalendar())) {
                    return -1;
                } else if (lhs.getCalendar().after(rhs.getCalendar())) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        for (KKMoneyRecord KKMoneyRecord : createdKKMoneyRecords) {
            saveRecord(KKMoneyRecord);
        }
    }

    // Todo bug here
    private static void sortTAGS() {
        Collections.sort(TAGS, new Comparator<Tag>() {
            @Override
            public int compare(Tag lhs, Tag rhs) {
                if (lhs.getWeight() != rhs.getWeight()) {
                    return Integer.valueOf(lhs.getWeight()).compareTo(rhs.getWeight());
                } else if (!lhs.getName().equals(rhs.getName())) {
                    return lhs.getName().compareTo(rhs.getName());
                } else {
                    return Integer.valueOf(lhs.getId()).compareTo(rhs.getId());
                }
            }
        });
    }

}
