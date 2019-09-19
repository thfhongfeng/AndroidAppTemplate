package com.pine.base.component.uploader.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pine.base.R;
import com.pine.base.component.image_loader.ImageLoaderManager;
import com.pine.base.component.uploader.FileUploadComponent;
import com.pine.base.component.uploader.FileUploadComponent.OneByOneUploadAdapter;
import com.pine.base.component.uploader.IFileOneByOneUploader;
import com.pine.base.component.uploader.IFileUploaderConfig;
import com.pine.base.component.uploader.bean.FileUploadBean;
import com.pine.base.component.uploader.bean.FileUploadState;
import com.pine.base.recycle_view.BaseListViewHolder;
import com.pine.base.recycle_view.adapter.BaseNoPaginationListAdapter;
import com.pine.base.recycle_view.bean.BaseListAdapterItemEntity;
import com.pine.base.recycle_view.bean.BaseListAdapterItemProperty;
import com.pine.tool.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanghongfeng on 2018/11/1
 */

public class FileUploadRecycleView extends UploadFileRecyclerView implements IFileUploaderConfig, IFileOneByOneUploader {
    private final String TAG = LogUtils.makeLogTag(this.getClass());
    private UploadFileAdapter mUploadFileAdapter;
    // RecyclerView列数（一行可容纳image数量）
    protected int mColumnSize = 3;
    // 最大允许上传文件数
    protected int mMaxFileCount = 30;
    // 最大允许上传文件大小
    protected long mMaxFileSize = 1024 * 1024;

    public FileUploadRecycleView(Context context) {
        super(context);
    }

