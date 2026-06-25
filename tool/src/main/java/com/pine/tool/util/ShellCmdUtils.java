package com.pine.tool.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class ShellCmdUtils {
    private final static String TAG = ShellCmdUtils.class.getSimpleName();
    private static final Object CMD_LOCK = new Object();

    public static boolean executeCmd(String content) {
        synchronized (CMD_LOCK) { // 串行执行，防止并发卡死
            Process process = null;
            PrintWriter pw = null;
            InputStream stdOut = null;
            InputStream stdErr = null;
            try {
                // 查找su路径
                String suBin;
                if (Files.exists(Paths.get("/system/xbin/su"), new LinkOption[0])) {
                    suBin = "/system/xbin/su";
                } else if (Files.exists(Paths.get("/system/bin/su"), new LinkOption[0])) {
                    suBin = "/system/bin/su";
                } else {
                    LogUtils.e(TAG, "未找到su二进制，设备无root");
                    return false;
                }
                process = Runtime.getRuntime().exec(suBin);

                stdOut = process.getInputStream();
                stdErr = process.getErrorStream();
                // 异步吞流，新开线程，不会阻塞当前执行流程
                asyncDrainStream(stdOut);
                asyncDrainStream(stdErr);

                // 先写入命令，再等待执行
                pw = new PrintWriter(new OutputStreamWriter(process.getOutputStream()), true);
                pw.println(content);
                pw.println("exit");
                pw.flush();

                // 3秒超时等待进程结束
                boolean finished = process.waitFor(3, TimeUnit.SECONDS);
                if (!finished) {
                    LogUtils.e(TAG, "命令执行超时: " + content);
                    process.destroy();
                    return false;
                }

                int exitCode = process.exitValue();
                LogUtils.d(TAG, "cmd => " + content + " => exitCode=" + exitCode);
                return exitCode == 0;

            } catch (Exception e) {
                LogUtils.e(TAG, "executeCmd异常:" + e.getMessage());
                return false;
            } finally {
                // 释放资源
                try {
                    if (pw != null) pw.close();
                    if (stdOut != null) stdOut.close();
                    if (stdErr != null) stdErr.close();
                    if (process != null) process.destroy();
                } catch (IOException ignore) {
                }
            }
        }
    }

    /**
     * 异步后台吞流，不会阻塞调用线程
     */
    private static void asyncDrainStream(InputStream in) {
        if (in == null) return;
        new Thread(() -> {
            byte[] buf = new byte[256];
            try {
                while (in.read(buf) != -1) {
                    // 丢弃输出，不打印
                }
            } catch (IOException e) {
                // 流关闭后正常抛出，忽略
            }
        }).start();
    }
}
