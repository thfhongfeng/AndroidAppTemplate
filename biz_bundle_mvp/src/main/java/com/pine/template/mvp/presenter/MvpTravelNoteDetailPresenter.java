package com.pine.template.mvp.presenter;

import android.os.Bundle;
import android.text.TextUtils;

import com.pine.template.mvp.MvpKeyConstants;
import com.pine.template.mvp.adapter.MvpTravelNoteDetailComplexAdapter;
import com.pine.template.mvp.bean.MvpTravelNoteCommentEntity;
import com.pine.template.mvp.bean.MvpTravelNoteDetailEntity;
import com.pine.template.mvp.contract.IMvpTravelNoteDetailContract;
import com.pine.template.mvp.model.MvpTravelNoteModel;
import com.pine.tool.architecture.mvp.presenter.Presenter;
import com.pine.tool.exception.MessageException;
import com.pine.tool.request.response.IAsyncResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvpTravelNoteDetailPresenter extends Presenter<IMvpTravelNoteDetailContract.Ui>
        implements IMvpTravelNoteDetailContract.Presenter {
    private String mId;
    private MvpTravelNoteModel mTravelNoteModel;
    private MvpTravelNoteDetailComplexAdapter mTravelNoteDetailAdapter;

    public MvpTravelNoteDetailPresenter() {
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
    public MvpTravelNoteDetailComplexAdapter getListAdapter() {
        if (mTravelNoteDetailAdapter == null) {
            mTravelNoteDetailAdapter = new MvpTravelNoteDetailComplexAdapter();
            mTravelNoteDetailAdapter.enableInitLoading(true);
        }
        return mTravelNoteDetailAdapter;
    }

    @Override
    public void loadTravelNoteDetailData() {
        if (mIsLoadProcessing) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("id", mId);
        setUiLoading(true);
        mTravelNoteModel.requestTravelNoteDetailData(params, new IAsyncResponse<MvpTravelNoteDetailEntity>() {
            @Override
            public void onResponse(MvpTravelNoteDetailEntity entity) {
                setUiLoading(false);
                if (isUiAlive()) {
                    List<MvpTravelNoteDetailEntity> list = new ArrayList<>();
                    list.add(entity);
                    mTravelNoteDetailAdapter.setFirstPartData(list);
                }
                loadTravelNoteCommentData(true);
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

    @Override
    public void loadTravelNoteCommentData(final boolean refresh) {
        if (mIsLoadProcessing) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        int pageNo = 1;
        if (!refresh) {
            pageNo = mTravelNoteDetailAdapter.getNextPageNo();
        }
        params.put(MvpKeyConstants.PAGE_NO, String.valueOf(pageNo));
        params.put(MvpKeyConstants.PAGE_SIZE, String.valueOf(mTravelNoteDetailAdapter.getPageSize()));
        params.put("id", mId);
        setUiLoading(true);
        mTravelNoteModel.requestTravelNoteCommentData(params, new IAsyncResponse<ArrayList<MvpTravelNoteCommentEntity>>() {
            @Override
            public void onResponse(ArrayList<MvpTravelNoteCommentEntity> list) {
                setUiLoading(false);
                if (isUiAlive()) {
                    if (refresh) {
                        mTravelNoteDetailAdapter.setSecondPartData(list);
                    } else {
                        mTravelNoteDetailAdapter.addSecondPartData(list);
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
