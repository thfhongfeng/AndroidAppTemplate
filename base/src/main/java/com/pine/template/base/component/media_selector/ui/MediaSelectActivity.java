package com.pine.template.base.component.media_selector.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pine.template.base.R;
import com.pine.template.base.component.image_loader.ImageLoaderManager;
import com.pine.template.base.component.media_selector.MediaSelector;
import com.pine.template.base.component.media_selector.OnBackPressedListener;
import com.pine.template.base.component.media_selector.bean.MediaBean;
import com.pine.template.base.component.media_selector.bean.MediaFolderBean;
import com.pine.template.base.component.media_selector.bean.MediaItemBean;
import com.pine.template.base.recycle_view.BaseListViewHolder;
import com.pine.template.base.recycle_view.adapter.BaseListAdapter;
import com.pine.template.base.recycle_view.adapter.BaseNoPaginationListAdapter;
import com.pine.template.base.recycle_view.bean.BaseListAdapterItemProperty;
import com.pine.template.base.ui.BaseFullScreenActivity;
import com.pine.tool.permission.PermissionsAnnotation;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.InstallUtil;
import com.pine.tool.util.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@PermissionsAnnotation(Permissions = {Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE})
public class MediaSelectActivity extends BaseFullScreenActivity {
    private final String TAG = LogUtils.makeLogTag(MediaSelectActivity.class);
    private LinearLayout folder_container;
    private RecyclerView media_rv, folder_rv;
    private Button folder_select_btn;
    private ImageView go_back_iv;
    private TextView title_tv, menu_btn, banner_root_dir_tv;
    private Uri mTreeUri;
    private int mMaxMediaCount = Integer.MAX_VALUE;
    private int[] mSelectType = null;
    private LinkedHashMap<String, MediaBean> mIntentSelectedMediaMap = new LinkedHashMap<>();
    private boolean mIsReselect = false;
    private boolean mCanCameraPic = false;
    private boolean mCanCameraVideo = false;
    // 0-不排序；1-DESC；2-ASC
    private int mOrder = 0;
    private File mCameraPicFile = null;
    private File mCameraVideoFile = null;
    private FolderAdapter mFolderAdapter;
    private MediaFolderBean mAllMediaFolder, mCurrentMediaFolder;
    private MediaAdapter mMediaAdapter;
    private LinkedHashMap<String, MediaBean> mSelectedMediaMap = new LinkedHashMap<>();
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private ArrayList<MediaFolderBean> mDirPaths = new ArrayList<>();

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.base_activity_media_select;
    }

    @Override
    protected void findViewOnCreate(Bundle savedInstanceState) {
        go_back_iv = findViewById(R.id.go_back_iv);
        title_tv = findViewById(R.id.title_tv);
        menu_btn = findViewById(R.id.menu_btn);
        media_rv = findViewById(R.id.media_rv);
        folder_container = findViewById(R.id.folder_container);
        banner_root_dir_tv = findViewById(R.id.banner_root_dir_tv);
        folder_select_btn = findViewById(R.id.folder_select_btn);
        folder_rv = findViewById(R.id.folder_rv);
    }

    @Override
    protected boolean parseIntentData() {
        if (getIntent().hasExtra(MediaSelector.INTENT_SELECTED_TREE_URI)) {
            mTreeUri = getIntent().getParcelableExtra(MediaSelector.INTENT_SELECTED_TREE_URI);
        }
        if (getIntent().hasExtra(MediaSelector.INTENT_MAX_SELECTED_COUNT)) {
            mMaxMediaCount = getIntent().getIntExtra(MediaSelector.INTENT_MAX_SELECTED_COUNT, Integer.MAX_VALUE);
        }
        if (getIntent().hasExtra(MediaSelector.INTENT_SELECTED_MEDIA_TYPE)) {
            mSelectType = getIntent().getIntArrayExtra(MediaSelector.INTENT_SELECTED_MEDIA_TYPE);
        }
        if (getIntent().hasExtra(MediaSelector.INTENT_SELECTED_MEDIA_LIST)) {
            List<MediaBean> list = getIntent().getParcelableExtra(MediaSelector.INTENT_SELECTED_MEDIA_LIST);
            if (list != null) {
                for (MediaBean mediaBean : list) {
                    mIntentSelectedMediaMap.put(mediaBean.getUrl(), mediaBean);
                }
            }
        }
        if (getIntent().hasExtra(MediaSelector.INTENT_IS_RESELECT)) {
            mIsReselect = getIntent().getBooleanExtra(MediaSelector.INTENT_IS_RESELECT, false);
        }
        if (getIntent().hasExtra(MediaSelector.INTENT_CAN_CAMERA_PIC)) {
            mCanCameraPic = getIntent().getBooleanExtra(MediaSelector.INTENT_CAN_CAMERA_PIC, false);
        }
        if (getIntent().hasExtra(MediaSelector.INTENT_CAN_CAMERA_VIDEO)) {
            mCanCameraVideo = getIntent().getBooleanExtra(MediaSelector.INTENT_CAN_CAMERA_VIDEO, false);
        }
        if (getIntent().hasExtra(MediaSelector.INTENT_CAMERA_PIC_FILE)) {
            mCameraPicFile = getIntent().getParcelableExtra(MediaSelector.INTENT_CAMERA_PIC_FILE);
        }
        if (getIntent().hasExtra(MediaSelector.INTENT_CAMERA_VIDEO_FILE)) {
            mCameraVideoFile = getIntent().getParcelableExtra(MediaSelector.INTENT_CAMERA_VIDEO_FILE);
        }
        if (getIntent().hasExtra(MediaSelector.INTENT_DATE_ORDER)) {
            mOrder = getIntent().getIntExtra(MediaSelector.INTENT_DATE_ORDER, 0);
        }
        return false;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        go_back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        title_tv.setText(R.string.base_media_select);
        setupMenuTv(0);
        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goComplete();
            }
        });

        mAllMediaFolder = new MediaFolderBean();
        mAllMediaFolder.setDir(getString(R.string.base_all_media));
        mAllMediaFolder.setRelativeParent(getString(R.string.base_all_media));
        mDirPaths.add(mAllMediaFolder);

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(realDisplayMetrics);

        mMediaAdapter = new MediaAdapter();
        int mediaColumn = AppUtils.isLandScreen() ? 8 : 4;
        if (realDisplayMetrics.widthPixels > 0) {
            mediaColumn = realDisplayMetrics.widthPixels / getResources().getDimensionPixelOffset(R.dimen.dp_80);
        }
        GridLayoutManager mediaLm = new GridLayoutManager(this, mediaColumn);
        media_rv.setLayoutManager(mediaLm);
        media_rv.setHasFixedSize(true);
        mMediaAdapter.enableEmptyComplete(true, false);
        media_rv.setAdapter(mMediaAdapter);

        mFolderAdapter = new FolderAdapter();
        int folderColumn = AppUtils.isLandScreen() ? 2 : 1;
        GridLayoutManager folderLm = new GridLayoutManager(this, folderColumn);
        folder_rv.setLayoutManager(folderLm);
        folder_rv.setHasFixedSize(true);
        mFolderAdapter.enableEmptyComplete(true, false);
        folder_rv.setAdapter(mFolderAdapter);
        mFolderAdapter.setOnItemClickListener(new BaseListAdapter.IOnItemClickListener<MediaFolderBean>() {
            @Override
            public void onItemClick(View view, int position, String tag, MediaFolderBean customData) {
                setupFolder(customData);
            }
        });
        folder_select_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (folder_container.getVisibility() == View.VISIBLE) {
                    hideFolderListAnimation();
                } else {
                    folder_container.setVisibility(View.VISIBLE);
                    showFolderListAnimation();
                    mFolderAdapter.notifyDataSetChanged();
                }
            }
        });
        mStopScan = false;
        startScanDirectory();
    }

    @Override
    protected void afterInit() {

    }

    private void setupFolder(MediaFolderBean folderBean) {
        mCurrentMediaFolder = folderBean;
        List<MediaItemBean> list = new ArrayList<>();
        for (int type : mSelectType) {
            switch (type) {
                case MediaBean.TYPE_IMAGE:
                    if (mCanCameraPic) {
                        list.add(MediaItemBean.buildCameraPicTake());
                    }
                    break;
                case MediaBean.TYPE_VIDEO:
                    if (mCanCameraVideo) {
                        list.add(MediaItemBean.buildCameraVideoTake());
                    }
                    break;
            }
        }
        if (mCurrentMediaFolder == null) {
            folder_select_btn.setVisibility(View.GONE);
        } else {
            folder_select_btn.setVisibility(View.VISIBLE);
            list.addAll(mCurrentMediaFolder.medias);
            folder_select_btn.setText(mCurrentMediaFolder.getName());
        }

        mMediaAdapter.setData(list);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mStopScan = true;
        mScanMainHandler.removeCallbacksAndMessages(null);
        mScanThread = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        List<Fragment> list = getSupportFragmentManager().getFragments();
        boolean isHandled = false;
        for (int i = list.size() - 1; i >= 0; i--) {
            Fragment f = list.get(i);
            if (f != null && f instanceof OnBackPressedListener && f.isVisible()) {
                ((OnBackPressedListener) f).onBackPressed();
                isHandled = true;
                break; // only check the first not null (TOP fragment) and jump out
            }
        }
        if (!isHandled) {
            super.onBackPressed();
        }
    }

    private void goComplete() {
        Intent data = new Intent();
        ArrayList<MediaBean> list = new ArrayList<>();
        for (String key : mSelectedMediaMap.keySet()) {
            list.add(mSelectedMediaMap.get(key));
        }
        data.putParcelableArrayListExtra(MediaSelector.INTENT_SELECTED_MEDIA_LIST, list);
        data.putExtra(MediaSelector.INTENT_IS_RESELECT, mIsReselect);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private Thread mScanThread;
    private boolean mStopScan;
    private Handler mScanMainHandler = new Handler(Looper.getMainLooper());

    public void startScanDirectory() {
        if (mStopScan) {
            return;
        }
        if (mScanThread == null || !mScanThread.isAlive()) {
            HashMap<String, MediaBean> selectedMap = new HashMap<>();
            if (mIntentSelectedMediaMap != null) {
                for (String key : mIntentSelectedMediaMap.keySet()) {
                    MediaBean mediaBean = mIntentSelectedMediaMap.get(key);
                    selectedMap.put(mediaBean.getUrl(), mediaBean);
                }
            }
            mScanThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (mStopScan) {
                        return;
                    }
                    scanDirectory(selectedMap);
                }
            });
            // 开始扫描目录
            mScanThread.start();
        }
    }

    private void scanDirectory(HashMap<String, MediaBean> selectedMap) {
        if (mSelectType == null || mSelectType.length < 1) {
            return;
        }
        String treeDoc = MediaSelector.getTreeUriDocPath(mTreeUri);
        String[] projection = {MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.RELATIVE_PATH,
                MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns._ID};
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " IN (";
        for (int i = 0; i < mSelectType.length; i++) {
            switch (mSelectType[i]) {
                case MediaBean.TYPE_IMAGE:
                    selection += MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
                    break;
                case MediaBean.TYPE_VIDEO:
                    selection += MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
                    break;
            }
            selection += ",";
        }
        selection = selection.substring(0, selection.length() - 1);
        selection += ")";
        String[] selectionArgs = null;
        String likeStr = treeDoc;
        if (!TextUtils.isEmpty(likeStr)) {
            if (likeStr.startsWith("primary") || !likeStr.contains("-")) { // 内部存储sdcard
                int index = treeDoc.indexOf("/");
                if (index > 0) {
                    likeStr = likeStr.substring(index);
                }
                if (!likeStr.endsWith("/")) {
                    likeStr = likeStr + "/";
                }
                if (!likeStr.startsWith("/")) {
                    likeStr = "/" + likeStr;
                }
                selection += " AND " + MediaStore.Files.FileColumns.DATA + " LIKE ?";
                selectionArgs = new String[]{"%/emulated/%" + likeStr + "%"};
            } else {    // U盘
                if (!likeStr.endsWith("/")) {
                    likeStr = likeStr + "/";
                }
                if (!likeStr.startsWith("/")) {
                    likeStr = "/" + likeStr;
                }
                selection += " AND " + MediaStore.Files.FileColumns.DATA + " LIKE ?";
                selectionArgs = new String[]{"%" + likeStr + "%"};
            }
        }
        LogUtils.d(TAG, "scanDirectory selection:" + selection + " => likeStr:" + likeStr + ", treeDoc:" + treeDoc);
        String order = null;
        switch (mOrder) {
            case 1:
                order = MediaStore.MediaColumns.DATE_ADDED + " DESC";
                break;
            case 2:
                order = MediaStore.MediaColumns.DATE_ADDED + " ASC";
                break;
        }
        Cursor cursor = getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                projection, selection, selectionArgs, order);
        if (cursor != null && cursor.moveToFirst()) {
            HashMap<String, MediaFolderBean> tmpDir = new HashMap();
            HashMap<String, MediaItemBean> mediaMap = new HashMap<>();
            do {
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                int mediaTypeColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);
                int idColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
                int parentColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.RELATIVE_PATH);
                String filePath = cursor.getString(dataColumnIndex);
                String parent = cursor.getString(parentColumnIndex);
                int mediaType = cursor.getInt(mediaTypeColumnIndex);
                int id = cursor.getInt(idColumnIndex);
                Bitmap thumbnail = getThumbnail(id, mediaType);
                MediaBean bean = null;
                if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                    bean = MediaBean.buildImageBean(filePath);
                } else if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                    bean = MediaBean.buildVideoBean(filePath);
                }
                if (bean != null) {
                    bean.setStoreId(String.valueOf(id));
                    MediaItemBean item = new MediaItemBean();
                    item.setMediaBean(bean);
                    item.setThumbnail(thumbnail);
                    item.setSelected(selectedMap.containsKey(filePath));
                    mediaMap.put(filePath, item);

                    // 获取该图片的父路径名
                    File parentFile = new File(filePath).getParentFile();
                    if (parentFile != null) {
                        MediaFolderBean folder = null;
                        String dirPath = parentFile.getAbsolutePath();
                        if (!tmpDir.containsKey(dirPath)) {
                            folder = new MediaFolderBean();
                            folder.setDir(dirPath);
                            folder.setRelativeParent(parent);
                            folder.setFirstMediaPath(filePath);
                            tmpDir.put(dirPath, folder);
                        } else {
                            folder = tmpDir.get(dirPath);
                        }
                        folder.medias.add(item);
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
            LogUtils.d(TAG, "scanDirectory mediaMap count:" + mediaMap.size());
            final String finalTreeDoc = treeDoc;
            mScanMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (String key : mediaMap.keySet()) {
                        MediaItemBean itemBean = mediaMap.get(key);
                        mAllMediaFolder.medias.add(itemBean);
                    }
                    for (String key : mIntentSelectedMediaMap.keySet()) {
                        if (mediaMap.containsKey(key)) {
                            mSelectedMediaMap.put(key, mediaMap.get(key).getMediaBean());
                        }
                    }
                    for (String key : tmpDir.keySet()) {
                        MediaFolderBean folderBean = tmpDir.get(key);
                        mDirPaths.add(folderBean);
                        LogUtils.d(TAG, folderBean.getName() + "---" + folderBean.medias.size());
                    }
                    mFolderAdapter.setData(mDirPaths);
                    banner_root_dir_tv.setText(getString(R.string.base_media_root_dir, finalTreeDoc));
                    setupFolder(mDirPaths.size() > 0 ? mDirPaths.get(0) : null);
                }
            });
        }
    }

    private Bitmap getThumbnail(int id, int mediaType) {
        if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            return MediaStore.Images.Thumbnails.getThumbnail(
                    getContentResolver(),
                    id,
                    MediaStore.Images.Thumbnails.MICRO_KIND,
                    null
            );
        } else if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
            return MediaStore.Video.Thumbnails.getThumbnail(
                    getContentResolver(),
                    id,
                    MediaStore.Video.Thumbnails.MICRO_KIND,
                    null
            );
        }
        return null;
    }

    private static final int REQUEST_IMAGE_CAPTURE = 520;
    private static final int REQUEST_VIDEO_CAPTURE = 521;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.d(TAG, "onActivityResult requestCode:" + requestCode + ", resultCode:" + resultCode);
        if (requestCode == REQUEST_IMAGE_CAPTURE && mCameraPicFile != null && mCameraPicFile.exists()) {
            if (resultCode == Activity.RESULT_OK) {
                MediaBean mediaBean = MediaBean.buildImageBean(mCameraPicFile.getAbsolutePath());
                mSelectedMediaMap.put(mCameraPicFile.getAbsolutePath(), mediaBean);
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(mCameraPicFile);
                mediaScanIntent.setData(contentUri);
                sendBroadcast(mediaScanIntent);
                goComplete();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                new File(mCameraPicFile.getPath()).delete();
            }
        } else if (requestCode == REQUEST_VIDEO_CAPTURE && mCameraVideoFile != null && mCameraVideoFile.exists()) {
            if (resultCode == Activity.RESULT_OK) {
                MediaBean mediaBean = MediaBean.buildVideoBean(mCameraVideoFile.getAbsolutePath());
                mSelectedMediaMap.put(mCameraVideoFile.getAbsolutePath(), mediaBean);
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(mCameraVideoFile);
                mediaScanIntent.setData(contentUri);
                sendBroadcast(mediaScanIntent);
                goComplete();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                new File(mCameraVideoFile.getPath()).delete();
            }
        }
    }

    // 拍照
    private void capturePhoto() {
        if (mSelectedMediaMap.size() + 1 > mMaxMediaCount) {
            Toast.makeText(MediaSelectActivity.this,
                    getString(R.string.base_media_selected_exceeding_msg, mMaxMediaCount), Toast.LENGTH_SHORT).show();
            return;
        }

        if (mCameraPicFile == null) {
            mCameraPicFile = MediaSelector.createMediaFile(this, "IMG_", Environment.DIRECTORY_PICTURES, ".jpg");
        }
        Uri uri = FileProvider.getUriForFile(this, InstallUtil.getFileProviderAuthority(this), mCameraPicFile);
        MediaSelector.capturePhoto(this, uri, REQUEST_IMAGE_CAPTURE);
    }

    // 录像
    private void captureVideo() {
        if (mSelectedMediaMap.size() + 1 > mMaxMediaCount) {
            Toast.makeText(MediaSelectActivity.this,
                    getString(R.string.base_media_selected_exceeding_msg, mMaxMediaCount), Toast.LENGTH_SHORT).show();
            return;
        }
        if (mCameraVideoFile == null) {
            mCameraVideoFile = MediaSelector.createMediaFile(this, "VID_", Environment.DIRECTORY_MOVIES, ".mp4");
        }
        Uri uri = FileProvider.getUriForFile(this, InstallUtil.getFileProviderAuthority(this), mCameraVideoFile);
        MediaSelector.captureVideo(this, uri, REQUEST_VIDEO_CAPTURE);
    }

    public void showFolderListAnimation() {
        TranslateAnimation ta = new TranslateAnimation(1, 0f, 1,
                0f, 1, 1f, 1, 0f);
        ta.setDuration(200);
        folder_container.startAnimation(ta);
    }

    public void hideFolderListAnimation() {
        TranslateAnimation ta = new TranslateAnimation(1, 0f, 1,
                0f, 1, 0f, 1, 1f);
        ta.setDuration(200);
        folder_container.startAnimation(ta);
        ta.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                folder_container.setVisibility(View.GONE);
            }
        });
    }

    private void setupMenuTv(int selected) {
        if (mMaxMediaCount != Integer.MAX_VALUE) {
            menu_btn.setText(getString(R.string.base_done) + selected + "/" + mMaxMediaCount);
        } else {
            if (selected > 0) {
                menu_btn.setText(getString(R.string.base_done) + "(" + selected + ")");
            } else {
                menu_btn.setText(getString(R.string.base_done));
            }
        }
    }

    class MediaAdapter extends BaseNoPaginationListAdapter<MediaItemBean> {
        @Override
        public BaseListViewHolder getViewHolder(ViewGroup parent, int viewType) {
            BaseListViewHolder viewHolder = new ViewHolder(parent.getContext(),
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.base_item_media_select, parent, false));
            return viewHolder;
        }

        class ViewHolder extends BaseListViewHolder<MediaItemBean> {
            private Context mContext;
            private ImageView iv;
            private Button checkBox;

            public ViewHolder(Context context, View itemView) {
                super(itemView);
                mContext = context;
                iv = itemView.findViewById(R.id.iv);
                checkBox = itemView.findViewById(R.id.check);
            }

            @Override
            public void updateData(final MediaItemBean content, BaseListAdapterItemProperty propertyEntity, int position) {
                if (content.isCameraPicTake()) {
                    checkBox.setVisibility(View.GONE);
                    iv.setImageResource(R.mipmap.base_iv_camera_pic);
                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            capturePhoto();
                        }
                    });
                } else if (content.isCameraVideoTake()) {
                    checkBox.setVisibility(View.GONE);
                    iv.setImageResource(R.mipmap.base_iv_camera_video);
                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            captureVideo();
                        }
                    });
                } else {
                    checkBox.setVisibility(View.VISIBLE);
                    iv.setImageBitmap(content.getThumbnail());
                    checkBox.setSelected(content.isSelected());
                    checkSelected(iv, content.getMediaBean());
                    checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!v.isSelected() && mSelectedMediaMap.size() + 1 > mMaxMediaCount) {
                                Toast.makeText(MediaSelectActivity.this,
                                        getString(R.string.base_media_selected_exceeding_msg, mMaxMediaCount), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            v.setSelected(!v.isSelected());
                            content.setSelected(v.isSelected());
                            if (content.isSelected()) {
                                mSelectedMediaMap.put(content.getMediaBean().getUrl(), content.getMediaBean());
                            } else {
                                mSelectedMediaMap.remove(content.getMediaBean().getUrl());
                            }
                            checkSelected(iv, content.getMediaBean());
                        }
                    });
                }
            }

            private void checkSelected(ImageView iv, @NonNull MediaBean mediaBean) {
                if (mSelectedMediaMap.containsKey(mediaBean.getUrl())) {
                    iv.setColorFilter(null);
                } else {
                    iv.setColorFilter(Color.parseColor("#77000000"));
                }
                if (mSelectedMediaMap.size() == 0) {
                    menu_btn.setEnabled(false);
                    setupMenuTv(0);
                } else {
                    menu_btn.setEnabled(true);
                    setupMenuTv(mSelectedMediaMap.size());
                }
            }
        }
    }

    class FolderAdapter extends BaseNoPaginationListAdapter<MediaFolderBean> {
        @Override
        public BaseListViewHolder getViewHolder(ViewGroup parent, int viewType) {
            BaseListViewHolder viewHolder = new ViewHolder(parent.getContext(),
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.base_item_folder, parent, false));
            return viewHolder;
        }

        class ViewHolder extends BaseListViewHolder<MediaFolderBean> {
            private Context mContext;
            private ImageView image_iv, choose_iv;
            private TextView name_tv, count_tv;
            private View itemView;

            public ViewHolder(Context context, View itemView) {
                super(itemView);
                mContext = context;
                this.itemView = itemView;
                image_iv = itemView.findViewById(R.id.image_iv);
                name_tv = itemView.findViewById(R.id.name_tv);
                count_tv = itemView.findViewById(R.id.count_tv);
                choose_iv = itemView.findViewById(R.id.choose_iv);
            }

            @Override
            public void updateData(final MediaFolderBean content, BaseListAdapterItemProperty propertyEntity, int position) {
                if (position == 0 && TextUtils.isEmpty(content.getFirstMediaPath())) {
                    ImageLoaderManager.getInstance().loadImage(MediaSelectActivity.this, R.mipmap.base_ic_all_image, image_iv);
                } else {
                    ImageLoaderManager.getInstance().loadImage(MediaSelectActivity.this, content.getFirstMediaPath(), image_iv);
                }
                count_tv.setText(content.medias.size() + getString(R.string.base_unit_zhang));
                name_tv.setText(content.getRelativeParent());
                choose_iv.setVisibility(mCurrentMediaFolder == content ? View.VISIBLE : View.GONE);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.onItemClick(itemView, position, "root", content);
                    }
                });
            }
        }
    }
}
