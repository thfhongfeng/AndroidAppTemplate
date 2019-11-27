package com.pine.mvp.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pine.base.access.UiAccessAction;
import com.pine.base.access.UiAccessType;
import com.pine.base.architecture.mvp.ui.activity.BaseMvpActionBarTextMenuActivity;
import com.pine.base.component.editor.bean.TextImageEntity;
import com.pine.base.component.editor.ui.ArticleDisplayView;
import com.pine.base.component.editor.ui.ArticleEditorView;
import com.pine.base.util.DialogUtils;
import com.pine.base.widget.dialog.DateSelectDialog;
import com.pine.base.widget.dialog.InputTextDialog;
import com.pine.mvp.R;
import com.pine.mvp.bean.MvpShopItemEntity;
import com.pine.mvp.contract.IMvpTravelNoteReleaseContract;
import com.pine.mvp.presenter.MvpTravelNoteReleasePresenter;
import com.pine.tool.access.UiAccessAnnotation;
import com.pine.tool.bean.InputParam;
import com.pine.tool.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by tanghongfeng on 2018/10/23
 */

@UiAccessAnnotation(AccessTypes = {UiAccessType.LOGIN}, AccessArgs = {""},
        AccessActions = {UiAccessAction.LOGIN_ACCESS_FALSE_ON_RESUME_NOT_GO_LOGIN,
                UiAccessAction.LOGIN_ACCESS_FALSE_ON_CREATE_NOT_FINISH_UI})
