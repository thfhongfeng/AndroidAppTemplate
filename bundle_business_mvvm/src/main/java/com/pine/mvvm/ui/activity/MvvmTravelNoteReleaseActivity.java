package com.pine.mvvm.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.pine.base.BaseConstants;
import com.pine.base.access.UiAccessAction;
import com.pine.base.access.UiAccessType;
import com.pine.base.architecture.mvvm.ui.activity.BaseMvvmActionBarTextMenuActivity;
import com.pine.base.component.editor.bean.TextImageEntity;
import com.pine.base.component.uploader.FileUploadComponent;
import com.pine.base.component.uploader.bean.FileUploadBean;
import com.pine.base.component.uploader.bean.RemoteUploadFileInfo;
import com.pine.base.util.DialogUtils;
import com.pine.base.widget.dialog.DateSelectDialog;
import com.pine.base.widget.dialog.InputTextDialog;
import com.pine.config.ConfigKey;
import com.pine.mvvm.MvvmUrlConstants;
import com.pine.mvvm.R;
import com.pine.mvvm.bean.MvvmShopItemEntity;
import com.pine.mvvm.bean.MvvmTravelNoteDetailEntity;
import com.pine.mvvm.databinding.MvvmTravelNoteReleaseActivityBinding;
import com.pine.mvvm.vm.MvvmTravelNoteReleaseVm;
import com.pine.tool.access.UiAccessAnnotation;
import com.pine.tool.util.StringUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tanghongfeng on 2018/10/23
 */

@UiAccessAnnotation(AccessTypes = {UiAccessType.LOGIN, UiAccessType.CONFIG_SWITCHER},
        AccessArgs = {"", ConfigKey.FUN_ADD_TRAVEL_NOTE_KEY},
        AccessActions = {UiAccessAction.LOGIN_ACCESS_FALSE_ON_RESUME_NOT_GO_LOGIN,
                UiAccessAction.LOGIN_ACCESS_FALSE_ON_CREATE_NOT_FINISH_UI,
                UiAccessAction.CONFIG_SWITCHER_ACCESS_FALSE_ON_CREATE_SHOW_TOAST})
