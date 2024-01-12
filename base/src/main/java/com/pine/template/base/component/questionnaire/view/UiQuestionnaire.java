package com.pine.template.base.component.questionnaire.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.pine.template.base.R;
import com.pine.template.base.component.questionnaire.QSubjectAdapter;
import com.pine.template.base.component.questionnaire.bean.QSubjectBean;
import com.pine.template.base.component.questionnaire.bean.QuestionnaireBean;
import com.pine.template.base.util.DialogUtils;
import com.pine.tool.service.TimerHelper;

import java.util.List;

public class UiQuestionnaire extends FrameLayout {
    private final String TAG = this.getClass().getSimpleName();

    private View rootView;
    private RecyclerView recyclerView;
    private LinearLayout titleContainer;
    private TextView titleTv, subRemarkTv;
    private TextView subjectAnswerStateTv, timeTv, preTv, nextTv, cancelTv, completeTv;

    private QSubjectAdapter mAdapter;
    // 使用 PagerSnapHelper 实现每次滚动停在一个子项上
    private PagerSnapHelper mSnapHelper;
    private int mCurPosition;

    private Dialog mConfirmDialog;

    private boolean mIsManualScroll;
    private boolean mNeedFixScroll;

    public UiQuestionnaire(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public UiQuestionnaire(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public UiQuestionnaire(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        rootView = LayoutInflater.from(context).inflate(R.layout.base_ui_questionnaire, this, true);
        titleContainer = rootView.findViewById(R.id.title_container);
        titleTv = rootView.findViewById(R.id.tv_title);
        subRemarkTv = rootView.findViewById(R.id.tv_sub_remark);
        subjectAnswerStateTv = rootView.findViewById(R.id.tv_subject_answer_state);
        timeTv = rootView.findViewById(R.id.tv_time);
        recyclerView = rootView.findViewById(R.id.rv_content);
        cancelTv = rootView.findViewById(R.id.tv_cancel);
        preTv = rootView.findViewById(R.id.tv_pre);
        nextTv = rootView.findViewById(R.id.tv_next);
        completeTv = rootView.findViewById(R.id.tv_complete);

        cancelTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mConfirmDialog = DialogUtils.showConfirmDialog(getContext(), R.string.base_q_cancel_d_title,
                        R.string.base_q_cancel_d_msg, true, true,
                        new DialogUtils.IActionListener() {
                            @Override
                            public boolean onLeftBtnClick(Dialog dialog) {
                                return false;
                            }

                            @Override
                            public boolean onRightBtnClick(Dialog dialog) {
                                if (mListener != null) {
                                    mListener.onDone(false);
                                }
                                return false;
                            }
                        });
            }
        });
        completeTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onCompleteBtnClick(false);
            }
        });

        preTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollStep(-1);
            }
        });
        nextTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollStep(1);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(context) {
            // scrollToPosition和smoothScrollToPosition效果不同：
            // 1.scrollToPosition不会触发onScrollStateChanged，并且不受canScrollHorizontally的影响；
            // 2.smoothScrollToPosition会触发onScrollStateChanged的不同状态，且受canScrollHorizontally的影响
            @Override
            public boolean canScrollHorizontally() {
                return mIsManualScroll || mData.canSlideScroll();
            }

            @Override
            public void onScrollStateChanged(int newState) {
                super.onScrollStateChanged(newState);
                // 代码滑动过程中如果有手势滑动，则暂停滑动，后续计时任务中再做调整
                if (!mData.canSlideScroll() &&
                        mIsManualScroll && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    mNeedFixScroll = true;
                }
                // newState并不总是在动作结束后会SCROLL_STATE_IDLE，
                // 有时候也会停留在SCROLL_STATE_DRAGGING状态(可能跟其它滑动冲突)。
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        || newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    int position = findFirstVisibleItemPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onScrollComplete(position);
                    }
                }
            }
        };

        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

        mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(recyclerView);
    }

    private void onCompleteBtnClick(boolean forceComplete) {
        if (!forceComplete && !checkAnswerState()) {
            mConfirmDialog = DialogUtils.showConfirmDialog(getContext(), R.string.base_q_complete_d_title,
                    R.string.base_q_complete_d_not_all_msg, true, true,
                    new DialogUtils.IActionListener() {
                        @Override
                        public boolean onLeftBtnClick(Dialog dialog) {
                            return false;
                        }

                        @Override
                        public boolean onRightBtnClick(Dialog dialog) {
                            if (mListener != null) {
                                mListener.onDone(true);
                            }
                            return false;
                        }
                    });
        } else {
            if (mListener != null) {
                mListener.onDone(true);
            }
        }
    }

    private void onScrollComplete(int position) {
        mIsManualScroll = false;
        mCurPosition = position;
        setBtnState();
        checkAnswerState();
    }

    private void scrollToHead() {
        scrollStep(Integer.MIN_VALUE);
    }

    private void scrollStep(int count) {
        int nextP;
        if (count == Integer.MIN_VALUE) {
            nextP = 0;
        } else if (count == Integer.MAX_VALUE) {
            nextP = mAdapter.getItemCount() - 1;
        } else {
            nextP = mCurPosition + count;
        }
        scrollToPosition(count != Integer.MIN_VALUE && count != Integer.MAX_VALUE, nextP);
    }

    private void scrollToPosition(boolean smooth, int position) {
        if (position >= 0 && position < mAdapter.getItemCount()) {
            mIsManualScroll = true;
            if (smooth) {
                recyclerView.smoothScrollToPosition(position);
                mCurPosition = position;
            } else {
                recyclerView.scrollToPosition(position);
                onScrollComplete(position);
            }
        }
    }

    private void setBtnState() {
        if (mCurPosition > 0 && mData.isCanGoPre()) {
            preTv.setEnabled(true);
        } else {
            preTv.setEnabled(false);
        }
        if (mCurPosition < mAdapter.getItemCount() - 1 && mData.isCanGoNext()) {
            nextTv.setEnabled(true);
        } else {
            nextTv.setEnabled(false);
        }
    }

    private boolean checkAnswerState() {
        List<QSubjectBean> data = mAdapter.getData();
        if (data != null && data.size() > 0) {
            int answeredCount = 0;
            for (QSubjectBean qSubjectBean : data) {
                if (qSubjectBean.isAnswered()) {
                    answeredCount++;
                }
            }
            subjectAnswerStateTv.setVisibility(VISIBLE);
            subjectAnswerStateTv.setText(answeredCount + "/" + data.size());
            return answeredCount == data.size();
        } else {
            subjectAnswerStateTv.setVisibility(GONE);
        }
        return true;
    }

    public void release() {
        stop();
    }

    private QuestionnaireBean mData = new QuestionnaireBean();
    private IListener mListener;
    private int mDurationTime = 0;

    public void start(QuestionnaireBean data, IListener listener) {
        mData = data == null ? new QuestionnaireBean() : data;
        mListener = listener;

        if (!TextUtils.isEmpty(mData.getTitle())) {
            titleTv.setText(mData.getTitle());
            titleTv.setVisibility(VISIBLE);
        } else {
            titleTv.setVisibility(GONE);
        }
        if (!TextUtils.isEmpty(mData.getSubRemark())) {
            subRemarkTv.setText(mData.getSubRemark());
            subRemarkTv.setVisibility(VISIBLE);
        } else {
            subRemarkTv.setVisibility(GONE);
        }

        if (mAdapter == null) {
            mAdapter = new QSubjectAdapter();
            recyclerView.setAdapter(mAdapter);
        }
        mAdapter.setup(data);
        if (data != null && data.getEntities() != null) {
            for (QSubjectBean qSubjectBean : data.getEntities()) {
                qSubjectBean.clearAnswer();
            }
        }
        mAdapter.setData(data.getEntities());
        mAdapter.notifyDataSetChanged();
        onScrollComplete(0);
        scrollToHead();
        if (mData.getAnswerTime() > 0) {
            TimerHelper.schemeTimerWork(TAG, 1000, 1000, new Runnable() {
                @Override
                public void run() {
                    mDurationTime++;
                    int left = mData.getAnswerTime() - mDurationTime;
                    int hour = left / 3600;
                    int minute = left % 3600 / 60;
                    int second = left % 60;
                    String hourStr = (hour > 0 ? ((hour < 10 ? ("0" + hour) : hour) + ":") : "");
                    String minuteStr = (hour > 0 || minute > 0 ? ((minute < 10 ? ("0" + minute) : minute) + ":") : "");
                    String secondStr = second < 10 ? ("0" + second) : second + "";
                    int answerWarningTime = mData.getAnswerWarningTime();
                    answerWarningTime = answerWarningTime > 0 ? answerWarningTime : mData.getAnswerTime() / 10;
                    timeTv.setText(hourStr + minuteStr + secondStr);
                    if (left < answerWarningTime) {
                        timeTv.setSelected(true);
                    } else {
                        timeTv.setSelected(false);
                    }
                    if (left <= 0) {
                        TimerHelper.cancel(TAG);
                        onCompleteBtnClick(true);
                    } else {
                        if (mNeedFixScroll) {
                            scrollStep(0);
                            mNeedFixScroll = false;
                        }
                    }
                }
            });
            timeTv.setVisibility(VISIBLE);
        } else {
            timeTv.setVisibility(GONE);
        }
        setVisibility(VISIBLE);
    }

    public void stop() {
        mIsManualScroll = false;
        mDurationTime = 0;
        TimerHelper.cancel(TAG);
        if (mAdapter != null) {
            mAdapter.release();
        }
        if (mConfirmDialog != null && mConfirmDialog.isShowing()) {
            mConfirmDialog.dismiss();
        }
        mListener = null;
        setVisibility(GONE);
    }

    public interface IListener {
        void onDone(boolean complete);
    }
}
