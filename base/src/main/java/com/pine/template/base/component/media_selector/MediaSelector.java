package com.pine.template.base.component.media_selector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.pine.template.base.component.media_selector.bean.MediaBean;
import com.pine.template.base.component.media_selector.ui.MediaSelectActivity;
import com.pine.tool.util.InstallUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tanghongfeng on 2018/11/2
 */

public class MediaSelector {
    public static final String INTENT_SELECTED_MEDIA_LIST = "intent_selected_media_list";
    public static final String INTENT_SELECTED_MEDIA_TYPE = "intent_selected_media_type";
    public static final String INTENT_SELECTED_TREE_URI = "intent_selected_tree_uri";
    public static final String INTENT_MAX_SELECTED_COUNT = "intent_max_select_count";
    public static final String INTENT_IS_RESELECT = "intent_is_reselect";
    public static final String INTENT_CAN_CAMERA_PIC = "intent_can_camera_pic";
    public static final String INTENT_CAN_CAMERA_VIDEO = "intent_can_camera_video";
    public static final String INTENT_CAMERA_PIC_FILE = "intent_camera_pic_file";
    public static final String INTENT_CAMERA_VIDEO_FILE = "intent_camera_video_file";
    public static final String INTENT_DATE_ORDER = "intent_date_order";
    private int mMaxCount = Integer.MAX_VALUE;
    private Uri mTreeUri;
    private int[] mSelectedTypes;
    private ArrayList<MediaBean> mSelectedData;
    private boolean mIsReselect = false;
    private boolean mCanCameraPic = false;
    private boolean mCanCameraVideo = false;
    private File mCameraPicFile;
    private File mCameraVideoFile;
    // 0-不排序；1-DESC；2-ASC
    private int mOrder = 0;

    private MediaSelector() {
    }

    public static MediaSelector create() {
        return new MediaSelector();
    }

    public static MediaSelector createImageSelector() {
        MediaSelector selector = new MediaSelector();
        selector.type(MediaBean.TYPE_IMAGE);
        return selector;
    }

    public static MediaSelector createVideoSelector() {
        MediaSelector selector = new MediaSelector();
        selector.type(MediaBean.TYPE_VIDEO);
        return selector;
    }

    public static MediaSelector createMediaSelector() {
        MediaSelector selector = new MediaSelector();
        selector.type(MediaBean.TYPE_IMAGE, MediaBean.TYPE_VIDEO);
        return selector;
    }

    public MediaSelector count(int count) {
        mMaxCount = count;
        return this;
    }

    public MediaSelector selected(ArrayList<MediaBean> images) {
        mSelectedData = images;
        return this;
    }

    public MediaSelector isReselect(boolean isReselect) {
        mIsReselect = isReselect;
        return this;
    }

    public MediaSelector type(int... types) {
        mSelectedTypes = types;
        return this;
    }

    public MediaSelector treeUri(Uri treeUri) {
        mTreeUri = treeUri;
        return this;
    }

    public MediaSelector addCameraTakePic() {
        mCanCameraPic = true;
        return this;
    }

    public MediaSelector addCameraTakeVideo() {
        mCanCameraVideo = true;
        return this;
    }

    public MediaSelector addCameraTakePic(File picFile) {
        mCanCameraPic = true;
        mCameraPicFile = picFile;
        return this;
    }

    public MediaSelector addCameraTakeVideo(File videoFile) {
        mCanCameraVideo = true;
        mCameraVideoFile = videoFile;
        return this;
    }

    public MediaSelector addCamera(File picFile, File videoFile) {
        mCanCameraPic = true;
        mCameraPicFile = picFile;
        mCanCameraVideo = true;
        mCameraVideoFile = videoFile;
        return this;
    }

    public MediaSelector addCamera() {
        mCanCameraPic = true;
        mCanCameraVideo = true;
        return this;
    }

    public MediaSelector orderByDesc() {
        mOrder = 1;
        return this;
    }

    public MediaSelector orderByAsc() {
        mOrder = 2;
        return this;
    }