public class MvvmTravelNoteReleaseActivity extends
        BaseMvvmActionBarTextMenuActivity<MvvmTravelNoteReleaseActivityBinding, MvvmTravelNoteReleaseVm> {
    private final int REQUEST_CODE_SELECT_BELONG_SHOP = 1;
    private InputTextDialog mDayCountInputDialog;
    private DateSelectDialog mSetOutDateSelectDialog;
    private FileUploadComponent.OneByOneUploadAdapter mUploadAdapter = new FileUploadComponent.OneByOneUploadAdapter() {

        @Override
        public String getUploadUrl() {
            return MvvmUrlConstants.Upload_Single_File;
        }

        @Override
        public String getFileKey(FileUploadBean fileUploadBean) {
            // Test code begin
            return "file";
            // Test code end
        }

        @Override
        public Map<String, String> getUploadParam(FileUploadBean fileUploadBean) {
            HashMap<String, String> params = new HashMap<>();
            // Test code begin
            params.put("bizType", "10");
            params.put("orderNum", "100");
            params.put("descr", "");
            params.put("fileType", "1");
            // Test code end
            return params;
        }

        @Override
        public RemoteUploadFileInfo getRemoteFileInfoFromResponse(FileUploadBean fileUploadBean, JSONObject
                response) {
            // Test code begin
            if (response == null) {
                return null;
            }
            if (!response.optBoolean(BaseConstants.SUCCESS)) {
                return null;
            }
            JSONObject data = response.optJSONObject(BaseConstants.DATA);
            if (data == null) {
                return null;
            }
            RemoteUploadFileInfo fileInfo = new RemoteUploadFileInfo();
            fileInfo.setUrl(data.optString("fileUrl"));
            return fileInfo;
            // Test code end
        }
    };

    @Override
    public void observeInitLiveData(Bundle savedInstanceState) {
        mViewModel.getBelongShopListData().observe(this, new Observer<ArrayList<MvvmShopItemEntity>>() {
            @Override
            public void onChanged(@Nullable ArrayList<MvvmShopItemEntity> list) {
                String names = "";
                if (list != null) {
                    for (MvvmShopItemEntity entity : list) {
                        names += entity.getName() + ",";
                    }
                }
                if (!TextUtils.isEmpty(names) && names.lastIndexOf(",") == names.length() - 1) {
                    names = names.substring(0, names.length() - 1);
                }
                mBinding.belongShopTv.setText(names);
                mBinding.belongShopTv.setData(list);
            }
        });
        mViewModel.getNoteDetailData().observe(this, new Observer<MvvmTravelNoteDetailEntity>() {
            @Override
            public void onChanged(@Nullable MvvmTravelNoteDetailEntity mvvmTravelNoteDetailEntity) {
                mBinding.setNoteDetail(mvvmTravelNoteDetailEntity);
            }
        });
    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.mvvm_activity_travel_note_release;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mBinding.setPresenter(new Presenter());
        initView();
    }

    private void initView() {
        mBinding.swipeRefreshLayout.setColorSchemeResources(
                R.color.red,
                R.color.yellow,
                R.color.green
        );
        mBinding.swipeRefreshLayout.setEnabled(false);
    }

    @Override
    protected void setupActionBar(View actionbar, ImageView goBackIv, TextView titleTv, TextView menuBtnTv) {
        titleTv.setText(R.string.mvvm_note_release_title);
        menuBtnTv.setText(R.string.mvvm_note_release_confirm_menu);
        menuBtnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddNoteBtnClicked();
            }
        });
    }

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }

    @Override
    public void onDestroy() {
        if (mDayCountInputDialog != null && mDayCountInputDialog.isShowing()) {
            mDayCountInputDialog.dismiss();
        }
        if (mSetOutDateSelectDialog != null && mSetOutDateSelectDialog.isShowing()) {
            mSetOutDateSelectDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_BELONG_SHOP) {
            if (resultCode == RESULT_OK) {
                mViewModel.onBelongShopSelected(data);
            }
        }
    }

    private void onAddNoteBtnClicked() {
        mViewModel.addNote(mBinding.aevView.getSectionList());
    }

    @Override
    public void setLoadingUiVisibility(boolean processing) {
        if (mBinding.swipeRefreshLayout == null) {
            return;
        }
        mBinding.swipeRefreshLayout.setRefreshing(processing);
    }

    public class Presenter {
        public void onSetOutDateClick(View view) {
            if (mSetOutDateSelectDialog == null) {
                int year = Calendar.getInstance().get(Calendar.YEAR);
                mSetOutDateSelectDialog = DialogUtils.createDateSelectDialog(MvvmTravelNoteReleaseActivity.this,
                        year, year + 1, new DateSelectDialog.IDialogDateSelected() {
                            @Override
                            public void onSelected(Calendar calendar) {
                                mBinding.setOutDateTv
                                        .setText(new SimpleDateFormat("yyyy-MM-dd")
                                                .format(calendar.getTime()));
                            }
                        });
            }
            mSetOutDateSelectDialog.show();
        }

        public void onDayCountClick(View view) {
            if (mDayCountInputDialog == null) {
                mDayCountInputDialog = DialogUtils.createTextInputDialog(MvvmTravelNoteReleaseActivity.this,
                        getString(R.string.mvvm_shop_release_day_count_hint),
                        "", 3,
                        EditorInfo.TYPE_CLASS_NUMBER, new InputTextDialog.IActionClickListener() {

                            @Override
                            public boolean onSubmitClick(Dialog dialog, List<String> textList) {
                                if (textList != null && textList.size() > 0 &&
                                        !TextUtils.isEmpty(textList.get(0))) {
                                    mBinding.dayCountTv.setText(textList.get(0));
                                    int count = Integer.parseInt(textList.get(0));
                                    mBinding.dayCountTv.setData(count);
                                    List<String> titleList = null;
                                    if (count > 1) {
                                        titleList = new ArrayList<>();
                                        for (int i = 0; i < count; i++) {
                                            titleList.add(getString(R.string.mvvm_note_release_day_note_title, StringUtils.toChineseNumber(i + 1)));
                                        }
                                    }
                                    mBinding.aevView.setSectionCount(MvvmTravelNoteReleaseActivity.this,
                                            count, titleList, mUploadAdapter);
                                }
                                return false;
                            }

                            @Override
                            public boolean onCancelClick(Dialog dialog) {
                                return false;
                            }
                        });
            }
            mDayCountInputDialog.show();
        }

        public void onBelongShopClick(View view) {
            Intent intent = new Intent(MvvmTravelNoteReleaseActivity.this, MvvmShopSearchCheckActivity.class);
            intent.putParcelableArrayListExtra(MvvmShopSearchCheckActivity.REQUEST_CHECKED_LIST_KEY, mViewModel.getBelongShopListData().getValue());
            startActivityForResult(intent, REQUEST_CODE_SELECT_BELONG_SHOP);
        }

        public void onPreviewNoteClick(View view) {
            List<TextImageEntity> noteDayList = mBinding.aevView.getSectionList();
            if (noteDayList != null && noteDayList.size() > 0) {
                mBinding.notePreviewRl.setVisibility(View.VISIBLE);
                mBinding.notePreviewAdv.init(noteDayList);
            }
        }

        public void onPreviewContainerClick(View view) {
            mBinding.notePreviewRl.setVisibility(View.GONE);
        }
    }
}
