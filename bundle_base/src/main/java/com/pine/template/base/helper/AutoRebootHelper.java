package com.pine.template.base.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;

import com.pine.app.template.bundle_base.BuildConfigKey;
import com.pine.template.base.config.switcher.ConfigSwitcherServer;
import com.pine.template.base.receiver.AutoPowerReceiver;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.SharePreferenceUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AutoRebootHelper {
    private static final String TAG = "AutoRebootHelper";

    public static final String SP_KEY_AUTO_REBOOT_TIME = "SP_KEY_AUTO_REBOOT_TIME";
    private static final int REQUEST_CODE_POWER_REBOOT = 1;

    private static Handler mSetupAutoRebootHandler = new Handler(Looper.getMainLooper());

    public static void setupAutoReboot(Context context) {
        boolean enable = "true".equalsIgnoreCase(
                ConfigSwitcherServer.getConfig(BuildConfigKey.ENABLE_SCHEDULE_REBOOT));
        LogUtils.d(TAG, "setupAutoReboot enable:" + enable);
        if (enable) {
            observerAutoTime(context);
            setupAutoRebootImpl(context);
        } else {
            cancelAutoReboot(context);
        }
    }

    public static void setupAutoRebootImpl(Context context) {
        Calendar checkDate = Calendar.getInstance();
        checkDate.set(Calendar.YEAR, 2025);
        mSetupAutoRebootHandler.removeCallbacksAndMessages(null);
        if (Calendar.getInstance().before(checkDate)) {
            mSetupAutoRebootHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setupAutoRebootImpl(context);
                }
            }, 30 * 1000);
            return;
        }
        long lastScheduleTime = SharePreferenceUtils
                .readLongFromConfig(SP_KEY_AUTO_REBOOT_TIME, -1l);
        LogUtils.d(TAG, "setupAutoRebootImpl lastScheduleTime:" + lastScheduleTime);
        long scheduledTime = getAutoRebootTime();
        if (scheduledTime > 0 && scheduledTime != lastScheduleTime) {
            if (scheduleAutoReboot(context, scheduledTime) > 0) {
                SharePreferenceUtils.saveToConfig(SP_KEY_AUTO_REBOOT_TIME, scheduledTime);
            }
        }
    }

    private static long scheduleAutoReboot(Context context, long time) {
        if (time < 0) {
            return -1;
        }
        LogUtils.d(TAG, "scheduleAutoReboot time:" + time);

        cancelAutoReboot(context);

        Intent intent = new Intent(context, AutoPowerReceiver.class);
        intent.setAction(AutoPowerReceiver.ACTION_AUTO_REBOOT);

        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_POWER_REBOOT,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_POWER_REBOOT,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        return time;
    }

    private static void cancelAutoReboot(Context context) {
        LogUtils.d(TAG, "cancelAutoReboot");
        Intent intent = new Intent(context, AutoPowerReceiver.class);
        intent.setAction(AutoPowerReceiver.ACTION_AUTO_REBOOT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_POWER_REBOOT,
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingIntent);
        SharePreferenceUtils.saveToConfig(AutoRebootHelper.SP_KEY_AUTO_REBOOT_TIME, -1l);
    }

    private static long getAutoRebootTime() {
        String timeStr = ConfigSwitcherServer.getConfig(BuildConfigKey.CONFIG_SCHEDULE_REBOOT_TIME);
        if (TextUtils.isEmpty(timeStr)) {
            return -1;
        }
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Calendar now = Calendar.getInstance();
        Calendar pendingC = Calendar.getInstance();
        Calendar timeC = Calendar.getInstance();
        try {
            timeC.setTime(timeFormat.parse(timeStr));
            pendingC.set(Calendar.HOUR_OF_DAY, timeC.get(Calendar.HOUR_OF_DAY));
            pendingC.set(Calendar.MINUTE, timeC.get(Calendar.MINUTE));
            while (now.after(pendingC)) {
                pendingC.add(Calendar.DAY_OF_YEAR, 1);
            }
            return pendingC.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public static AutoTimeObserver mAutoTimeObserver = null;

    public static void observerAutoTime(Context context) {
        if (mAutoTimeObserver == null) {
            mAutoTimeObserver = new AutoTimeObserver(context, new Handler());
            context.getContentResolver().registerContentObserver(
                    Settings.System.getUriFor(Settings.System.AUTO_TIME),
                    false,
                    mAutoTimeObserver
            );
        }
    }

    public static void unObserverAutoTime(Context context) {
        if (mAutoTimeObserver != null) {
            context.getContentResolver().unregisterContentObserver(mAutoTimeObserver);
            mAutoTimeObserver = null;
        }
    }

    public static class AutoTimeObserver extends ContentObserver {
        private Context context;

        public AutoTimeObserver(Context context, Handler handler) {
            super(handler);
            this.context = context;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            boolean isAutoTimeEnable = Settings.System.getInt(
                    context.getContentResolver(),
                    Settings.System.AUTO_TIME,
                    0) == 1;
            LogUtils.d(TAG, "AutoTimeObserver onChange selfChange:" + selfChange
                    + ", isAutoTimeEnable:" + isAutoTimeEnable);
            if (isAutoTimeEnable) {
                setupAutoReboot(context);
            }
        }
    }
}