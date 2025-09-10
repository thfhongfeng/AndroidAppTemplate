package com.pine.app.lib.input.candidate;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pine.app.lib.input.FlexibleKeyboard;
import com.pine.app.lib.input.R;
import com.pine.app.lib.input.pinyin.PinyinEntity;
import com.pine.template.base.recycle_view.adapter.BaseListAdapter;

public class CandidateView extends ConstraintLayout {

    public CandidateView(Context context) {
        super(context);
        init();
    }

    public CandidateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private RecyclerView rv;
    private TextView pinyin_tv;
    private ImageView right_more_iv;

    private CandidateAdapter mAdapter;
    private CandidateAssemble mCandidateAssemble;

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.input_candidate_view, this, true);
        rv = view.findViewById(R.id.rv);
        pinyin_tv = view.findViewById(R.id.pinyin_tv);
        right_more_iv = view.findViewById(R.id.right_more_iv);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv.setLayoutManager(linearLayoutManager);
        rv.setHasFixedSize(true);
        mAdapter = new CandidateAdapter();
        mAdapter.enableEmptyComplete(false, false);
        rv.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseListAdapter.IOnItemClickListener<CandidateBean>() {
            @Override
            public void onItemClick(View view, int position, String tag, CandidateBean data) {
                mKeyboard.onCandidateSelect(mCandidateAssemble == null ? "" : mCandidateAssemble.getPinyin(), data);
            }
        });
        right_more_iv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                rv.smoothScrollBy(getResources().getDimensionPixelOffset(R.dimen.dp_26), 0);
            }
        });
        right_more_iv.setVisibility(GONE);
        setVisibility(INVISIBLE);
    }

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            checkRvOverflow();
            // 移除监听，避免重复触发
            unListenForLayoutCompletion();
        }
    };

    private void listenForLayoutCompletion() {
        rv.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }

    private void unListenForLayoutCompletion() {
        rv.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }

    private FlexibleKeyboard mKeyboard;

    public void setKeyboard(@NonNull FlexibleKeyboard keyboard) {
        mKeyboard = keyboard;
    }

    private void checkRvOverflow() {
        int totalHeight = rv.computeHorizontalScrollRange();
        int visibleHeight = rv.computeHorizontalScrollExtent();
        boolean isOverflow = totalHeight > visibleHeight;
        if (isOverflow) {
            right_more_iv.setVisibility(VISIBLE);
        } else {
            right_more_iv.setVisibility(GONE);
        }
    }

    public void setCandidates(@NonNull PinyinEntity pinyin, CandidateAssemble candidateAssemble) {
        mCandidateAssemble = candidateAssemble;
        if (mCandidateAssemble == null) {
            mCandidateAssemble = new CandidateAssemble();
        }
        if (pinyin == null) {
            pinyin_tv.setText("");
        } else {
            pinyin_tv.setText(pinyin.getShowTxt());
        }
        mAdapter.setData(mCandidateAssemble.getData());
        if (mCandidateAssemble != null && mCandidateAssemble.size() > 0) {
            listenForLayoutCompletion();
        }
        setVisibility(pinyin == null ? INVISIBLE : VISIBLE);
    }

    public void clearCandidatesView() {
        setCandidates(null, null);
    }

    @Override
    protected void onDetachedFromWindow() {
        unListenForLayoutCompletion();
        super.onDetachedFromWindow();
    }
}
