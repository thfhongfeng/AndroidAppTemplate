package com.pine.template.face.utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.documentfile.provider.DocumentFile;

import com.pine.tool.util.FileUtils;
import com.pine.tool.util.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DocumentUtils {
    public static boolean copyFile(Context context, File sourceFile, Uri destDir) {
        return copyFile(context, sourceFile, destDir, sourceFile.getName());
    }

    public static boolean copyFile(Context context, File sourceFile, Uri destDir, String destFileName) {
        FileInputStream inputStream = null;
        try {
            DocumentFile destDFDir = DocumentFile.fromTreeUri(context, destDir);

            DocumentFile existingFile = destDFDir.findFile(destFileName);
            if (existingFile != null) {
                existingFile.delete();
            }

            DocumentFile destFile = destDFDir.createFile("*/*", destFileName);
            if (destFile != null) {
                inputStream = new FileInputStream(sourceFile);
                FileOutputStream outputStream = (FileOutputStream) context.getContentResolver().openOutputStream(destFile.getUri());
                FileChannel sourceChannel = inputStream.getChannel();
                FileChannel destChannel = outputStream.getChannel();
                sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
                sourceChannel.close();
                destChannel.close();
                inputStream.close();
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean copyFile(Context context, Uri sourceFile, File destFile) {
        FileInputStream inputStream = null;
        try {
            if (destFile != null) {
                inputStream = (FileInputStream) context.getContentResolver().openInputStream(sourceFile);
                FileOutputStream outputStream = new FileOutputStream(destFile);
                FileChannel sourceChannel = inputStream.getChannel();
                FileChannel destChannel = outputStream.getChannel();
                sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
                sourceChannel.close();
                destChannel.close();
                inputStream.close();
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean saveFileToSdDownload(Context context, String srcFile, String fileName) {
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                Uri contentUri = MediaStore.Downloads.getContentUri("external");
                context.getContentResolver().delete(contentUri, "_display_name like '" + fileName + "%'", (String[]) null);
                ContentValues contentValues = new ContentValues();
                contentValues.put("_display_name", fileName);
                contentValues.put("mime_type", "*/*");
                long dateAdded = System.currentTimeMillis();
                contentValues.put("date_added", dateAdded);
                long dateModified = System.currentTimeMillis();
                contentValues.put("date_modified", dateModified);
                Uri insert = context.getContentResolver().insert(contentUri, contentValues);
                OutputStream outputStream = null;
                try {
                    LogUtils.w("PromiseManager", "copy file: " + srcFile + " to target file " + insert);
                    outputStream = context.getContentResolver().openOutputStream(insert);
                    Files.copy(Paths.get(srcFile), outputStream);
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    return false;
                }
            } else {
                String filePath = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + fileName;
                File file = new File(filePath);
                if (file != null && file.exists()) {
                    file.delete();
                }
                FileUtils.copyFile(srcFile, file.getPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
