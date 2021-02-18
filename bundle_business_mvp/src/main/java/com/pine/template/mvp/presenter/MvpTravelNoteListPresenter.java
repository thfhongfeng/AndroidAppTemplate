package com.pine.template.mvp.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.pine.template.mvp.MvpConstants;
import com.pine.template.mvp.adapter.MvpTravelNoteListPaginationAdapter;
import com.pine.template.mvp.bean.MvpTravelNoteItemEntity;
import com.pine.template.mvp.contract.IMvpTravelNoteListContract;
import com.pine.template.mvp.model.MvpTravelNoteModel;
import com.pine.template.mvp.ui.activity.MvpTravelNoteReleaseActivity;
import com.pine.tool.architecture.mvp.model.IModelAsyncResponse;
import com.pine.tool.architecture.mvp.presenter.Presenter;
import com.pine.tool.exception.MessageException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvpTravelNoteListPresenter extends Presenter<IMvpTravelNoteListContract.Ui>
        implements IMvpTravelNoteListContract.Presenter {
    private String mId;
    private MvpTravelNoteModel mTravelNoteModel;
    private MvpTravelNoteListPaginationAdapter mMvpTravelNoteItemAdapter;

    public MvpTravelNoteListPresenter() {
        mTravelNoteModel = new MvpTravelNoteModel();
    }

    @Override
    public boolean parseIntentData(Bundle bundle) {
        mId = bundle.getString("id", "");
        if (TextUtils.isEmpty(mId)) {
            finishUi();
            return true;
        }
        return false;
    }

    @Override
    public MvpTravelNoteListPaginationAdapter getListAdapter() {
        if (mMvpTravelNoteItemAdapter == null) {
            mMvpTravelNoteItemAdapter = new MvpTravelNoteListPaginationAdapter();
            mMvpTravelNoteItemAdapter.enableInitLoading(true);
        }
        return mMvpTravelNoteItemAdapter;
    }

    @Override
    public void goToAddTravelNoteActivity() {
        Intent intent = new Intent(getContext(), MvpTravelNoteReleaseActivity.class);
        getContext().startActivity(intent);
    }

    @Override
    public void loadTravelNoteListData(final boolean refresh) {
        if (mIsLoadProcessing) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        int pageNo = 1;
        if (!refresh) {
            pageNo = mMvpTravelNoteItemAdapter.getNextPageNo();
        }
        params.put(MvpConstants.PAGE_NO, String.valueOf(pageNo));
        params.put(MvpConstants.PAGE_SIZE, String.valueOf(mMvpTravelNoteItemAdapter.getPageSize()));
        params.put("id", mId);
        setUiLoading(true);
        mTravelNoteModel.requestTravelNoteListData(params, new IModelAsyncResponse<ArrayList<MvpTravelNoteItemEntity>>() {
            @Override
            public void onResponse(ArrayList<MvpTravelNoteItemEntity> list) {
                setUiLoading(false);
                if (isUiAlive()) {
                    if (refresh) {
                        mMvpTravelNoteItemAdapter.setData(list);
                    } else {
                        mMvpTravelNoteItemAdapter.addData(list);
                    }
                }
            }

            @Override
            public boolean onFail(Exception e) {
                setUiLoading(false);
                if (e instanceof MessageException) {
                    if (!TextUtils.isEmpty(e.getMessage())) {
                        showShortToast(e.getMessage());
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void onCancel() {
                setUiLoading(false);
            }
        });
    }
}