public class MvpTravelNoteReleaseActivity extends
        BaseMvpActionBarTextMenuActivity<IMvpTravelNoteReleaseContract.Ui, MvpTravelNoteReleasePresenter>
        implements IMvpTravelNoteReleaseContract.Ui, View.OnClickListener {
    private SwipeRefreshLayout swipe_refresh_layout;
    private NestedScrollView nested_scroll_view;
    private TextView preview_note_btn_tv;
    private ArticleDisplayView note_preview_adv;
    private RelativeLayout note_preview_rl, set_out_date_rl, day_count_rl, belong_shop_rl;
    private ArticleEditorView aev_view;
    private EditText title_et, preface_et;
    private TextView set_out_date_tv, day_count_tv, belong_shop_tv;
    private InputTextDialog mDayCountInputDialog;
    private DateSelectDialog mSetOutDateSelectDialog;

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.mvp_activity_travel_note_release;
    }

    @Override
    protected void findViewOnCreate() {
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);
        nested_scroll_view = findViewById(R.id.nested_scroll_view);
        note_preview_rl = findViewById(R.id.note_preview_rl);
        preview_note_btn_tv = findViewById(R.id.preview_note_btn_tv);
        note_preview_adv = findViewById(R.id.note_preview_adv);
        set_out_date_rl = findViewById(R.id.set_out_date_rl);
        day_count_rl = findViewById(R.id.day_count_rl);
        belong_shop_rl = findViewById(R.id.belong_shop_rl);
        aev_view = findViewById(R.id.aev_view);
        title_et = findViewById(R.id.title_et);
        preface_et = findViewById(R.id.preface_et);
        set_out_date_tv = findViewById(R.id.set_out_date_tv);
        day_count_tv = findViewById(R.id.day_count_tv);
        belong_shop_tv = findViewById(R.id.belong_shop_tv);
    }

    @Override
    protected void init() {
        preview_note_btn_tv.setOnClickListener(this);
        note_preview_rl.setOnClickListener(this);
        set_out_date_rl.setOnClickListener(this);
        belong_shop_rl.setOnClickListener(this);
        day_count_rl.setOnClickListener(this);

        swipe_refresh_layout.setColorSchemeResources(
                R.color.red,
                R.color.yellow,
                R.color.green
        );
        swipe_refresh_layout.setEnabled(false);
    }

    @Override
    protected void setupActionBar(ImageView goBackIv, TextView titleTv, TextView menuBtnTv) {
        titleTv.setText(R.string.mvp_note_release_title);
        menuBtnTv.setText(R.string.mvp_note_release_confirm_menu);
        menuBtnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddNoteBtnClicked();
            }
        });
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
        if (requestCode == mPresenter.REQUEST_CODE_SELECT_BELONG_SHOP) {
            if (resultCode == RESULT_OK) {
                mPresenter.onBelongShopSelected(data);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.note_preview_rl) {
            note_preview_rl.setVisibility(View.GONE);
        } else if (v.getId() == R.id.preview_note_btn_tv) {
            note_preview_rl.setVisibility(View.VISIBLE);
            List<TextImageEntity> noteDayList = aev_view.getSectionList();
            note_preview_adv.init(noteDayList);
        } else if (v.getId() == R.id.set_out_date_rl) {
            if (mSetOutDateSelectDialog == null) {
                int year = Calendar.getInstance().get(Calendar.YEAR);
                mSetOutDateSelectDialog = DialogUtils.createDateSelectDialog(this,
                        year, year + 1, new DateSelectDialog.IDialogDateSelected() {
                            @Override
                            public void onSelected(Calendar calendar) {
                                set_out_date_tv
                                        .setText(new SimpleDateFormat("yyyy-MM-dd")
                                                .format(calendar.getTime()));
                            }
                        });
            }
            mSetOutDateSelectDialog.show();
        } else if (v.getId() == R.id.day_count_rl) {
            if (mDayCountInputDialog == null) {
                mDayCountInputDialog = DialogUtils.createTextInputDialog(this,
                        getString(R.string.mvp_shop_release_day_count_hint),
                        "", 3,
                        EditorInfo.TYPE_CLASS_NUMBER, new InputTextDialog.IActionClickListener() {

                            @Override
                            public void onSubmitClick(Dialog dialog, List<String> textList) {
                                if (textList != null && textList.size() > 0 &&
                                        !TextUtils.isEmpty(textList.get(0))) {
                                    day_count_tv.setText(textList.get(0));
                                    int count = Integer.parseInt(textList.get(0));
                                    List<String> titleList = null;
                                    if (count > 1) {
                                        titleList = new ArrayList<>();
                                        for (int i = 0; i < count; i++) {
                                            titleList.add(getString(R.string.mvp_note_release_day_note_title, StringUtils.toChineseNumber(i + 1)));
                                        }
                                    }
                                    aev_view.setSectionCount(MvpTravelNoteReleaseActivity.this,
                                            count, titleList, mPresenter.getUploadAdapter());
                                }
                            }

                            @Override
                            public void onCancelClick(Dialog dialog) {

                            }
                        });
            }
            mDayCountInputDialog.show();
        } else if (v.getId() == R.id.belong_shop_rl) {
            mPresenter.selectBelongShop();
        }
    }

    private void onAddNoteBtnClicked() {
        mPresenter.addNote();
    }

    @Override
    public void setBelongShop(String ids, String names) {
        belong_shop_tv.setText(names);
        belong_shop_tv.setTag(ids);
    }

    @NonNull
    @Override
    public InputParam getNoteTitleParam(String key) {
        return new InputParam(this, key, title_et.getText().toString(),
                nested_scroll_view, title_et);
    }

    @NonNull
    @Override
    public InputParam getNoteSetOutDateParam(String key) {
        return new InputParam(this, key, set_out_date_tv.getText().toString(),
                nested_scroll_view, set_out_date_tv);
    }

    @NonNull
    @Override
    public InputParam getNoteTravelDayCountParam(String key) {
        return new InputParam(this, key, day_count_tv.getText().toString(),
                nested_scroll_view, day_count_tv);
    }

    @NonNull
    @Override
    public InputParam getNoteBelongShopsParam(String key, ArrayList<MvpShopItemEntity> list) {
        return new InputParam(this, key, list,
                nested_scroll_view, belong_shop_tv);
    }

    @NonNull
    @Override
    public InputParam getNotePrefaceParam(String key) {
        return new InputParam(this, key, preface_et.getText().toString(),
                nested_scroll_view, preface_et);
    }

    @NonNull
    @Override
    public InputParam getNoteContentParam(String key) {
        return new InputParam(this, key, aev_view.getSectionList());
    }

    @Override
    public void setLoadingUiVisibility(boolean processing) {
        if (swipe_refresh_layout == null) {
            return;
        }
        swipe_refresh_layout.setRefreshing(processing);
    }
}