    public void start(Activity activity, int requestCode) {
        final Context context = activity;
        Intent intent = new Intent(context, MediaSelectActivity.class);
        if (mMaxCount != Integer.MAX_VALUE) {
            intent.putExtra(INTENT_MAX_SELECTED_COUNT, mMaxCount);
        }
        if (mSelectedTypes != null) {
            intent.putExtra(INTENT_SELECTED_MEDIA_TYPE, mSelectedTypes);
        }
        if (mSelectedData != null) {
            intent.putParcelableArrayListExtra(INTENT_SELECTED_MEDIA_LIST, mSelectedData);
        }
        if (mTreeUri != null) {
            intent.putExtra(INTENT_SELECTED_TREE_URI, mTreeUri);
        }
        intent.putExtra(INTENT_IS_RESELECT, mIsReselect);
        intent.putExtra(INTENT_CAN_CAMERA_PIC, mCanCameraPic);
        intent.putExtra(INTENT_CAN_CAMERA_VIDEO, mCanCameraVideo);
        if (mCameraPicFile != null) {
            intent.putExtra(INTENT_CAMERA_PIC_FILE, mCameraPicFile);
        }
        if (mCameraVideoFile != null) {
            intent.putExtra(INTENT_CAMERA_VIDEO_FILE, mCameraVideoFile);
        }
        intent.putExtra(INTENT_DATE_ORDER, mOrder);
        activity.startActivityForResult(intent, requestCode);
    }

    public static boolean checkPersistedUriPermission(Context context, Uri uri) {
        if (uri != null) {
            List<UriPermission> uriPermissions = context.getContentResolver().getPersistedUriPermissions();
            for (UriPermission permission : uriPermissions) {
                if (permission.getUri().equals(uri)) {
                    // 权限仍有效，可直接使用
                    return true;
                }
            }
        }
        return false;
    }

    public static String getTreeUriDocPath(Uri treeUri) {
        String treeDoc = "";
        if (treeUri != null) {
            if (DocumentsContract.isTreeUri(treeUri)) {
                treeDoc = DocumentsContract.getTreeDocumentId(treeUri);
            } else {
                treeDoc = DocumentsContract.getDocumentId(treeUri);
            }
        }
        if (!TextUtils.isEmpty(treeDoc)) {
            treeDoc = treeDoc.replaceAll(":", "/");
        }
        return treeDoc;
    }

    public static List<MediaBean> getIntentResultData(Intent resultDataIntent) {
        if (resultDataIntent == null) {
            return null;
        }
        List<MediaBean> list = resultDataIntent.getParcelableArrayListExtra(INTENT_SELECTED_MEDIA_LIST);
        return list;
    }

    public static void openDocumentTree(Activity activity, int requestCode) {
        // 启动文档树选择器请求U盘根目录
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void takePersistableUriPermission(Activity activity, Uri uri, boolean write) {
        int flag = Intent.FLAG_GRANT_READ_URI_PERMISSION;
        if (write) {
            flag = flag | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
        }
        activity.getContentResolver().takePersistableUriPermission(uri, flag);
    }

    // 拍照
    public static void capturePhoto(Activity activity, Uri outputUri, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        activity.startActivityForResult(intent, requestCode);
    }

    // 录像
    public static void captureVideo(Activity activity, Uri outputUri, int requestCode) {
        captureVideo(activity, outputUri, requestCode, 1);
    }

    // 录像
    public static void captureVideo(Activity activity, Uri outputUri, int requestCode, int quality) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, quality); // 视频质量（0-低，1-高）
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        activity.startActivityForResult(intent, requestCode);
    }

    public static File createMediaFile(Context context, String prefix, String directory, String extension) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = prefix + timeStamp + extension;
        File storageDir = context.getExternalFilesDir(directory); // 私有目录（Android 11+ 推荐）
        return new File(storageDir, fileName);
    }

    public static Uri createMediaUri(Context context, String prefix, String directory, String extension) {
        File file = createMediaFile(context, prefix, directory, extension);
        Uri uri = FileProvider.getUriForFile(context, InstallUtil.getFileProviderAuthority(context), file);
        return uri;
    }

    public static Uri createMediaUri(Context context, File file) {
        Uri uri = FileProvider.getUriForFile(context, InstallUtil.getFileProviderAuthority(context), file);
        return uri;
    }
}