    public FileUploadRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        int defaultColumnSize = getResources().getDisplayMetrics().widthPixels / getResources().getDimensionPixelOffset(R.dimen.dp_106);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseFileUploadView);
        mMaxFileCount = typedArray.getInt(R.styleable.BaseFileUploadView_baseMaxFileCount, 10);
        mColumnSize = typedArray.getInt(R.styleable.BaseFileUploadView_baseColumnSize, defaultColumnSize);
        mMaxFileSize = typedArray.getInt(R.styleable.BaseFileUploadView_baseMaxFileSize, 1024 * 1024);
    }

    public FileUploadRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        int defaultColumnSize = getResources().getDisplayMetrics().widthPixels / getResources().getDimensionPixelOffset(R.dimen.dp_106);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseFileUploadView);
        mMaxFileCount = typedArray.getInt(R.styleable.BaseFileUploadView_baseMaxFileCount, 10);
        mColumnSize = typedArray.getInt(R.styleable.BaseFileUploadView_baseColumnSize, defaultColumnSize);
        mMaxFileSize = typedArray.getInt(R.styleable.BaseFileUploadView_baseMaxFileSize, 1024 * 1024);
    }

    @Override
    public void init(@NonNull Activity activity,
                     @NonNull FileUploadComponent.OneByOneUploadAdapter adapter, int requestCodeSelectFile) {
        mHelper.init(activity, adapter, requestCodeSelectFile);
        mHelper.setMaxFileCount(mMaxFileCount);
        mHelper.setMaxFileSize(mMaxFileSize);
    }

    public void init(@NonNull Activity activity) {
        init(activity, false);
    }

    public void init(@NonNull Activity activity, boolean canDelete) {
        init(activity, null, -1);

        mUploadFileAdapter = new UploadFileAdapter(
                UploadFileAdapter.UPLOAD_FILE_VIEW_HOLDER, false, canDelete, mMaxFileCount);
        addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelOffset(R.dimen.dp_10)));
        if (getUploadFileType() != FileUploadComponent.TYPE_IMAGE) {
            mColumnSize = 1;
        }
        GridLayoutManager layoutManager = new GridLayoutManager(activity, mColumnSize);
        setLayoutManager(layoutManager);
        setAdapter(mUploadFileAdapter);
        mUploadFileAdapter.setData(null);
        notifyAdapterDataChanged();
    }

    public void init(@NonNull Activity activity, boolean canDelete,
                     @NonNull OneByOneUploadAdapter adapter, int requestCodeSelectFile) {
        init(activity, adapter, requestCodeSelectFile);

        mUploadFileAdapter = new UploadFileAdapter(
                UploadFileAdapter.UPLOAD_FILE_VIEW_HOLDER, true, canDelete, mMaxFileCount);
        addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelOffset(R.dimen.dp_10)));
        if (getUploadFileType() != FileUploadComponent.TYPE_IMAGE) {
            mColumnSize = 1;
        }
        GridLayoutManager layoutManager = new GridLayoutManager(activity, mColumnSize);
        setLayoutManager(layoutManager);
        setAdapter(mUploadFileAdapter);
        mUploadFileAdapter.setData(null);
        notifyAdapterDataChanged();
    }

    @Override
    public int getUploadFileType() {
        return FileUploadComponent.TYPE_ALL;
    }

    @Override
    public int getValidFileCount() {
        ArrayList<String> list = getValidFileList();
        return list == null ? 0 : list.size();
    }

    @Override
    public void onFileUploadPrepare(List<FileUploadBean> uploadBeanList) {
        int startIndex = mUploadFileAdapter.getAdapterData().size();
        mUploadFileAdapter.addData(uploadBeanList);
        notifyAdapterItemRangeChanged(startIndex, mUploadFileAdapter.getAdapterData().size());
    }

    @Override
    public void onFileUploadProgress(FileUploadBean uploadBean) {
        notifyAdapterItemChanged(uploadBean.getOrderIndex());
    }

    @Override
    public void onFileUploadCancel(FileUploadBean uploadBean) {
        notifyAdapterItemChanged(uploadBean.getOrderIndex());
    }

    @Override
    public void onFileUploadFail(FileUploadBean uploadBean) {
        notifyAdapterItemChanged(uploadBean.getOrderIndex());
    }

    @Override
    public void onFileUploadSuccess(FileUploadBean uploadBean) {
        notifyAdapterItemChanged(uploadBean.getOrderIndex());
        notifyAdapterItemChanged(0);
    }

    public void setRemoteFiles(String remoteFiles, String joinStr) {
        if (TextUtils.isEmpty(remoteFiles)) {
            return;
        }
        setRemoteFiles(remoteFiles.split(joinStr));
    }

    public void setRemoteFileList(List<String> remoteFileList) {
        if (remoteFileList == null && remoteFileList.size() < 1) {
            return;
        }
        setRemoteFiles(remoteFileList.toArray(new String[0]));
    }

    public void setRemoteFiles(String[] remoteFileArr) {
        if (remoteFileArr == null && remoteFileArr.length < 1) {
            return;
        }
        List<FileUploadBean> uploadFileList = new ArrayList<>();
        FileUploadBean fileUploadBean = null;
        for (int i = 0; i < remoteFileArr.length; i++) {
            fileUploadBean = new FileUploadBean();
            fileUploadBean.setRemoteFilePath(remoteFileArr[i]);
            fileUploadBean.setOrderIndex(i);
            fileUploadBean.setUploadState(FileUploadState.UPLOAD_STATE_SUCCESS);
            uploadFileList.add(fileUploadBean);
        }
        if (uploadFileList.size() < 1) {
            return;
        }
        mUploadFileAdapter.setData(uploadFileList);
        notifyAdapterDataChanged();
    }

    private ArrayList<String> getFileShowList() {
        List<FileUploadState> states = new ArrayList<>();
        states.add(FileUploadState.UPLOAD_STATE_SUCCESS);
        states.add(FileUploadState.UPLOAD_STATE_UPLOADING);
        return mUploadFileAdapter.getFileList(states);
    }

    private ArrayList<String> getValidFileList() {
        List<FileUploadState> states = new ArrayList<>();
        states.add(FileUploadState.UPLOAD_STATE_SUCCESS);
        states.add(FileUploadState.UPLOAD_STATE_UPLOADING);
        return mUploadFileAdapter.getFileList(states);
    }

    private ArrayList<String> getValidFileLocalList() {
        List<FileUploadState> states = new ArrayList<>();
        states.add(FileUploadState.UPLOAD_STATE_SUCCESS);
        states.add(FileUploadState.UPLOAD_STATE_UPLOADING);
        return mUploadFileAdapter.getFileLocalList(states);
    }

    private ArrayList<String> getUploadedFileLocalList() {
        List<FileUploadState> states = new ArrayList<>();
        states.add(FileUploadState.UPLOAD_STATE_SUCCESS);
        return mUploadFileAdapter.getFileLocalList(states);
    }

    private ArrayList<String> getUploadingFileLocalList() {
        List<FileUploadState> states = new ArrayList<>();
        states.add(FileUploadState.UPLOAD_STATE_UPLOADING);
        return mUploadFileAdapter.getFileLocalList(states);
    }

    private ArrayList<String> getUploadedFileRemoteList() {
        List<FileUploadState> states = new ArrayList<>();
        states.add(FileUploadState.UPLOAD_STATE_SUCCESS);
        return mUploadFileAdapter.getFileRemoteList(states);
    }

    private String getUploadedFileRemoteString(String joinStr) {
        List<FileUploadState> states = new ArrayList<>();
        states.add(FileUploadState.UPLOAD_STATE_SUCCESS);
        return mUploadFileAdapter.getFileRemoteString(states, joinStr);
    }

    public List<String> getNewUploadFileRemoteList() {
        return mUploadFileAdapter.getNewUploadFileRemoteList();
    }

    public String getNewUploadFileRemoteString(String joinStr) {
        return mUploadFileAdapter.getNewUploadFileRemoteString(joinStr);
    }

    public class UploadFileAdapter extends BaseNoPaginationListAdapter<FileUploadBean> {
        public static final int UPLOAD_FILE_VIEW_HOLDER = 1;
        private boolean mCanUpload, mCanDelete;
        private int mMaxFileCount;

        public UploadFileAdapter(int defaultItemViewType, boolean canUpload,
                                 boolean canDelete, int maxFileCount) {
            super(defaultItemViewType);
            mCanUpload = canUpload;
            mCanDelete = canDelete;
            mMaxFileCount = maxFileCount;
        }

        @Override
        protected List<BaseListAdapterItemEntity<FileUploadBean>> parseData(List<FileUploadBean> data,
                                                                            boolean reset) {
            List<BaseListAdapterItemEntity<FileUploadBean>> adapterData = new ArrayList<>();
            BaseListAdapterItemEntity adapterEntity;
            if (mCanUpload && reset) {
                adapterEntity = new BaseListAdapterItemEntity();
                adapterEntity.getPropertyEntity().setItemViewType(getDefaultItemViewType());
                adapterData.add(adapterEntity);
            }
            if (data != null) {
                for (int i = 0; i < data.size(); i++) {
                    adapterEntity = new BaseListAdapterItemEntity();
                    adapterEntity.setData(data.get(i));
                    adapterEntity.getPropertyEntity().setItemViewType(getDefaultItemViewType());
                    adapterData.add(adapterEntity);
                }
            }
            return adapterData;
        }

        public ArrayList<String> getFileList(List<FileUploadState> states) {
            ArrayList<String> retList = new ArrayList<>();
            for (int i = 0; i < mData.size(); i++) {
                FileUploadBean bean = mData.get(i).getData();
                if (bean != null && states.contains(bean.getUploadState())) {
                    if (!TextUtils.isEmpty(bean.getLocalFilePath())) {
                        retList.add(bean.getLocalFilePath());
                    } else if (!TextUtils.isEmpty(bean.getRemoteFilePath())) {
                        retList.add(bean.getRemoteFilePath());
                    }
                }
            }
            return retList;
        }

        public ArrayList<String> getFileLocalList(List<FileUploadState> states) {
            ArrayList<String> retList = new ArrayList<>();
            for (int i = 0; i < mData.size(); i++) {
                FileUploadBean bean = mData.get(i).getData();
                if (bean != null && !TextUtils.isEmpty(bean.getLocalFilePath()) &&
                        states.contains(bean.getUploadState())) {
                    retList.add(bean.getLocalFilePath());
                }
            }
            return retList;
        }

        public String getFileLocalString(List<FileUploadState> states, String joinStr) {
            List<String> list = getFileLocalList(states);
            return listJoinToString(list, joinStr);
        }

        public ArrayList<String> getFileRemoteList(List<FileUploadState> states) {
            ArrayList<String> retList = new ArrayList<>();
            for (int i = 0; i < mData.size(); i++) {
                FileUploadBean bean = mData.get(i).getData();
                if (bean != null && !TextUtils.isEmpty(bean.getRemoteFilePath()) &&
                        states.contains(bean.getUploadState())) {
                    retList.add(bean.getRemoteFilePath());
                }
            }
            return retList;
        }

        public String getFileRemoteString(List<FileUploadState> states, String joinStr) {
            List<String> list = getFileRemoteList(states);
            return listJoinToString(list, joinStr);
        }

        public ArrayList<String> getNewUploadFileRemoteList() {
            ArrayList<String> retList = new ArrayList<>();
            for (int i = 0; i < mData.size(); i++) {
                FileUploadBean bean = mData.get(i).getData();
                if (bean != null && bean.getUploadState() == FileUploadState.UPLOAD_STATE_SUCCESS &&
                        !TextUtils.isEmpty(bean.getRemoteFilePath()) && !TextUtils.isEmpty(bean.getLocalFilePath())) {
                    retList.add(bean.getRemoteFilePath());
                }
            }
            return retList;
        }

        public String getNewUploadFileRemoteString(String joinStr) {
            List<String> list = getNewUploadFileRemoteList();
            return listJoinToString(list, joinStr);
        }

        private String listJoinToString(List<String> list, String joinStr) {
            if (list.size() < 1) {
                return "";
            }
            String reStr = list.get(0);
            for (int i = 1; i < list.size(); i++) {
                reStr += joinStr + list.get(i);
            }
            return reStr;
        }

        @Override
        public BaseListViewHolder getViewHolder(ViewGroup parent, int viewType) {
            BaseListViewHolder viewHolder = null;
            switch (viewType) {
                case UPLOAD_FILE_VIEW_HOLDER:
                    if (getUploadFileType() == FileUploadComponent.TYPE_IMAGE) {
                        viewHolder = new UploadFileViewHolder(parent.getContext(), LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.base_item_upload_image, parent, false));
                    } else {
                        viewHolder = new UploadFileViewHolder(parent.getContext(), LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.base_item_upload_file, parent, false));
                    }
                    break;
            }
            return viewHolder;
        }

        public class UploadFileViewHolder extends BaseListViewHolder<FileUploadBean> {
            private Context context;
            private LinearLayout show_container;
            private RelativeLayout state_rl;
            private ImageView show_iv, delete_iv, loading_iv;
            private TextView file_desc_tv, num_max_tv, fail_tv, loading_tv;


            public UploadFileViewHolder(Context context, View itemView) {
                super(itemView);
                this.context = context;
                show_container = itemView.findViewById(R.id.show_container);
                state_rl = itemView.findViewById(R.id.state_rl);
                show_iv = itemView.findViewById(R.id.show_iv);
                file_desc_tv = itemView.findViewById(R.id.file_desc_tv);
                delete_iv = itemView.findViewById(R.id.delete_iv);
                loading_iv = itemView.findViewById(R.id.loading_iv);
                loading_tv = itemView.findViewById(R.id.loading_tv);
                num_max_tv = itemView.findViewById(R.id.num_max_tv);
                fail_tv = itemView.findViewById(R.id.fail_tv);
            }

            @Override
            public void updateData(final FileUploadBean fileBean,
                                   BaseListAdapterItemProperty propertyEntity, final int position) {
                if (position < mData.size() && (mCanUpload && position > 0 || !mCanUpload)) {
                    fileBean.setOrderIndex(position);
                    String imageUrl = fileBean.getLocalFilePath();
                    if (!TextUtils.isEmpty(imageUrl)) {
                        imageUrl = "file://" + imageUrl;
                    } else if (!TextUtils.isEmpty(fileBean.getRemoteFilePath())) {
                        imageUrl = fileBean.getRemoteFilePath();
                    }
                    if (getUploadFileType() == FileUploadComponent.TYPE_IMAGE) {
                        ImageLoaderManager.getInstance().loadImage(context, imageUrl, show_iv);
                    } else {
                        ImageLoaderManager.getInstance().loadImage(context, R.mipmap.base_iv_file, show_iv);
                        if (file_desc_tv != null) {
                            file_desc_tv.setText(imageUrl);
                        }
                    }
                    loading_iv.setVisibility(View.GONE);
                    loading_tv.setVisibility(GONE);
                    fail_tv.setVisibility(View.GONE);
                    switch (fileBean.getUploadState()) {
                        case UPLOAD_STATE_PREPARING:
                        case UPLOAD_STATE_UPLOADING:
                            state_rl.setVisibility(View.VISIBLE);
                            loading_iv.setVisibility(View.VISIBLE);
                            loading_tv.setVisibility(VISIBLE);
                            loading_tv.setText(fileBean.getUploadProgress() + "%");
                            break;
                        case UPLOAD_STATE_CANCEL:
                            state_rl.setVisibility(View.VISIBLE);
                            fail_tv.setVisibility(View.VISIBLE);
                            break;
                        case UPLOAD_STATE_FAIL:
                            state_rl.setVisibility(View.VISIBLE);
                            fail_tv.setVisibility(View.VISIBLE);
                            break;
                        case UPLOAD_STATE_SUCCESS:
                            state_rl.setVisibility(View.GONE);
                            break;
                        default:
                            state_rl.setVisibility(View.GONE);
                            break;
                    }
                    num_max_tv.setText("");
                    num_max_tv.setVisibility(View.GONE);
                    show_container.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArrayList<String> showList = getFileShowList();
                            if (showList == null || showList.size() < 1) {
                                return;
                            }
                            String curPath = fileBean.getLocalFilePath();
                            if (TextUtils.isEmpty(curPath)) {
                                curPath = fileBean.getRemoteFilePath();
                            }
                            int curPos = -1;
                            for (int i = 0; i < showList.size(); i++) {
                                String path = showList.get(i);
                                if (TextUtils.isEmpty(path)) {
                                    path = fileBean.getRemoteFilePath();
                                }
                                if (curPath != null && curPath.equals(path)) {
                                    curPos = i;
                                    break;
                                }
                            }
                            mHelper.displayUploadObject(showList, curPos);
                        }
                    });
                    if (mCanDelete) {
                        delete_iv.setVisibility(View.VISIBLE);
                        delete_iv.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mData.remove(position);
                                mHelper.getFileUploadComponent().cancel(fileBean);
                                notifyAdapterDataChanged();
                            }
                        });
                    } else {
                        delete_iv.setVisibility(View.GONE);
                    }
                } else if (mCanUpload) {
                    show_iv.setImageResource(R.mipmap.base_iv_add_upload_image);// 第一个显示加号图片
                    int successSize = getUploadedFileRemoteList().size();
                    if (getUploadFileType() == FileUploadComponent.TYPE_IMAGE) {
                        num_max_tv.setText(successSize + "/" + mMaxFileCount);
                        num_max_tv.setVisibility(VISIBLE);
                    } else {
                        if (file_desc_tv != null) {
                            file_desc_tv.setText(successSize + "/" + mMaxFileCount);
                        }
                    }
                    show_container.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mHelper.selectUploadObjects();
                        }
                    });
                    state_rl.setVisibility(View.GONE);
                    delete_iv.setVisibility(View.GONE);
                }
            }
        }
    }

    protected void notifyAdapterDataChanged() {
        mUploadFileAdapter.notifyDataSetChanged();
    }

    protected void notifyAdapterItemChanged(int index) {
        mUploadFileAdapter.notifyItemChanged(index);
    }

    protected void notifyAdapterItemRangeChanged(int start, int end) {
        mUploadFileAdapter.notifyItemRangeChanged(start, end);
    }

    class SpaceItemDecoration extends ItemDecoration {
        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (parent.getChildAdapterPosition(view) % mColumnSize == 0) {
                outRect.left = space;
            }
            if (parent.getChildAdapterPosition(view) < mColumnSize) {
                outRect.top = space;
            }
            outRect.right = space;
            outRect.bottom = space;
        }
    }
}
