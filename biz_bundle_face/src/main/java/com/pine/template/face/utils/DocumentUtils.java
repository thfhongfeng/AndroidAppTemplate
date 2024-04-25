package com.pine.template.face.utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
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
    public static boolean copyFile(@NonNull Context context, @NonNull File sourceFile, @NonNull Uri destDir) {
        return copyFile(context, sourceFile, destDir, sourceFile.getName());
    }

    public static boolean copyFile(@NonNull Context context, @NonNull File sourceFile,
                                   @NonNull Uri destDir, @NonNull String destFileName) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            DocumentFile destDFDir = DocumentFile.fromTreeUri(context, destDir);

            DocumentFile existingFile = destDFDir.findFile(destFileName);
            if (existingFile != null) {
                existingFile.delete();
            }

            DocumentFile destFile = destDFDir.createFile("*/*", destFileName);
            if (destFile != null) {
                inputStream = new FileInputStream(sourceFile);
                outputStream = (FileOutputStream) context.getContentResolver().openOutputStream(destFile.getUri());
                sourceChannel = inputStream.getChannel();
                destChannel = outputStream.getChannel();
                sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (sourceChannel != null) {
                    sourceChannel.close();
                }
                if (destChannel != null) {
                    destChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean copyFile(@NonNull Context context, @NonNull Uri sourceFile, @NonNull File destFile) {
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            inputStream = (FileInputStream) context.getContentResolver().openInputStream(sourceFile);
            outputStream = new FileOutputStream(destFile);
            sourceChannel = inputStream.getChannel();
            destChannel = outputStream.getChannel();
            sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (sourceChannel != null) {
                    sourceChannel.close();
                }
                if (destChannel != null) {
                    destChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean copyFile(@NonNull File sourceFile, @NonNull File destFile) {
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            if (destFile != null) {
                inputStream = new FileInputStream(sourceFile);
                outputStream = new FileOutputStream(destFile);
                sourceChannel = inputStream.getChannel();
                destChannel = outputStream.getChannel();
                sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (sourceChannel != null) {
                    sourceChannel.close();
                }
                if (destChannel != null) {
                    destChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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
