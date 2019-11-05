package com.pine.db_server.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.pine.config.ConfigKey;
import com.pine.tool.util.DecimalUtils;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.SecurityUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.pine.db_server.DbConstants.ACCOUNT_ACCESS_LOG_TABLE_NAME;
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
        createAccountAccessLogTable(db);
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
                "createTime text,updateTime text)");
    }

    private void createConfigSwitcherTable(SQLiteDatabase db) {
        try {
            db.execSQL("create table if not exists " + SWITCHER_CONFIG_TABLE_NAME +
                    "(_id integer primary key autoincrement,configType integer not null," +
                    "accountType integer not null,configKey text not null,state integer not null," +
                    "createTime text,updateTime text)");
            List<ContentValues> list = new ArrayList<>();
            HashMap<Integer, Integer> accountTypeMap = new HashMap<>();
            accountTypeMap.put(0, 999999);
            for (int i = 10; i >= 1; i--) {
                accountTypeMap.put(i, 9000 + i * 10);
            }
            accountTypeMap.put(11, 100);
            accountTypeMap.put(12, 0);
            for (int i = 0; i < 13; i++) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("configType", 1); // 配置类型:0-缺省；1-模块开关；2-功能开关
                contentValues.put("configKey", ConfigKey.BUNDLE_DB_SEVER_KEY);
                contentValues.put("state", 1); // 是否开放：0-关闭；1-开放
                contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("accountType", accountTypeMap.get(i)); // 账户类型:0-游客（临时账户），100-注册用户，999999-超级管理员，会员(9000-9999之间)
                list.add(contentValues);
                contentValues = new ContentValues();
                contentValues.put("configType", 1); // 配置类型:0-缺省；1-模块开关；2-功能开关
                contentValues.put("configKey", ConfigKey.BUNDLE_WELCOME_KEY);
                contentValues.put("state", 1); // 是否开放：0-关闭；1-开放
                contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("accountType", accountTypeMap.get(i)); // 账户类型:0-游客（临时账户），100-注册用户，999999-超级管理员，会员(9000-9999之间)
                contentValues = new ContentValues();
                contentValues.put("configType", 1); // 配置类型:0-缺省；1-模块开关；2-功能开关
                contentValues.put("configKey", ConfigKey.BUNDLE_LOGIN_KEY);
                contentValues.put("state", 1); // 是否开放：0-关闭；1-开放
                contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("accountType", accountTypeMap.get(i)); // 账户类型:0-游客（临时账户），100-注册用户，999999-超级管理员，会员(9000-9999之间)
                list.add(contentValues);
                contentValues = new ContentValues();
                contentValues.put("configType", 1); // 配置类型:0-缺省；1-模块开关；2-功能开关
                contentValues.put("configKey", ConfigKey.BUNDLE_MAIN_KEY);
                contentValues.put("state", 1); // 是否开放：0-关闭；1-开放
                contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("accountType", accountTypeMap.get(i)); // 账户类型:0-游客（临时账户），100-注册用户，999999-超级管理员，会员(9000-9999之间)
                list.add(contentValues);
                contentValues = new ContentValues();
                contentValues.put("configType", 1); // 配置类型:0-缺省；1-模块开关；2-功能开关
                contentValues.put("configKey", ConfigKey.BUNDLE_USER_KEY);
                contentValues.put("state", 1); // 是否开放：0-关闭；1-开放
                contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("accountType", accountTypeMap.get(i)); // 账户类型:0-游客（临时账户），100-注册用户，999999-超级管理员，会员(9000-9999之间)
                list.add(contentValues);
                contentValues = new ContentValues();
                contentValues.put("configType", 1); // 配置类型:0-缺省；1-模块开关；2-功能开关
                contentValues.put("configKey", ConfigKey.BUNDLE_BUSINESS_MVC_KEY);
                contentValues.put("state", i < 12 ? 1 : 0); // 是否开放：0-关闭；1-开放
                contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("accountType", accountTypeMap.get(i)); // 账户类型:0-游客（临时账户），100-注册用户，999999-超级管理员，会员(9000-9999之间)
                list.add(contentValues);
                contentValues = new ContentValues();
                contentValues.put("configType", 1); // 配置类型:0-缺省；1-模块开关；2-功能开关
                contentValues.put("configKey", ConfigKey.BUNDLE_BUSINESS_MVP_KEY);
                contentValues.put("state", 1); // 是否开放：0-关闭；1-开放
                contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("accountType", accountTypeMap.get(i)); // 账户类型:0-游客（临时账户），100-注册用户，999999-超级管理员，会员(9000-9999之间)
                list.add(contentValues);
                contentValues = new ContentValues();
                contentValues.put("configType", 1); // 配置类型:0-缺省；1-模块开关；2-功能开关
                contentValues.put("configKey", ConfigKey.BUNDLE_BUSINESS_MVVM_KEY);
                contentValues.put("state", 1); // 是否开放：0-关闭；1-开放
                contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("accountType", accountTypeMap.get(i)); // 账户类型:0-游客（临时账户），100-注册用户，999999-超级管理员，会员(9000-9999之间)
                list.add(contentValues);

                contentValues = new ContentValues();
                contentValues.put("configType", 2); // 配置类型:0-缺省；1-模块开关；2-功能开关
                contentValues.put("configKey", ConfigKey.FUN_ADD_SHOP_KEY);
                contentValues.put("state", i < 11 ? 1 : 0); // 是否开放：0-关闭；1-开放
                contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("accountType", accountTypeMap.get(i)); // 账户类型:0-游客（临时账户），100-注册用户，999999-超级管理员，会员(9000-9999之间)
                list.add(contentValues);
                contentValues = new ContentValues();
                contentValues.put("configType", 2); // 配置类型:0-缺省；1-模块开关；2-功能开关
                contentValues.put("configKey", ConfigKey.FUN_ADD_PRODUCT_KEY);
                contentValues.put("state", i < 11 ? 1 : 0); // 是否开放：0-关闭；1-开放
                contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("accountType", accountTypeMap.get(i)); // 账户类型:0-游客（临时账户），100-注册用户，999999-超级管理员，会员(9000-9999之间)
                list.add(contentValues);
                contentValues = new ContentValues();
                contentValues.put("configType", 2); // 配置类型:0-缺省；1-模块开关；2-功能开关
                contentValues.put("configKey", ConfigKey.FUN_ADD_TRAVEL_NOTE_KEY);
                contentValues.put("state", i < 12 ? 1 : 0); // 是否开放：0-关闭；1-开放
                contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                contentValues.put("accountType", accountTypeMap.get(i)); // 账户类型:0-游客（临时账户），100-注册用户，999999-超级管理员，会员(9000-9999之间)
                list.add(contentValues);
            }
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
                    "minSupportedVersion text,force integer,fileName text,path text," +
                    "createTime text,updateTime text)");
            ContentValues contentValues = new ContentValues();
            contentValues.put("packageName", "com.pine.template");
            contentValues.put("versionName", "1.0.2");
            contentValues.put("versionCode", 2);
            contentValues.put("minSupportedVersion", 1);
            contentValues.put("force", 0);  // 是否强制更新：0-不强制；1-强制
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

    private final String[] HEAD_IMAGES = {"http://i1.sinaimg.cn/ent/d/2008-06-04/U105P28T3D2048907F326DT20080604225106.jpg",
            "https://c-ssl.duitang.com/uploads/item/201704/04/20170404153225_EiMHP.thumb.700_0.jpeg",
            "http://image2.sina.com.cn/IT/d/2005-10-31/U1235P2T1D752393F13DT20051031133235.jpg"};

    private void createAccountTable(SQLiteDatabase db) {
        try {
            db.execSQL("create table if not exists " + ACCOUNT_TABLE_NAME +
                    "(_id integer primary key autoincrement,id text not null unique," +
                    "account text not null,accountType integer not null," +
                    "name text not null,password text not null, headImgUrl text,state integer not null," +
                    "mobile text not null,curLoginTimeStamp integer not null,createTime text," +
                    "updateTime text)");
            int imageTotalCount = HEAD_IMAGES.length;
            Calendar calendar = Calendar.getInstance();
            List<ContentValues> list = new ArrayList<>();
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", "1000" + "20190328102000000" + "000");
            contentValues.put("account", "admin");
            contentValues.put("accountType", 999999); // 账户类型:0-游客（临时账户），100-注册用户，999999-超级管理员，会员(9000-9999之间)
            contentValues.put("name", "admin");
            contentValues.put("password", SecurityUtils.generateMD5("111aaa"));
            contentValues.put("state", 1); // 账户状态:0-删除，1-激活，2-未激活
            contentValues.put("mobile", "18672943565");
            contentValues.put("headImgUrl", HEAD_IMAGES[0 % imageTotalCount]);
            contentValues.put("curLoginTimeStamp", 0);
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
            list.add(contentValues);
            contentValues = new ContentValues();
            contentValues.put("id", "1000" + "20190328102000000" + "001");
            contentValues.put("account", "15221464292");
            contentValues.put("accountType", 9100); // 账户类型:0-游客（临时账户），100-注册用户，999999-超级管理员，会员(9000-9999之间)
            contentValues.put("name", "15221464292");
            contentValues.put("password", SecurityUtils.generateMD5("111aaa"));
            contentValues.put("state", 1); // 账户状态:0-删除，1-激活，2-未激活
            contentValues.put("mobile", "15221464292");
            contentValues.put("headImgUrl", HEAD_IMAGES[1 % imageTotalCount]);
            contentValues.put("curLoginTimeStamp", 0);
            contentValues.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
            contentValues.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
            list.add(contentValues);
            contentValues = new ContentValues();
            contentValues.put("id", "1000" + "20190328102000000" + "002");
            contentValues.put("account", "15221464296");
            contentValues.put("accountType", 9010); // 账户类型:0-游客（临时账户），100-注册用户，999999-超级管理员，会员(9000-9999之间)
            contentValues.put("name", "15221464296");
            contentValues.put("password", SecurityUtils.generateMD5("111aaa"));
            contentValues.put("state", 1); // 账户状态:0-删除，1-激活，2-未激活
            contentValues.put("mobile", "15221464296");
            contentValues.put("headImgUrl", HEAD_IMAGES[2 % imageTotalCount]);
            contentValues.put("curLoginTimeStamp", 0);
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

    private void createAccountAccessLogTable(SQLiteDatabase db) {
        try {
            db.execSQL("create table if not exists " + ACCOUNT_ACCESS_LOG_TABLE_NAME +
                    "(_id integer primary key autoincrement,accountId text not null," +
                    "loginTimeStamp integer,logoutTimeStamp integer)");
            LogUtils.d(TAG, "createAccountAccessLogTable success");
        } catch (SQLException e) {
            LogUtils.d(TAG, "createAccountAccessLogTable fail: " + e.toString());
        }
    }

    private void createShopTypeTable(SQLiteDatabase db) {
        try {
            db.execSQL("create table if not exists " + SHOP_TYPE_TABLE_NAME +
                    "(_id integer primary key autoincrement," +
                    "type text not null unique,typeName text not null," +
                    "createTime text,updateTime text)");
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

    private final String[] SHOP_IMAGES = {"http://img.sccnn.com/bimg/337/31660.jpg",
            "http://img.qqzhi.com/uploads/2019-02-28/093640204.jpg",
            "http://img.juimg.com/tuku/yulantu/140218/330598-14021R23A410.jpg",
            "https://img.zcool.cn/community/019af55798a4090000018c1be7a078.jpg@1280w_1l_2o_100sh.webp",
            "https://c-ssl.duitang.com/uploads/item/201510/08/20151008100856_uGVh5.thumb.700_0.jpeg",
            "http://www.xdfpr.com/uploadfile/2017/0629/20170629023457170.jpg",
            "http://pic1.win4000.com/wallpaper/2018-12-04/5c062a2388f3a.jpg",
            "https://c-ssl.duitang.com/uploads/item/201208/30/20120830173930_PBfJE.thumb.700_0.jpeg",
            "https://hbimg.huabanimg.com/146b38721f241d26f389be9b1f7155533116f299caa99-RJwASk_fw658",
            "https://hbimg.huabanimg.com/45858c1f11e0b3c30bd0113c6f7ab88f5847034e51d57-Hprwwb_fw658",
            "http://pic.lvmama.com/uploads/pc/place2/2017-07-25/dfb764eb-f294-4e44-92c0-2f0e0db5542b.jpg",
            "http://static.jisutui.vip/data/upload/2019/05/4dbe54b546jhkudt.jpg",
            "http://static.jisutui.vip/data/upload/2019/05/5424747238ryipcc.jpg",
            "http://www.xdfpr.com/uploadfile/2017/0204/20170204053005927.jpg",
            "http://static.jisutui.vip/data/upload/2019/05/63edd9d032smord6.jpg",
            "https://c-ssl.duitang.com/uploads/item/201207/02/20120702194505_8V2yi.jpeg"};

    private void createShopTable(SQLiteDatabase db) {
        try {
            db.execSQL("create table if not exists " + SHOP_TABLE_NAME +
                    "(_id integer primary key autoincrement,id text not null unique,name text not null," +
                    "type text not null,typeName text not null,mobile text not null," +
                    "accountId text not null,latitude text not null,longitude text not null," +
                    "addressDistrict text not null,addressZipCode text not null,addressStreet text," +
                    "mainImgUrl text,imgUrls text,description text," +
                    "onlineDate text not null,remark text," +
                    "createTime text,updateTime text)");
            List<ContentValues> list = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -500);
            int imageTotalCount = SHOP_IMAGES.length;
            for (int i = 0; i < 24; i++) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("id", "1100" + "20190328102000000" + "0" + (i > 9 ? i : "0" + i));
                contentValues.put("name", "Shop Item " + (i + 1));
                contentValues.put("type", i % 3 == 0 ? "2" : "1");
                contentValues.put("typeName", i % 3 == 0 ? "食品店" : "景点");
                int r = new Random().nextInt(3);
                int hasImageInt = new Random().nextInt(15);
                int index = new Random().nextInt(100);
                if (r == 1) {
                    contentValues.put("accountId", "100020190328102000000001");
                    contentValues.put("mobile", "15221464292");
                    if (hasImageInt > 1) {
                        contentValues.put("mainImgUrl", SHOP_IMAGES[index % imageTotalCount]);
                        contentValues.put("imgUrls", SHOP_IMAGES[index % imageTotalCount] + "," +
                                SHOP_IMAGES[(index + 1) % imageTotalCount] + "," +
                                SHOP_IMAGES[(index + 2) % imageTotalCount] + "," +
                                SHOP_IMAGES[(index + 3) % imageTotalCount]);
                    }
                } else if (r == 2) {
                    contentValues.put("accountId", "100020190328102000000002");
                    contentValues.put("mobile", "15221464296");
                    if (hasImageInt > 1) {
                        contentValues.put("mainImgUrl", SHOP_IMAGES[index % imageTotalCount]);
                        contentValues.put("imgUrls", SHOP_IMAGES[index % imageTotalCount] + "," +
                                SHOP_IMAGES[(index + 1) % imageTotalCount] + "," +
                                SHOP_IMAGES[(index + 2) % imageTotalCount]);
                    }
                } else {
                    contentValues.put("accountId", "100020190328102000000000");
                    contentValues.put("mobile", "18672943565");
                    if (hasImageInt > 1) {
                        contentValues.put("mainImgUrl", SHOP_IMAGES[index % imageTotalCount]);
                        contentValues.put("imgUrls", SHOP_IMAGES[index % imageTotalCount] + "," +
                                SHOP_IMAGES[(index + 1) % imageTotalCount]);
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
                    "shelvePrice text not null,shelveDate text not null," +
                    "shopId text not null,description text,remark text," +
                    "createTime text,updateTime text)");
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

    private final String[] TRAVEL_NOTE_IMAGES = {"http://img.sccnn.com/bimg/337/31660.jpg",
            "http://img.mp.itc.cn/upload/20161230/415c05319f0a4318a5cb2662ae314a84_th.jpeg",
            "http://img.juimg.com/tuku/yulantu/140218/330598-14021R23A410.jpg",
            "https://c-ssl.duitang.com/uploads/item/201404/24/20140424154030_hyiBw.thumb.700_0.jpeg",
            "http://pic1.win4000.com/wallpaper/2018-12-04/5c062a2388f3a.jpg",
            "https://n.sinaimg.cn/translate/666/w400h266/20191002/84ae-ifmectk3880839.jpg",
            "http://img.mp.itc.cn/upload/20161230/b92cb3c2841b40b9994c709a4a365bd1_th.jpeg",
            "http://img.qqzhi.com/uploads/2019-02-28/093640204.jpg",
            "https://img1.qunarzz.com/travel/d2/1807/c1/9b4f4b705cce1b5.jpg_480x360x95_784166eb.jpg",
            "https://hbimg.huabanimg.com/146b38721f241d26f389be9b1f7155533116f299caa99-RJwASk_fw658",
            "http://img.mp.itc.cn/upload/20161230/20aecebf989547588677d7247e782c12_th.jpeg",
            "http://n.sinaimg.cn/sinacn10113/208/w640h368/20190629/88dd-hyzpvis2421955.jpg",
            "https://hbimg.huabanimg.com/45858c1f11e0b3c30bd0113c6f7ab88f5847034e51d57-Hprwwb_fw658",
            "http://img.mp.itc.cn/upload/20161230/4328110b663444b09ddf39e450ff2772_th.jpeg",
            "https://c-ssl.duitang.com/uploads/item/201207/02/20120702194505_8V2yi.jpeg",
            "https://img1.qunarzz.com/travel/d0/1807/5b/40f7d2eb06e4ebb5.jpg_480x360x95_89f62ae3.jpg",
            "http://img.juimg.com/tuku/yulantu/110516/1717-11051604500688.jpg"};

    private void createTravelNoteTable(SQLiteDatabase db) {
        try {
            db.execSQL("create table if not exists " + TRAVEL_NOTE_TABLE_NAME +
                    "(_id integer primary key autoincrement," +
                    "id text not null unique,title text not null,authorId text not null," +
                    "author text not null,dayCount integer not null default 0," +
                    "likeCount integer not null default 0," +
                    "hot integer not null default 0,headImgUrl text," +
                    "readCount integer not null default 0,preface text not null," +
                    "days text,setOutDate text not null," +
                    "createTime text,updateTime text)");
            List<ContentValues> list = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -51);
            int imageTotalCount = TRAVEL_NOTE_IMAGES.length;
            int headImageTotalCount = HEAD_IMAGES.length;
            for (int i = 0; i < 50; i++) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("id", "1102" + "20190328102000000" + "0" + (i > 9 ? i : "0" + i));
                contentValues.put("title", "Travel Note Item " + (i + 1));
                int r = new Random().nextInt(3);
                contentValues.put("headImgUrl", HEAD_IMAGES[r % headImageTotalCount]);
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
                contentValues.put("hot", new Random().nextInt(6) > 2 ? 1 : 0); // 是否热门文章:0-否；1-是
                int readCount = new Random().nextInt(5000) - 400;
                contentValues.put("likeCount", readCount > 0 ? readCount : 0);
                contentValues.put("preface", "这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言这是一段前言");
                String days = "[{id:'1',day:'第1天',contentList:[{type:'text',index:'1',text:'第1天第1段'}," +
                        "{type:'text',index:'2',text:'第1天第2段'}]}";
                for (int j = 1; j < new Random().nextInt(8) * 3 + 1; j++) {
                    String str = "[{type:'text',index:'1',text:'第" + (j + 1) + "天第1段'}," +
                            "{type:'image',index:'2',remoteFilePath:'" + TRAVEL_NOTE_IMAGES[(j - 1) % imageTotalCount] + "',text:'第" + (j + 1) + "天第2段'}," +
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
                    "createTime text,updateTime text)");
            List<ContentValues> list = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, -10000);
            int imageTotalCount = HEAD_IMAGES.length;
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
                contentValues.put("headImgUrl", HEAD_IMAGES[r % imageTotalCount]);
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
