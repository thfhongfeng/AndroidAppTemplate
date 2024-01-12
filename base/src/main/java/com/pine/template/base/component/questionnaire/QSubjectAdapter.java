package com.pine.template.base.component.questionnaire;

import static com.pine.template.base.component.questionnaire.bean.QTrueOrFalseS.ANSWER_FALSE;
import static com.pine.template.base.component.questionnaire.bean.QTrueOrFalseS.ANSWER_TRUE;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pine.template.base.R;
import com.pine.template.base.component.questionnaire.bean.QChoiceOptionS;
import com.pine.template.base.component.questionnaire.bean.QShortAnswerS;
import com.pine.template.base.component.questionnaire.bean.QSubjectBean;
import com.pine.template.base.component.questionnaire.bean.QTrueOrFalseS;
import com.pine.template.base.component.questionnaire.bean.QuestionnaireBean;
import com.pine.template.base.databinding.BaseUiQChoiceOptionBinding;
import com.pine.template.base.databinding.BaseUiQComponentBinding;
import com.pine.template.base.databinding.BaseUiQShortAnswerBinding;
import com.pine.template.base.databinding.BaseUiQTrueOrFalseBinding;
import com.pine.template.base.util.DialogUtils;
import com.pine.template.base.widget.dialog.InputTextDialog;
import com.pine.template.base.widget.view.OptionSelector;
import com.pine.tool.util.AppUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class QSubjectAdapter extends RecyclerView.Adapter<QSubjectAdapter.BaseViewHolder> {
    private List<QSubjectBean> mData = new ArrayList<>();

    private InputTextDialog mAnswerDialog;
    private QuestionnaireBean mBean = new QuestionnaireBean();// 每页显示的题目数量
    private int mCountPerPage = 1;// 每页显示的题目数量

    public void setup(QuestionnaireBean bean) {
        mBean = bean;
        mCountPerPage = bean.getCountPerPage();
        if (mCountPerPage < 1) {
            mCountPerPage = 1;
        }
    }

    public void release() {
        if (mAnswerDialog != null && mAnswerDialog.isShowing()) {
            mAnswerDialog.dismiss();
        }
    }

    public void setData(List<QSubjectBean> data) {
        mData = data;
        if (mData == null) {
            mData = new ArrayList<>();
        }
    }

    public List<QSubjectBean> getData() {
        return mData;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder = null;
        switch (viewType) {
            case -9999:
                viewHolder = new ComponentVH(parent.getContext(), LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.base_ui_q_component, parent, false));
                break;
            case QSubjectBean.TYPE_CHOICE_OPTION:
                viewHolder = new ChoiceOptionVH(parent.getContext(), LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.base_ui_q_choice_option, parent, false));
                break;
            case QSubjectBean.TYPE_TRUE_OR_FALSE:
                viewHolder = new TrueOrFalseVH(parent.getContext(), LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.base_ui_q_true_or_false, parent, false));
                break;
            case QSubjectBean.TYPE_SHOR_ANSWER:
                viewHolder = new ShortAnswerVH(parent.getContext(), LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.base_ui_q_short_answer, parent, false));
                break;
            default:
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (holder instanceof ChoiceOptionVH ||
                holder instanceof TrueOrFalseVH
                || holder instanceof ShortAnswerVH) {
            holder.updateData(mData.get(position), position);
        } else if (holder instanceof ComponentVH) {
            holder.updateData(null, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mCountPerPage == 1 ? mData.get(position).getType() : -9999;
    }

    @Override
    public int getItemCount() {
        return (mData.size() + mCountPerPage - 1) / mCountPerPage;
    }

    public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void updateData(T content, int position);
    }

    private void loadImage(RecyclerView recyclerView, List<String> imageList) {
        if (imageList == null || imageList.size() < 1) {
            recyclerView.setVisibility(View.GONE);
        }
        ImageAdapter adapter = new ImageAdapter();
        GridLayoutManager layoutManager = new GridLayoutManager(recyclerView.getContext(),
                AppUtils.isLandScreen() ? 6 : 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setData(imageList);
    }

    public class ComponentVH extends BaseViewHolder<QSubjectBean> {
        private Context mContext;
        private BaseUiQComponentBinding mBinding;

        public ComponentVH(Context context, View itemView) {
            super(itemView);
            mContext = context;
            mBinding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void updateData(QSubjectBean content, int position) {
            mBinding.container.removeAllViews();
            for (int i = 0; i < mCountPerPage; i++) {
                int index = position * mCountPerPage + i;
                if (index >= mData.size()) {
                    break;
                }
                QSubjectBean qSubjectBean = mData.get(index);
                View view = null;
                BaseViewHolder viewHolder = null;
                switch (qSubjectBean.getType()) {
                    case QSubjectBean.TYPE_CHOICE_OPTION:
                        view = LayoutInflater.from(mContext).inflate(R.layout.base_ui_q_choice_option,
                                null, false);
                        viewHolder = new ChoiceOptionVH(mContext, view);
                        break;
                    case QSubjectBean.TYPE_TRUE_OR_FALSE:
                        view = LayoutInflater.from(mContext).inflate(R.layout.base_ui_q_true_or_false,
                                null, false);
                        viewHolder = new TrueOrFalseVH(mContext, view);
                        break;
                    case QSubjectBean.TYPE_SHOR_ANSWER:
                        view = LayoutInflater.from(mContext).inflate(R.layout.base_ui_q_short_answer,
                                null, false);
                        viewHolder = new ShortAnswerVH(mContext, view);
                        break;
                    default:
                        break;
                }
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mBinding.container.addView(view, layoutParams);
                viewHolder.updateData(qSubjectBean, index);
            }
        }
    }

    public class ChoiceOptionVH extends BaseViewHolder<QSubjectBean> {
        private Context mContext;
        private BaseUiQChoiceOptionBinding mBinding;

        public ChoiceOptionVH(Context context, View itemView) {
            super(itemView);
            mContext = context;
            mBinding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void updateData(final QSubjectBean content, int position) {
            final QChoiceOptionS q = content.parseToChoiceOption();
            if (q == null || q.getOptionList() == null) {
                return;
            }
            int columns = mBean.getChoiceOptionColumn() > 0 ? mBean.getChoiceOptionColumn() : 1;
            mBinding.tvOrder.setText((position + 1) + ". ");
            mBinding.tvSubject.setText(content.getSubject());
            loadImage(mBinding.imgRv, content.getSubjectImages());
            List<OptionSelector.Option> optionList = q.getOptionList();
            if (content.getAnswerIndexes() != null) {
                for (int index : content.getAnswerIndexes()) {
                    if (index >= 0 && index < optionList.size()) {
                        optionList.get(index).setSelected(true);
                    }
                }
            }
            mBinding.optionContainer.build(q.getType(), optionList, columns,
                    new OptionSelector.IOnSelectListener() {
                        @Override
                        public void onSelectStateChange(OptionSelector.Option option, int index, boolean selected) {
                            if (q.getType() == OptionSelector.TYPE_SINGLE) {
                                List<Integer> answerList = new LinkedList<>();
                                answerList.add(index);
                                content.setAnswerIndexes(answerList);
                            } else {
                                List<Integer> answerList = content.getAnswerIndexes();
                                if (answerList == null) {
                                    answerList = new LinkedList<>();
                                }
                                if (selected) {
                                    answerList.add(index);
                                } else {
                                    answerList.remove(new Integer(index));
                                }
                            }
                        }
                    });
        }
    }

    public class TrueOrFalseVH extends BaseViewHolder<QSubjectBean> {
        private Context mContext;
        private BaseUiQTrueOrFalseBinding mBinding;

        public TrueOrFalseVH(Context context, View itemView) {
            super(itemView);
            mContext = context;
            mBinding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void updateData(final QSubjectBean content, int position) {
            final QTrueOrFalseS q = content.parseToTrueOrFalse();
            if (q == null) {
                return;
            }
            mBinding.tvOrder.setText((position + 1) + ". ");
            mBinding.tvSubject.setText(content.getSubject());
            loadImage(mBinding.imgRv, content.getSubjectImages());
            mBinding.optionSingleRg.removeAllViews();

            RadioButton itemTrue = new RadioButton(mContext);
            itemTrue.setSelected(QTrueOrFalseS.ANSWER_TRUE.equals(content.getAnswer()));
            itemTrue.setText(TextUtils.isEmpty(q.getTrueText()) ? mContext.getString(R.string.base_true) : q.getTrueText());
            itemTrue.setId(View.generateViewId());
            itemTrue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((RadioButton) v).isChecked()) {
                        content.setAnswer(ANSWER_TRUE);
                    }
                }
            });
            RadioButton itemFalse = new RadioButton(mContext);
            itemFalse.setSelected(ANSWER_FALSE.equals(content.getAnswer()));
            itemFalse.setText(TextUtils.isEmpty(q.getFalseText()) ? mContext.getString(R.string.base_false) : q.getFalseText());
            itemFalse.setId(View.generateViewId());
            itemFalse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((RadioButton) v).isChecked()) {
                        content.setAnswer(ANSWER_FALSE);
                    }
                }
            });
            mBinding.optionSingleRg.addView(itemTrue);
            mBinding.optionSingleRg.addView(itemFalse);
        }
    }

    public class ShortAnswerVH extends BaseViewHolder<QSubjectBean> {
        private Context mContext;
        private BaseUiQShortAnswerBinding mBinding;

        public ShortAnswerVH(Context context, View itemView) {
            super(itemView);
            mContext = context;
            mBinding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void updateData(final QSubjectBean content, int position) {
            final QShortAnswerS q = content.parseToShortAnswer();
            if (q == null) {
                return;
            }
            mBinding.tvOrder.setText((position + 1) + ". ");
            mBinding.tvSubject.setText(content.getSubject());
            loadImage(mBinding.imgRv, content.getSubjectImages());
            mBinding.tvAnswer.setText(content.getAnswer());
            mBinding.tvAnswer.setLines(q.getLines() > 0 ? q.getLines() : 3);
            mBinding.tvAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mAnswerDialog == null) {
                        mAnswerDialog = DialogUtils.createTextInputDialog(mContext, "", "",
                                9999, new InputTextDialog.ActionClickListener() {
                                    @Override
                                    public boolean onSubmitClick(Dialog dialog, List<String> textList) {
                                        mBinding.tvAnswer.setText(textList.get(0));
                                        content.setAnswer(textList.get(0));
                                        return false;
                                    }
                                });
                    }
                    mAnswerDialog.show(true);
                }
            });
        }
    }
}
