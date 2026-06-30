package com.pine.template.base.business.track;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.template.bundle_base.R;
import com.pine.tool.util.LogUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = CrashHandler.class.getSimpleName();
    private static final CrashHandler INSTANCE = new CrashHandler();
    private Thread.UncaughtExceptionHandler mSystemHandler;
    private Context mAppContext;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context appCtx) {
        LogUtils.d(TAG, "init");
        mAppContext = appCtx.getApplicationContext();
        mSystemHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void recordCrashToDb(@NonNull Context context) {
        // 在这里新开线程扫描崩溃日志入库
        new Thread(() -> handleAllCrashLogFile(context)).start();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        LogUtils.d(TAG, "uncaughtException thread:" + thread + ", ex:" + ex);
        boolean saved = saveCrashLogToLocalFile(thread, ex);

        try {
            Thread.sleep(800); // 给文件IO落盘时间
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "uncaughtException killProcess");
        // 主动切到系统桌面独立Task，让AMS有合法前台页面，不再拉起FallbackHome
        try {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            // FLAG_NEW_TASK：创建全新桌面任务栈，脱离当前崩溃App的Task
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mAppContext.startActivity(homeIntent);
        } catch (Exception ignored) {}
        // 销毁本App全部Activity，清空任务栈（关键，消除无效Task）
        finishAllActivities();
        // 双保险彻底销毁整个进程，不存在残留、不会ANR
        Process.killProcess(Process.myPid());
        System.exit(1);
        Runtime.getRuntime().halt(1);
    }

    /**
     * 遍历ActivityThread内部所有ActivityRecord，全部finish，清空当前任务栈
     */
    private void finishAllActivities() {
        try {
            Class<?> activityThreadCls = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadCls.getDeclaredMethod("currentActivityThread");
            Object activityThreadObj = currentActivityThreadMethod.invoke(null);

            Field mActivitiesField = activityThreadCls.getDeclaredField("mActivities");
            mActivitiesField.setAccessible(true);
            Map<?, ?> activityRecordMap = (Map<?, ?>) mActivitiesField.get(activityThreadObj);

            // 逐个销毁所有页面
            for (Object recordObj : activityRecordMap.values()) {
                Class<?> recordCls = recordObj.getClass();
                Field activityField = recordCls.getDeclaredField("activity");
                activityField.setAccessible(true);
                Activity targetAct = (Activity) activityField.get(recordObj);
                if (targetAct != null) {
                    targetAct.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean saveCrashLogToLocalFile(Thread thread, Throwable ex) {
        LogUtils.d(TAG, "saveCrashLogToLocalFile thread:" + thread + ", ex:" + ex);
        try {
            File crashDir = new File(mAppContext.getExternalFilesDir(null), "crash_log");
            if (!crashDir.exists()) {
                crashDir.mkdirs();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
            String timeStr = sdf.format(new Date());
            String fileName = "crash_" + timeStr + "_" + System.currentTimeMillis() + ".log";
            File logFile = new File(crashDir, fileName);

            StringBuilder sb = new StringBuilder();
            sb.append("crash_file_name:").append(fileName).append("\n");
            sb.append("crash_time:").append(new Date()).append("\n");
            sb.append("thread_name:").append(thread.getName()).append("\n");
            sb.append("brand:").append(Build.BRAND).append("\n");
            sb.append("model:").append(Build.MODEL).append("\n");
            sb.append("sdk:").append(Build.VERSION.SDK_INT).append("\n");
            sb.append("system_version:").append(Build.VERSION.RELEASE).append("\n");
            sb.append("===== stack trace =====\n");

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            sb.append(sw);

            // 同步写入磁盘
            FileWriter writer = new FileWriter(logFile);
            writer.write(sb.toString());
            writer.flush();
            writer.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 扫描crash_log目录，每条日志转为AppTrack入库
     */
    private void handleAllCrashLogFile(@NonNull Context context) {
        File crashDir = new File(context.getExternalFilesDir(null), "crash_log");
        if (!crashDir.exists()) {
            LogUtils.d(TAG, "handleAllCrashLogFile crash_log dir not exist");
            return;
        }
        File[] logFiles = crashDir.listFiles();
        if (logFiles == null || logFiles.length == 0) {
            LogUtils.d(TAG, "handleAllCrashLogFile crash_log dir is empty");
            return;
        }
        LogUtils.d(TAG, "handleAllCrashLogFile done");
        for (File logFile : logFiles) {
            if (!logFile.getName().endsWith(".log")) continue;
            try {
                convertCrashFileToTrack(context, logFile);
                // 入库成功删除本地日志
                logFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
                // 单条失败不影响其他，下次启动重试
            }
        }

        // 清理日志文件兜底
        cleanExpiredCrashLog(crashDir);
    }

    /**
     * 单条崩溃文件 -> recordInfoCrash
     */
    private void convertCrashFileToTrack(@NonNull Context context, File logFile) throws Exception {
        String fullLog = readFileToString(logFile);
        long crashTime = logFile.lastModified();
        if (!TextUtils.isEmpty(fullLog) && crashTime > 0) {
            recordInfoCrash(context, crashTime, fullLog);
        }
    }

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private void recordInfoCrash(@NonNull Context context, long crashTime, String logTxt) {
        LogUtils.d(TAG, "recordInfoCrash crashTime:" + crashTime + "\n" + logTxt);
        String actionData = context.getString(R.string.info_crash_log,
                mSimpleDateFormat.format(crashTime), logTxt);
        AppTrackManager.getInstance().recordInfoState(TrackDefaultBuilder.MODULE_DEBUG, "CrashGlobalExceptionHandler",
                TrackDefaultBuilder.DEBUG, actionData,
                crashTime, true);
    }

    // 读取日志文件全文
    private String readFileToString(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();
        return sb.toString();
    }

    // 日志最大保留条数
    private final int MAX_CRASH_LOG_COUNT = 30;

    /**
     * 清理日志文件兜底
     */
    private void cleanExpiredCrashLog(File crashDir) {
        File[] files = crashDir.listFiles();
        if (files == null || files.length == 0) {
            LogUtils.i(TAG, "cleanExpiredCrashLog ignore for no log file");
            return;
        }
        List<File> validFileList = new ArrayList<>();

        // 文件数量超过上限，按修改时间升序（最旧在前）删除
        if (validFileList.size() > MAX_CRASH_LOG_COUNT) {
            LogUtils.d(TAG, "cleanExpiredCrashLog done");
            // 按文件最后修改时间从小到大排序
            validFileList.sort(Comparator.comparingLong(File::lastModified));
            // 需要删除的条数
            int delCount = validFileList.size() - MAX_CRASH_LOG_COUNT;
            for (int i = 0; i < delCount; i++) {
                File oldFile = validFileList.get(i);
                oldFile.delete();
            }
        } else {
            LogUtils.i(TAG, "cleanExpiredCrashLog ignore for crash log file count not out max");
        }
    }
}
