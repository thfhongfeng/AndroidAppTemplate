package com.pine.db_server.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.pine.tool.util.DecimalUtils;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.SecurityUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static com.pine.db_server.DbConstants.ACCOUNT_LOGIN_TABLE_NAME;
import static com.pine.db_server.DbConstants.ACCOUNT_TABLE_NAME;
import static com.pine.db_server.DbConstants.APP_VERSION_TABLE_NAME;
import static com.pine.db_server.DbConstants.DATABASE_NAME;
import static com.pine.db_server.DbConstants.DATABASE_VERSION;
import static com.pine.db_server.DbConstants.FILE_INFO_TABLE_NAME;
import static com.pine.db_server.DbConstants.PRODUCT_TABLE_NAME;
import static com.pine.db_server.DbConstants.SHOP_TABLE_NAME;
import static com.pine.db_server.DbConstants.SHOP_TYPE_TABLE_NAME;
import static com.pine.db_server.DbConstants.SWITCHER_CONFIG_TABLE_NAME;
import static com.pine.db_server.DbConstants.TRAVEL_NOTE_COMMENT_TABLE_NAME;
import static com.pine.db_server.DbConstants.TRAVEL_NOTE_SHOP_TABLE_NAME;
import static com.pine.db_server.DbConstants.TRAVEL_NOTE_TABLE_NAME;

