package com.pine.tool.util;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.RawRes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by tanghongfeng on 2018/10/10
 */

public class FileUtils {
    public final static String FILE_EXTENSION_SEPARATOR = ".";

    private FileUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * read file to string list, a element of list is a line
     *
     * @param filePath    路径
     * @param charsetName The name of a supported {@link
     *                    Charset </code>charset<code>}
     * @return if file not exist, return null, else return content of file
     * @throws RuntimeException if an error occurs while operator
     *                          BufferedReader
     */
    public static List<String> readFileToList(String filePath, String charsetName) {

        File file = new File(filePath);
        List<String> fileContent = new ArrayList<String>();
        if (file == null || !file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(
                    new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String readFile(String filePath) {
        File file = new File(filePath);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            int length = fileInputStream.available();
            byte[] buffer = new byte[length];
            fileInputStream.read(buffer);
            return new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return e.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    /**
     * 读取文本文件内容，以行的形式读取
     *
     * @param filePath 带有完整绝对路径的文件名
     * @param encoding 文本文件打开的编码方式 例如 GBK,UTF-8
     * @param sep      分隔符 例如：#，默认为\n;
     * @param bufLen   设置缓冲区大小
     * @return String 返回文本文件的内容
     */
    public static String readFileContent(String filePath, String encoding, String sep, int bufLen) {
        if (filePath == null || filePath.equals("")) {
            return "";
        }
        if (sep == null || sep.equals("")) {
            sep = "\n";
        }
        if (!new File(filePath).exists()) {
            return "";
        }
        StringBuffer str = new StringBuffer("");
        FileInputStream fs = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fs = new FileInputStream(filePath);
            if (encoding == null || encoding.trim().equals("")) {
                isr = new InputStreamReader(fs);
            } else {
                isr = new InputStreamReader(fs, encoding.trim());
            }
            br = new BufferedReader(isr, bufLen);

            String data = "";
            while ((data = br.readLine()) != null) {
                str.append(data).append(sep);
            }
        } catch (IOException e) {
        } finally {
            try {
                if (br != null) br.close();
                if (isr != null) isr.close();
                if (fs != null) fs.close();
            } catch (IOException e) {
            }
        }
        return str.toString();
    }

    /**
     * write file
     *
     * @param filePath 路径
     * @param content  上下文
     * @param append   is append, if true, write to the end of file, else clear
     *                 content of file and write into it
     * @return return false if content is empty, true otherwise
     * @throws RuntimeException if an error occurs while operator FileWriter
     */
    public static boolean writeFile(String filePath, String content, boolean append) {

        if (TextUtils.isEmpty(content)) {
            return false;
        }

        FileWriter fileWriter = null;
        try {
            makeDirs(filePath);
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * write file
     *
     * @param filePath    路径
     * @param contentList 集合
     * @param append      is append, if true, write to the end of file, else clear
     *                    content of file and write into it
     * @return return false if contentList is empty, true otherwise
     * @throws RuntimeException if an error occurs while operator FileWriter
     */
    public static boolean writeFile(String filePath, List<String> contentList, boolean append) {

        if (contentList == null || contentList.size() == 0) {
            return false;
        }

        FileWriter fileWriter = null;
        try {
            makeDirs(filePath);
            fileWriter = new FileWriter(filePath, append);
            int i = 0;
            for (String line : contentList) {
                if (i++ > 0) {
                    fileWriter.write("\r\n");
                }
                fileWriter.write(line);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * write file, the string will be written to the begin of the file
     *
     * @param filePath 地址
     * @param content  上下文
     * @return 是否写入成功
     */
    public static boolean writeFile(String filePath, String content) {
        return writeFile(filePath, content, false);
    }

    /**
     * write file, the string list will be written to the begin of the file
     *
     * @param filePath    地址
     * @param contentList 集合
     * @return 是否写入成功
     */
    public static boolean writeFile(String filePath, List<String> contentList) {
        return writeFile(filePath, contentList, false);
    }

    /**
     * write file, the bytes will be written to the begin of the file
     *
     * @param filePath 路径
     * @param stream   输入流
     * @return 返回是否写入成功
     */
    public static boolean writeFile(String filePath, InputStream stream) {
        return writeFile(filePath, stream, false);
    }

    /**
     * write file
     *
     * @param filePath 路径
     * @param stream   the input stream
     * @param append   if <code>true</code>, then bytes will be written to the
     *                 end
     *                 of the file rather than the beginning
     * @return return true
     * FileOutputStream
     */
    public static boolean writeFile(String filePath, InputStream stream, boolean append) {
        return writeFile(filePath != null ? new File(filePath) : null, stream,
                append);
    }

    /**
     * write file, the bytes will be written to the begin of the file
     *
     * @param file   文件对象
     * @param stream 输入流
     * @return 返回是否写入成功
     */
    public static boolean writeFile(File file, InputStream stream) {
        return writeFile(file, stream, false);
    }

    /**
     * write file
     *
     * @param file   the file to be opened for writing.
     * @param stream the input stream
     * @param append if <code>true</code>, then bytes will be written to the
     *               end
     *               of the file rather than the beginning
     * @return return true
     * @throws RuntimeException if an error occurs while operator
     *                          FileOutputStream
     */
    public static boolean writeFile(File file, InputStream stream, boolean append) {
        OutputStream o = null;
        try {
            makeDirs(file.getAbsolutePath());
            o = new FileOutputStream(file, append);
            byte data[] = new byte[1024];
            int length = -1;
            while ((length = stream.read(data)) != -1) {
                o.write(data, 0, length);
            }
            o.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            try {
                if (o != null) {
                    o.close();
                }
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (o != null) {
                    o.close();
                }
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return false;
        }
        try {
            if (o != null) {
                o.close();
            }
            if (stream != null) {
                stream.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return true;
    }

    /**
     * move file
     *
     * @param sourceFilePath 资源路径
     * @param destFilePath   删除的路径
     */
    public static boolean moveFile(String sourceFilePath, String destFilePath) {
        if (TextUtils.isEmpty(sourceFilePath) ||
                TextUtils.isEmpty(destFilePath)) {
            return false;
        }
        return moveFile(new File(sourceFilePath), new File(destFilePath));
    }

    /**
     * move file
     *
     * @param srcFile  文件对象
     * @param destFile 对象
     */
    public static boolean moveFile(File srcFile, File destFile) {
        boolean rename = srcFile.renameTo(destFile);
        if (rename) {
            return true;
        }
        if (copyFile(srcFile.getAbsolutePath(), destFile.getAbsolutePath())) {
            return deleteFile(srcFile.getAbsolutePath());
        }
        return false;
    }

    /**
     * copy file
     *
     * @param sourceFilePath 资源路径
     * @param destFilePath   删除的文件
     * @return 返回是否成功
     * @throws RuntimeException if an error occurs while operator
     *                          FileOutputStream
     */
    public static boolean copyFile(String sourceFilePath, String destFilePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(sourceFilePath);
        } catch (FileNotFoundException e) {
            return false;
        }
        return writeFile(destFilePath, inputStream);
    }

    /**
     * 删除文件
     *
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(File file, boolean deleteSelfIfDir) {
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                deleteFile(f.getAbsolutePath());
            }
        }
        if (deleteSelfIfDir) {
            return file.delete();
        }
        return true;
    }

    /**
     * 删除单个文件
     *
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(File file) {
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        boolean success = true;
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                success = success && f.delete();
            } else if (f.isDirectory()) {
                success = success && deleteFile(f.getAbsolutePath());
            }
        }
        return success && file.delete();
    }

    /**
     * @param path 路径
     * @return 是否删除成功
     */
    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return true;
        }

        File file = new File(path);
        return deleteFile(file);
    }

    /**
     * @param filePath 路径
     * @return 是否创建成功
     */
    public static boolean makeDirs(String filePath) {

        String folderName = getFolderName(filePath);
        if (TextUtils.isEmpty(folderName)) {
            return false;
        }

        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory())
                ? true
                : folder.mkdirs();
    }

    /**
     * @param filePath 路径
     * @return 是否创建成功
     */
    public static boolean makeFolders(String filePath) {
        return makeDirs(filePath);
    }

    /**
     * 文件重命名
     *
     * @param oldPath 旧的文件名字
     * @param newPath 新的文件名字
     */
    public static void renameFile(String oldPath, String newPath) {
        try {
            if (!TextUtils.isEmpty(oldPath) && !TextUtils.isEmpty(newPath)
                    && !oldPath.equals(newPath)) {
                File fileOld = new File(oldPath);
                File fileNew = new File(newPath);
                fileOld.renameTo(fileNew);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param path 路径
     * @return 返回文件大小
     */
    public static long getFileSize(String path) {
        if (TextUtils.isEmpty(path)) {
            return -1;
        }

        File file = new File(path);
        return (file.exists() && file.isFile() ? file.length() : -1);
    }

    /**
     * @param filePath 文件的路径
     * @return 返回文件的信息
     */
    public static String getFileNameWithoutExtension(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        int extendPos = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePos = filePath.lastIndexOf(File.separator);
        if (filePos == -1) {
            return (extendPos == -1
                    ? filePath
                    : filePath.substring(0, extendPos));
        }
        if (extendPos == -1) {
            return filePath.substring(filePos + 1);
        }
        return (filePos < extendPos ? filePath.substring(filePos + 1,
                extendPos) : filePath.substring(filePos + 1));
    }

    /**
     * get file name from path, include suffix
     * <p>
     * <pre>
     *      getFileName(null)               =   null
     *      getFileName("")                 =   ""
     *      getFileName("   ")              =   "   "
     *      getFileName("a.mp3")            =   "a.mp3"
     *      getFileName("a.b.rmvb")         =   "a.b.rmvb"
     *      getFileName("abc")              =   "abc"
     *      getFileName("c:\\")              =   ""
     *      getFileName("c:\\a")             =   "a"
     *      getFileName("c:\\a.b")           =   "a.b"
     *      getFileName("c:a.txt\\a")        =   "a"
     *      getFileName("/home/admin")      =   "admin"
     *      getFileName("/home/admin/a.txt/b.mp3")  =   "b.mp3"
     * </pre>
     *
     * @param filePath 路径
     * @return file name from path, include suffix
     */
    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
    }

    /**
     * get folder name from path
     * <p>
     * <pre>
     *      getFolderName(null)               =   null
     *      getFolderName("")                 =   ""
     *      getFolderName("   ")              =   ""
     *      getFolderName("a.mp3")            =   ""
     *      getFolderName("a.b.rmvb")         =   ""
     *      getFolderName("abc")              =   ""
     *      getFolderName("c:\\")              =   "c:"
     *      getFolderName("c:\\a")             =   "c:"
     *      getFolderName("c:\\a.b")           =   "c:"
     *      getFolderName("c:a.txt\\a")        =   "c:a.txt"
     *      getFolderName("c:a\\b\\c\\d.txt")    =   "c:a\\b\\c"
     *      getFolderName("/home/admin")      =   "/home"
     *      getFolderName("/home/admin/a.txt/b.mp3")  =   "/home/admin/a.txt"
     * </pre>
     *
     * @param filePath 路径
     * @return file name from path, include suffix
     */
    public static String getFolderName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePos = filePath.lastIndexOf(File.separator);
        return (filePos == -1) ? "" : filePath.substring(0, filePos);
    }

    /**
     * get suffix of file from path
     * <p>
     * <pre>
     *      getFileExtension(null)               =   ""
     *      getFileExtension("")                 =   ""
     *      getFileExtension("   ")              =   "   "
     *      getFileExtension("a.mp3")            =   "mp3"
     *      getFileExtension("a.b.rmvb")         =   "rmvb"
     *      getFileExtension("abc")              =   ""
     *      getFileExtension("c:\\")              =   ""
     *      getFileExtension("c:\\a")             =   ""
     *      getFileExtension("c:\\a.b")           =   "b"
     *      getFileExtension("c:a.txt\\a")        =   ""
     *      getFileExtension("/home/admin")      =   ""
     *      getFileExtension("/home/admin/a.txt/b")  =   ""
     *      getFileExtension("/home/admin/a.txt/b.mp3")  =   "mp3"
     * </pre>
     *
     * @param filePath 路径
     * @return 信息
     */
    public static String getFileExtension(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        int extendPos = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePos = filePath.lastIndexOf(File.separator);
        if (extendPos == -1) {
            return "";
        }
        return (filePos >= extendPos) ? "" : filePath.substring(extendPos + 1);
    }

    /**
     * 根据Uri获取文件的绝对路径，解决Android4.4以上版本Uri转换
     *
     * @param context
     * @param fileUri
     */
    @TargetApi(19)
    public static String getFileAbsolutePath(Context context, Uri fileUri) {
        if (context == null || fileUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, fileUri)) {
            if (isExternalStorageDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(fileUri)) {
                String id = DocumentsContract.getDocumentId(fileUri);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(fileUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(fileUri))
                return fileUri.getLastPathSegment();
            return getDataColumn(context, fileUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(fileUri.getScheme())) {
            return fileUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param filePath 路径
     * @return 是否存在这个文件
     */
    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    /**
     * @param directoryPath 路径
     * @return 是否有文件夹
     */
    public static boolean isFolderExist(String directoryPath) {
        if (TextUtils.isEmpty(directoryPath)) {
            return false;
        }

        File dire = new File(directoryPath);
        return (dire.exists() && dire.isDirectory());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean unZip(Context context, InputStream inputStream, String outputDirectory,
                                boolean isReWrite, String charset) {
        if (inputStream == null) {
            return false;
        }
        // 创建解压目标目录
        File file = new File(outputDirectory);
        // 如果目标目录不存在，则创建
        if (!file.exists()) {
            file.mkdirs();
        }
        ZipInputStream zipInputStream = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            zipInputStream = new ZipInputStream(inputStream, Charset.forName(charset));
        } else {
            zipInputStream = new ZipInputStream(inputStream);
        }
        // 读取一个进入点
        ZipEntry zipEntry = null;
        try {
            zipEntry = zipInputStream.getNextEntry();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        // 使用1M buffer
        byte[] buffer = new byte[1024 * 1024];
        // 解压时字节计数
        int count = 0;
        // 如果进入点为空说明已经遍历完所有压缩包中文件和目录
        while (zipEntry != null) {
            // 如果是一个目录
            if (zipEntry.isDirectory()) {
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                // 文件需要覆盖或者是文件不存在
                if (isReWrite || !file.exists()) {
                    file.mkdir();
                }
            } else {
                // 如果是文件
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                // 文件需要覆盖或者文件不存在，则解压文件
                if (isReWrite || !file.exists()) {
                    FileOutputStream fileOutputStream;
                    try {
                        file.createNewFile();
                        fileOutputStream = new FileOutputStream(file);
                        while ((count = zipInputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, count);
                        }
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                }
            }
            // 定位到下一个文件入口
            try {
                zipEntry = zipInputStream.getNextEntry();
            } catch (IOException e) {
                e.printStackTrace();
                zipEntry = null;
            }
        }
        try {
            inputStream.close();
            zipInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean unZipFile(Context context, String zipFilePath, String outputDirectory,
                                    boolean isReWrite) {
        return unZipFile(context, zipFilePath, outputDirectory, isReWrite, Charset.defaultCharset().displayName());
    }

    /**
     * 解压zip压缩文件到指定目录
     *
     * @param context         上下文对象
     * @param zipFilePath     压缩文件名
     * @param outputDirectory 输出目录
     * @param isReWrite       是否覆盖
     * @param charset         编码
     * @return
     */
    public static boolean unZipFile(Context context, String zipFilePath, String outputDirectory,
                                    boolean isReWrite, String charset) {
        if (!zipFilePath.toLowerCase().endsWith(".zip")) {
            System.out.println("非zip文件！");
            return false;
        }
        // 打开压缩文件
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(zipFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return unZip(context, inputStream, outputDirectory, isReWrite, charset);
    }

    /**
     * 解压assets的zip压缩文件到指定目录
     *
     * @param context         上下文对象
     * @param assetName       压缩文件名
     * @param outputDirectory 输出目录
     * @param isReWrite       是否覆盖
     * @param charset         编码
     * @return
     */
    public static boolean unZipAssets(Context context, String assetName, String outputDirectory,
                                      boolean isReWrite, String charset) {
        if (!assetName.toLowerCase().endsWith(".zip")) {
            System.out.println("非zip文件！");
            return false;
        }
        // 打开压缩文件
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(assetName);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return unZip(context, inputStream, outputDirectory, isReWrite, charset);
    }

    /**
     * 拷贝assets的文件到指定目录
     *
     * @param context         上下文对象
     * @param assetsPath      文件路径
     * @param outputDirectory 输出目录
     * @param isCopyAssetsDir 是否拷贝目录
     * @param isReWrite       是否覆盖
     * @throws IOException
     */
    public static void copyAssets(Context context, String assetsPath, String outputDirectory,
                                  boolean isCopyAssetsDir, boolean isReWrite) throws IOException {
        String fileNames[] = context.getAssets().list(assetsPath);// 获取assets目录下的所有文件及目录名
        if (fileNames.length > 0) {// 如果是目录
            File outDir = null;
            if (isCopyAssetsDir) {
                outDir = new File(outputDirectory + File.separator + assetsPath);
            } else {
                outDir = new File(outputDirectory);
            }
            // 如果目标目录不存在，则创建
            if (!outDir.exists()) {
                outDir.mkdirs();
            }
            for (String fileName : fileNames) {
                String out = outDir.getAbsolutePath();
                if (context.getAssets().list(assetsPath + File.separator + fileName).length > 0) {
                    out = outDir.getAbsolutePath() + File.separator + fileName;
                }
                copyAssets(context, assetsPath + File.separator + fileName,
                        out, false, isReWrite);
            }
        } else {// 如果是文件

            copyAssetsFile(context, assetsPath, outputDirectory, isReWrite);
        }
    }

    public static void copyAssetsFile(Context context, String assetsFilePath,
                                      String outputDirectory, boolean isReWrite) throws IOException {
        String assetsFileName = new File(assetsFilePath).getName();
        InputStream is = context.getAssets().open(assetsFilePath);
        String outPath = outputDirectory + File.separator + assetsFileName;
        File file = new File(outPath);
        if (isReWrite || !file.exists()) {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                // buffer字节
                fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
            }
            fos.flush();// 刷新缓冲区
            fos.close();
        }
        is.close();
    }

    /**
     * 调用系统方式打开文件.
     *
     * @param context 上下文
     * @param file    文件
     * @param failMsg
     */
    public static void openFile(Context context, File file, String failMsg) {
        try {
            // 调用系统程序打开文件.
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String fileType = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(getFileExtension(file.getPath()));
            intent.setDataAndType(Uri.fromFile(file), TextUtils.isEmpty(fileType) ? "*/*" : fileType);
            context.startActivity(intent);
        } catch (Exception ex) {
            Toast.makeText(context, failMsg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param context 上下文
     * @param file    文件对象
     * @param failMsg
     */
    public static void openMedia(Context context, File file, String failMsg) {
        if (file.getName().endsWith(".png") ||
                file.getName().endsWith(".jpg") ||
                file.getName().endsWith(".jpeg")) {
            viewPhoto(context, file, failMsg);
        } else {
            openFile(context, file, failMsg);
        }
    }

    /**
     * 打开多媒体文件.
     *
     * @param context 上下文
     * @param file    多媒体文件
     * @param failMsg
     */
    public static void viewPhoto(Context context, String file, String failMsg) {
        viewPhoto(context, new File(file), failMsg);
    }

    /**
     * 打开照片
     *
     * @param context 上下文
     * @param file    文件对象
     * @param failMsg
     */
    public static void viewPhoto(Context context, File file, String failMsg) {
        try {
            // 调用系统程序打开文件.
            Intent intent = new Intent(Intent.ACTION_VIEW);
            //			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "image/*");
            context.startActivity(intent);
        } catch (Exception ex) {
            Toast.makeText(context, failMsg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 根据文件路径，检查文件是否不大于指定大小
     *
     * @param filepath 文件路径
     * @param maxSize  最大
     * @return 是否
     */
    public static boolean checkFileSize(String filepath, int maxSize) {
        File file = new File(filepath);
        if (!file.exists() || file.isDirectory()) {
            return false;
        }
        if (file.length() <= maxSize * 1024) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 目录查找方式获取当前文件夹下所有文件 + 模糊查询（当不需要模糊查询时，nameSearchKey传空或null即可）
     *
     * @param folderPath    路径
     * @param nameSearchKey 模糊查询文件名字符串
     * @param suffixes      指定文件后缀名集合
     * @return
     */
    public static ArrayList<String> getFileList(String folderPath, String nameSearchKey, final String... suffixes) {
        ArrayList<String> filePathList = new ArrayList<>();// 文件列表
        File root = new File(folderPath);
        if (root.exists()) {
            if (root.isFile()) { // 路径为文件
                filePathList.add(root.getPath());
            } else { // 路径为文件夹
                final String finalSearchKey = nameSearchKey == null ? "" : nameSearchKey;// 若queryStr传入为null,则替换为空（indexOf匹配值不能为null）
                File files[] = root.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        if (!file.isFile()) {
                            return false;
                        }
                        boolean accept = false;
                        if (suffixes != null && suffixes.length > 0) {
                            for (String suffix : suffixes) {
                                suffix = '.' == suffix.charAt(0) ? suffix : "." + suffix;
                                if (file.getPath().endsWith(suffix)) {
                                    accept = true;
                                    break;
                                }
                            }
                        } else {
                            accept = true;
                        }
                        return accept && file.getPath().indexOf(finalSearchKey) != -1;
                    }
                });
                if (files != null && files.length > 0) {
                    for (int i = 0; i < files.length; i++) {
                        filePathList.add(files[i].getPath());
                    }
                }
            }
        }
        return filePathList;
    }

    /**
     * 系统接口方式获取所有文件 + 模糊查询（当不需要模糊查询时，nameSearchKey传空或null即可）
     *
     * @param context
     * @param nameSearchKey
     * @param suffixes
     */
    public static ArrayList<String> getFileListByMediaStore(Context context, String nameSearchKey, String... suffixes) {
        ArrayList<String> filePathList = new ArrayList<>();// 文件列表
        //从外存中获取
        Uri uri = MediaStore.Files.getContentUri("external");
        //筛选列，这里只筛选了：文件路径和不含后缀的文件名
        String[] projection = new String[]{
                MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE
        };
        //构造筛选语句
        String selection = "";
        for (int i = 0; i < suffixes.length; i++) {
            if (i != 0) {
                selection = selection + " OR ";
            }
            String suffix = '.' == suffixes[i].charAt(0) ? suffixes[i] : "." + suffixes[i];
            selection = selection + MediaStore.Files.FileColumns.DATA + " LIKE '%" + suffix + "'";
        }
        if (!TextUtils.isEmpty(nameSearchKey)) {
            selection = "(" + selection + ") AND " + MediaStore.Files.FileColumns.DATA + " LIKE '%" + nameSearchKey + "%'";
        }
        //按时间递增顺序对结果进行排序;待会从后往前移动游标就可实现时间递减
        String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED;
        //获取内容解析器对象
        ContentResolver resolver = context.getContentResolver();
        //获取游标
        Cursor cursor = resolver.query(uri, projection, selection, null, sortOrder);
        if (cursor != null) {
            //游标从最后开始往前递减，以此实现时间递减顺序（最近访问的文件，优先显示）
            if (cursor.moveToLast()) {
                do {
                    //输出文件的完整路径
                    String data = cursor.getString(0);
                    filePathList.add(data);
                } while (cursor.moveToPrevious());
            }
        }
        cursor.close();
        return filePathList;
    }

    public static boolean copyRawFile(Context context, String fileName, String resType, File targetFile) {
        return copyRawFile(context, context.getResources().getIdentifier(
                fileName.substring(0, fileName.lastIndexOf(".")),
                resType, context.getPackageName()), targetFile);
    }

    public static boolean copyRawFile(Context context, @RawRes int rawId, File targetFile) {
        // 从应用程序资源加载mode文件
        try (InputStream is = context.getResources().openRawResource(rawId);
             FileOutputStream os = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