public class SQLiteDbHelper extends SQLiteOpenHelper {
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    public SQLiteDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtils.d(TAG, "onCreate");
        createFileInfoTable(db);
        createConfigSwitcherTable(db);
        createAppVersionTable(db);
        createAccountTable(db);
        createAccountLoginTable(db);
        createShopTypeTable(db);
        createShopTable(db);
        createProductTable(db);
        createTravelNoteTable(db);
        createTravelNoteShopRelatedTable(db);
        createTravelNoteCommentTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createFileInfoTable(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + FILE_INFO_TABLE_NAME +
                "(_id integer primary key autoincrement,fileName text not null," +
                "filePath text not null,bizType integer not null,fileType integer not null," +
                "descr text,orderNum integer not null," +
                "createTime datetime,updateTime datetime)");
    }

    private void createConfigSwitcherTable(SQLiteDatabase db) {
        try {
            db.execSQL("create table if not exists " + SWITCHER_CONFIG_TABLE_NAME +
                    "(_id integer primary key autoincrement,configKey text not null," +
                    "open text not null," +
                    "createTime datetime,updateTime datetime)");
            List<ContentValues> list = new ArrayList<>();
            ContentValues contentValues = new ContentValues();
            contentValues.put("configKey", "login_bundle");
            contentValues.put("open", "true");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            list.add(contentValues);
            contentValues = new ContentValues();
            contentValues.put("configKey", "main_bundle");
            contentValues.put("open", "true");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            list.add(contentValues);
            contentValues = new ContentValues();
            contentValues.put("configKey", "user_bundle");
            contentValues.put("open", "true");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            list.add(contentValues);
            contentValues = new ContentValues();
            contentValues.put("configKey", "business_mvc_bundle");
            contentValues.put("open", "true");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            list.add(contentValues);
            contentValues = new ContentValues();
            contentValues.put("configKey", "business_mvp_bundle");
            contentValues.put("open", "true");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            list.add(contentValues);
            contentValues = new ContentValues();
            contentValues.put("configKey", "business_mvvm_bundle");
            contentValues.put("open", "true");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            list.add(contentValues);
            contentValues = new ContentValues();
            contentValues.put("configKey", "business_demo_bundle");
            contentValues.put("open", "true");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            list.add(contentValues);
            boolean insertSuccess = true;
            db.beginTransaction();
            for (ContentValues cv : list) {
                long id = db.insert(SWITCHER_CONFIG_TABLE_NAME, "configKey", cv);
                if (id == -1) {
                    insertSuccess = false;
                }
            }
            db.setTransactionSuccessful();
            if (insertSuccess) {
                LogUtils.d(TAG, "createConfigSwitcherTable success");
            } else {
                LogUtils.d(TAG, "createConfigSwitcherTable fail: insert init data fail");
            }
        } catch (SQLException e) {
            LogUtils.d(TAG, "createConfigSwitcherTable fail: " + e.toString());
        }
        db.endTransaction();
    }

    private void createAppVersionTable(SQLiteDatabase db) {
        try {
            db.execSQL("create table if not exists " + APP_VERSION_TABLE_NAME +
                    "(_id integer primary key autoincrement,packageName text not null," +
                    "versionName text not null,versionCode integer not null," +
                    "minSupportedVersion text,force boolean,fileName text,path text," +
                    "createTime datetime,updateTime datetime)");
            ContentValues contentValues = new ContentValues();
            contentValues.put("packageName", "com.pine.template");
            contentValues.put("versionName", "1.0.2");
            contentValues.put("versionCode", 2);
            contentValues.put("minSupportedVersion", 1);
            contentValues.put("force", false);
            contentValues.put("fileName", "pine_app_template-V1.0.2-release.apk");
            contentValues.put("path", "http://yanyangtian.purang.com/download/bsd_purang.apk");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            long id = db.insert(APP_VERSION_TABLE_NAME, "package", contentValues);
            if (id != -1) {
                LogUtils.d(TAG, "createAppVersionTable success");
            } else {
                LogUtils.d(TAG, "createAppVersionTable fail: insert init data fail");
            }
        } catch (SQLException e) {
            LogUtils.d(TAG, "createAppVersionTable fail: " + e.toString());
        }
    }

    private void createAccountTable(SQLiteDatabase db) {
        try {
            db.execSQL("create table if not exists " + ACCOUNT_TABLE_NAME +
                    "(_id integer primary key autoincrement, id text not null unique," +
                    "account text not null, name text not null," +
                    "password text not null, headImgUrl text,state integer not null," +
                    "mobile text not null,createTime datetime,updateTime datetime)");
            Calendar calendar = Calendar.getInstance();
            List<ContentValues> list = new ArrayList<>();
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", "1000" + "20190328102000000" + "000");
            contentValues.put("account", "admin");
            contentValues.put("name", "admin");
            contentValues.put("password", SecurityUtils.generateMD5("111aaa"));
            contentValues.put("state", 1); // 账户状态:0-删除，1-激活，2-未激活
            contentValues.put("mobile", "18672943565");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
            list.add(contentValues);
            contentValues = new ContentValues();
            contentValues.put("id", "1000" + "20190328102000000" + "001");
            contentValues.put("account", "15221464292");
            contentValues.put("name", "15221464292");
            contentValues.put("password", SecurityUtils.generateMD5("111aaa"));
            contentValues.put("state", 1); // 账户状态:0-删除，1-激活，2-未激活
            contentValues.put("mobile", "15221464292");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
            list.add(contentValues);
            contentValues = new ContentValues();
            contentValues.put("id", "1000" + "20190328102000000" + "002");
            contentValues.put("account", "15221464296");
            contentValues.put("name", "15221464296");
            contentValues.put("password", SecurityUtils.generateMD5("111aaa"));
            contentValues.put("state", 1); // 账户状态:0-删除，1-激活，2-未激活
            contentValues.put("mobile", "15221464296");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
            list.add(contentValues);
            boolean insertSuccess = true;
            db.beginTransaction();
            for (ContentValues cv : list) {
                long id = db.insert(ACCOUNT_TABLE_NAME, "typeName", cv);
                if (id == -1) {
                    insertSuccess = false;
                }
            }
            db.setTransactionSuccessful();
            if (insertSuccess) {
                LogUtils.d(TAG, "createAccountTable success");
            } else {
                LogUtils.d(TAG, "createAccountTable fail: insert init data fail");
            }
        } catch (SQLException e) {
            LogUtils.d(TAG, "createAccountTable fail: " + e.toString());
        }
        db.endTransaction();
    }

    private void createAccountLoginTable(SQLiteDatabase db) {
        try {
            db.execSQL("create table if not exists " + ACCOUNT_LOGIN_TABLE_NAME +
                    "(_id integer primary key autoincrement,accountId text not null," +
                    "loginTime datetime,logoutTime datetime)");
            LogUtils.d(TAG, "createAccountLoginTable success");
        } catch (SQLException e) {
            LogUtils.d(TAG, "createAccountLoginTable fail: " + e.toString());
        }
    }

    private void createShopTypeTable(SQLiteDatabase db) {
        try {
            db.execSQL("create table if not exists " + SHOP_TYPE_TABLE_NAME +
                    "(_id integer primary key autoincrement," +
                    "type text not null unique,typeName text not null," +
                    "createTime datetime,updateTime datetime)");
            Calendar calendar = Calendar.getInstance();
            List<ContentValues> list = new ArrayList<>();
            ContentValues contentValues = new ContentValues();
            contentValues.put("type", "1");
            contentValues.put("typeName", "景点");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
            list.add(contentValues);
            contentValues = new ContentValues();
            contentValues.put("type", "2");
            contentValues.put("typeName", "食品店");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
            list.add(contentValues);
            contentValues = new ContentValues();
            contentValues.put("type", "3");
            contentValues.put("typeName", "服装店");
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
            list.add(contentValues);
            boolean insertSuccess = true;
            db.beginTransaction();
            for (ContentValues cv : list) {
                long id = db.insert(SHOP_TYPE_TABLE_NAME, "typeName", cv);
                if (id == -1) {
                    insertSuccess = false;
                }
            }
            db.setTransactionSuccessful();
            if (insertSuccess) {
                LogUtils.d(TAG, "createShopTypeTable success");
            } else {
                LogUtils.d(TAG, "createShopTypeTable fail: insert init data fail");
            }
        } catch (SQLException e) {
            LogUtils.d(TAG, "createShopTypeTable fail: " + e.toString());
        }
        db.endTransaction();
    }

    private void createShopTable(SQLiteDatabase db) {
        try {
            db.execSQL("create table if not exists " + SHOP_TABLE_NAME +
                    "(_id integer primary key autoincrement,id text not null unique,name text not null," +
                    "type text not null,typeName text not null,mobile text not null," +
                    "userId text not null,latitude text not null,longitude text not null," +
                    "addressDistrict text not null,addressZipCode text not null,addressStreet text," +
                    "mainImgUrl text,imgUrls text,description text," +
                    "onlineDate datetime not null,remark text," +
                    "createTime datetime,updateTime datetime)");
            List<ContentValues> list = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -500);
            for (int i = 0; i < 24; i++) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("id", "1100" + "20190328102000000" + "0" + (i > 9 ? i : "0" + i));
                contentValues.put("name", "Shop Item " + (i + 1));
                contentValues.put("type", i % 3 == 0 ? "2" : "1");
                contentValues.put("typeName", i % 3 == 0 ? "食品店" : "景点");
                int r = new Random().nextInt(3);
                if (r == 1) {
                    contentValues.put("userId", "100020190328102000000001");
                    contentValues.put("mobile", "15221464292");
                    if (new Random().nextInt(10) > 1) {
                        contentValues.put("mainImgUrl", "http://pic9.nipic.com/20100824/2531170_082435310724_2.jpg");
                        contentValues.put("imgUrls", "http://pic9.nipic.com/20100824/2531170_082435310724_2.jpg," +
                                "https://img.zcool.cn/community/019af55798a4090000018c1be7a078.jpg@1280w_1l_2o_100sh.webp," +
                                "https://hellorfimg.zcool.cn/preview/70789213.jpg");
                    }
                } else if (r == 2) {
                    contentValues.put("userId", "100020190328102000000002");
                    contentValues.put("mobile", "15221464296");
                    if (new Random().nextInt(10) > 1) {
                        contentValues.put("mainImgUrl", "http://pic31.nipic.com/20130720/5793914_122325176000_2.jpg");
                        contentValues.put("imgUrls", "http://pic31.nipic.com/20130720/5793914_122325176000_2.jpg," +
                                "https://img.zcool.cn/community/019af55798a4090000018c1be7a078.jpg@1280w_1l_2o_100sh.webp," +
                                "https://hellorfimg.zcool.cn/preview/70789213.jpg");
                    }
                } else {
                    contentValues.put("userId", "100020190328102000000000");
                    contentValues.put("mobile", "18672943565");
                    if (new Random().nextInt(10) > 1) {
                        contentValues.put("mainImgUrl", "http://img.juimg.com/tuku/yulantu/140218/330598-14021R23A410.jpg");
                        contentValues.put("imgUrls", "http://img.juimg.com/tuku/yulantu/140218/330598-14021R23A410.jpg," +
                                "http://pic31.nipic.com/20130720/5793914_122325176000_2.jpg," +
                                "https://hellorfimg.zcool.cn/preview/70789213.jpg");
                    }
                }
                contentValues.put("latitude", String.valueOf(DecimalUtils.add(31.221367d, (i + 1) / 2000.0d, 6)));
                contentValues.put("longitude", String.valueOf(DecimalUtils.add(121.635707d, (i + 1) / 1000.0d, 6)));
                contentValues.put("addressDistrict", "上海市浦东新区浦东新区");
                contentValues.put("addressZipCode", "310115");
                calendar.add(Calendar.SECOND, new Random().nextInt(1728000));
                contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
                contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
                contentValues.put("onlineDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
                list.add(contentValues);
            }
            boolean insertSuccess = true;
            db.beginTransaction();
            for (ContentValues cv : list) {
                long id = db.insert(SHOP_TABLE_NAME, "name", cv);
                if (id == -1) {
                    insertSuccess = false;
                }
            }
            db.setTransactionSuccessful();
            if (insertSuccess) {
                LogUtils.d(TAG, "createShopTable success");
            } else {
                LogUtils.d(TAG, "createShopTable fail: insert init data fail");
            }
        } catch (SQLException e) {
            LogUtils.d(TAG, "createShopTable fail: " + e.toString());
        }
        db.endTransaction();
    }

    private void createProductTable(SQLiteDatabase db) {
        try {
            db.execSQL("create table if not exists " + PRODUCT_TABLE_NAME +
                    "(_id integer primary key autoincrement," +
                    "id text not null unique,name text not null,price text not null," +
                    "shelvePrice text not null,shelveDate datetime not null," +
                    "shopId text not null,description text,remark text," +
                    "createTime datetime,updateTime datetime)");
            List<ContentValues> list = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            for (int i = 0; i < 80; i++) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("id", "1101" + "20190328102000000" + "0" + (i > 9 ? i : "0" + i));
                contentValues.put("name", "Product Item " + (i + 1));
                int r = new Random().nextInt(24);
                float price = r * 2.0f + i;
                contentValues.put("price", String.valueOf(price));
                contentValues.put("shelvePrice", String.valueOf(price * r / (r + 2)));
                contentValues.put("shopId", "110020190328102000000" + "0" + (r > 9 ? r : "0" + r));
                contentValues.put("shelveDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
                contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
                contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
                list.add(contentValues);
            }
            boolean insertSuccess = true;
            db.beginTransaction();
            for (ContentValues cv : list) {
                long id = db.insert(PRODUCT_TABLE_NAME, "name", cv);
                if (id == -1) {
                    insertSuccess = false;
                }
            }
            db.setTransactionSuccessful();
            if (insertSuccess) {
                LogUtils.d(TAG, "createShopProductTable success");
            } else {
                LogUtils.d(TAG, "createShopProductTable fail: insert init data fail");
            }
        } catch (SQLException e) {
            LogUtils.d(TAG, "createShopProductTable fail: " + e.toString());
        }
        db.endTransaction();
    }

    private final String[] IMAGE_ARR = {"http://pic9.nipic.com/20100824/2531170_082435310724_2.jpg",
            "http://img.juimg.com/tuku/yulantu/140218/330598-14021R23A410.jpg",
            "http://img3.imgtn.bdimg.com/it/u=1568060428,2727116091&fm=26&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=2189972113,381634258&fm=26&gp=0.jpg",
            "http://pic31.nipic.com/20130720/5793914_122325176000_2.jpg",
            "http://img4.imgtn.bdimg.com/it/u=3528623204,755864954&fm=26&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=1922419374,2716826347&fm=26&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=3130635505,2228339018&fm=26&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=1372993673,3445969129&fm=26&gp=0.jpg"};

    private void createTravelNoteTable(SQLiteDatabase db) {
        try {
            db.execSQL("create table if not exists " + TRAVEL_NOTE_TABLE_NAME +
                    "(_id integer primary key autoincrement," +
                    "id text not null unique,title text not null,authorId text not null," +
                    "author text not null,dayCount integer not null default 0," +
                    "likeCount integer not null default 0," +
                    "isLike boolean not null default 'false',headImgUrl text," +
                    "readCount integer not null default 0,preface text not null," +
                    "days text,setOutDate datetime not null," +
                    "createTime datetime,updateTime datetime)");
            List<ContentValues> list = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -51);
            for (int i = 0; i < 50; i++) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("id", "1102" + "20190328102000000" + "0" + (i > 9 ? i : "0" + i));
                contentValues.put("title", "Travel Note Item " + (i + 1));
                int r = new Random().nextInt(3);
                if (r == 1) {
                    contentValues.put("authorId", "100020190328102000000001");
                    contentValues.put("author", "15221464292");
                } else if (r == 2) {
                    contentValues.put("authorId", "100020190328102000000002");
                    contentValues.put("author", "15221464296");
                } else {
                    contentValues.put("authorId", "100020190328102000000000");
                    contentValues.put("author", "admin");
                }
                int likeCount = new Random().nextInt(100) - 80;
                contentValues.put("likeCount", likeCount > 0 ? likeCount : 0);
                contentValues.put("isLike", new Random().nextInt(6) > 5);
                int readCount = new Random().nextInt(5000) - 400;
                contentValues.put("likeCount", readCount > 0 ? readCount : 0);
                contentValues.put("preface", "这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言");
                String days = "[{id:'1',day:'第1天',contentList:[{type:'text',index:'1',text:'第1天第1段'}," +
                        "{type:'text',index:'2',text:'第1天第2段'}]}";
                for (int j = 1; j < 10; j++) {
                    String str = "[{type:'text',index:'1',text:'第" + (j + 1) + "天第1段'}," +
                            "{type:'image',index:'2',remoteFilePath:'" + IMAGE_ARR[j - 1] + "',text:'第" + (j + 1) + "天第2段'}," +
                            "{type:'text',index:'3',text:'第" + (j + 1) + "天第3段'}]";
                    days += ",{id:'" + (j + 1) + "',day:'第" + (j + 1) + "天',contentList:" + str + "}";
                }
                days += "]";
                contentValues.put("dayCount", 10);
                contentValues.put("days", days);
                contentValues.put("setOutDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
                calendar.add(Calendar.DATE, 1);
                contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
                contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
                list.add(contentValues);
            }
            boolean insertSuccess = true;
            db.beginTransaction();
            for (ContentValues cv : list) {
                long id = db.insert(TRAVEL_NOTE_TABLE_NAME, "title", cv);
                if (id == -1) {
                    insertSuccess = false;
                }
            }
            db.setTransactionSuccessful();
            if (insertSuccess) {
                LogUtils.d(TAG, "createTravelNoteTable success");
            } else {
                LogUtils.d(TAG, "createTravelNoteTable fail: insert init data fail");
            }
        } catch (SQLException e) {
            LogUtils.d(TAG, "createTravelNoteTable fail: " + e.toString());
        }
        db.endTransaction();
    }

    private void createTravelNoteShopRelatedTable(SQLiteDatabase db) {
        try {
            db.execSQL("create table if not exists " + TRAVEL_NOTE_SHOP_TABLE_NAME +
                    "(_id integer primary key autoincrement," +
                    "shopId text not null,travelNoteId text not null)");
            List<ContentValues> list = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                String travelNoteId = "1102" + "20190328102000000" + "0" + (i > 9 ? i : "0" + i);
                ContentValues contentValues = new ContentValues();
                int r1 = new Random().nextInt(15);
                contentValues.put("shopId", "110020190328102000000" + "0" + (r1 > 9 ? r1 : "0" + r1));
                contentValues.put("travelNoteId", travelNoteId);
                list.add(contentValues);
                int r2 = new Random().nextInt(15);
                if (r2 != r1 && new Random().nextInt(5) > 1) {
                    contentValues = new ContentValues();
                    contentValues.put("shopId", "110020190328102000000" + "0" + (r2 > 9 ? r2 : "0" + r2));
                    contentValues.put("travelNoteId", travelNoteId);
                    list.add(contentValues);
                    int r3 = new Random().nextInt(15);
                    if (r3 != r1 && r3 != r2 && new Random().nextInt(3) > 1) {
                        contentValues = new ContentValues();
                        contentValues.put("shopId", "110020190328102000000" + "0" + (r3 > 9 ? r3 : "0" + r3));
                        contentValues.put("travelNoteId", travelNoteId);
                        list.add(contentValues);
                    }
                }
            }
            boolean insertSuccess = true;
            db.beginTransaction();
            for (ContentValues cv : list) {
                long id = db.insert(TRAVEL_NOTE_SHOP_TABLE_NAME, "shopId", cv);
                if (id == -1) {
                    insertSuccess = false;
                }
            }
            db.setTransactionSuccessful();
            if (insertSuccess) {
                LogUtils.d(TAG, "createTravelNoteShopRelatedTable success");
            } else {
                LogUtils.d(TAG, "createTravelNoteShopRelatedTable fail: insert init data fail");
            }
        } catch (SQLException e) {
            LogUtils.d(TAG, "createTravelNoteShopRelatedTable fail: " + e.toString());
        }
        db.endTransaction();
    }

    private void createTravelNoteCommentTable(SQLiteDatabase db) {
        try {
            db.execSQL("create table if not exists " + TRAVEL_NOTE_COMMENT_TABLE_NAME +
                    "(_id integer primary key autoincrement," +
                    "id text not null unique,content text not null,travelNoteId text not null," +
                    "authorId text not null,author text not null, headImgUrl text," +
                    "createTime datetime,updateTime datetime)");
            List<ContentValues> list = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, -10000);
            for (int i = 0; i < 900; i++) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("id", "1103" + "20190328102000000" + (i > 9 ? (i > 99 ? i : "0" + i) : "00" + i));
                contentValues.put("content", "Comment Item " + (i + 1));
                int tr = new Random().nextInt(40);
                String travelNoteId = "110220190328102000000" + "0" + (tr > 9 ? tr : "0" + tr);
                contentValues.put("travelNoteId", travelNoteId);
                int r = new Random().nextInt(3);
                if (r == 1) {
                    contentValues.put("authorId", "100020190328102000000001");
                    contentValues.put("author", "15221464292");
                } else if (r == 2) {
                    contentValues.put("authorId", "100020190328102000000002");
                    contentValues.put("author", "15221464296");
                } else {
                    contentValues.put("authorId", "100020190328102000000000");
                    contentValues.put("author", "admin");
                }
                contentValues.put("headImgUrl", "https://img.zcool.cn/community/019af55798a4090000018c1be7a078.jpg@1280w_1l_2o_100sh.webp");
                calendar.add(Calendar.SECOND, new Random().nextInt(10));
                contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
                contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
                list.add(contentValues);
            }
            boolean insertSuccess = true;
            db.beginTransaction();
            for (ContentValues cv : list) {
                long id = db.insert(TRAVEL_NOTE_COMMENT_TABLE_NAME, "title", cv);
                if (id == -1) {
                    insertSuccess = false;
                }
            }
            db.setTransactionSuccessful();
            if (insertSuccess) {
                LogUtils.d(TAG, "createTravelNoteCommentTable success");
            } else {
                LogUtils.d(TAG, "createTravelNoteCommentTable fail: insert init data fail");
            }
        } catch (SQLException e) {
            LogUtils.d(TAG, "createTravelNoteCommentTable fail: " + e.toString());
        }
        db.endTransaction();
    }
}
