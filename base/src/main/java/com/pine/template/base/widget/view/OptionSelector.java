package com.pine.template.base.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;

import com.pine.template.base.R;
import com.pine.template.base.component.image_loader.ImageLoaderManager;
import com.pine.template.base.databinding.BaseUiOptionBtnBinding;

import java.util.ArrayList;
import java.util.List;

public class OptionSelector extends GridLayout {
    // 单选
    public final static int TYPE_SINGLE = 0;
    // 不定项选择
    public final static int TYPE_MULTI_INDEFINITE = 1;

    private int mType;
    private List<Option> mData;
    private View lastSingleSelectedBtn;
    private Option lastSingleSelectedOption;
    private int mMaxImgWidth, mMaxImgHeight;

    public OptionSelector(Context context) {
        super(context);
        initView(context);
    }

    public OptionSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public OptionSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mMaxImgWidth = context.getResources().getDimensionPixelOffset(R.dimen.dp_120);
        mMaxImgHeight = context.getResources().getDimensionPixelOffset(R.dimen.dp_120);
    }

    public void setup(int maxImgWidth, int maxImgHeight) {
        mMaxImgWidth = maxImgWidth;
        mMaxImgHeight = maxImgHeight;
    }

    public void buildSingle(List<Option> data) {
        build(TYPE_SINGLE, data, 1);
    }

    public void buildMulti(List<Option> data) {
        build(TYPE_MULTI_INDEFINITE, data, 1);
    }

    public void buildSingle(List<Option> data, int columns) {
        build(TYPE_SINGLE, data, columns, null);
    }

    public void buildMulti(List<Option> data, int columns) {
        build(TYPE_MULTI_INDEFINITE, data, columns, null);
    }

    public void build(int type, List<Option> data, int columns) {
        build(type, data, columns, null);
    }

    public void build(int type, List<Option> data, int columns, final IOnSelectListener listener) {
        mType = type;
        mData = data;
        removeAllViews();
        if (mData == null) {
            return;
        }
        setColumnCount(columns);
        for (int i = 0; i < data.size(); i++) {
            final Option option = data.get(i);
            View item = LayoutInflater.from(getContext())
                    .inflate(R.layout.base_ui_option_btn, null, false);
            BaseUiOptionBtnBinding optionBtnBinding = DataBindingUtil.bind(item);
            final int finalI = i;
            if (mType == TYPE_SINGLE) {
                optionBtnBinding.ivIc.setImageResource(R.drawable.base_selector_radio);
                optionBtnBinding.selectContainer.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (lastSingleSelectedBtn != v) {
                            if (lastSingleSelectedBtn != null) {
                                lastSingleSelectedBtn.setSelected(false);
                                lastSingleSelectedOption.setSelected(false);
                            }
                            lastSingleSelectedBtn = v;
                            lastSingleSelectedOption = option;
                            lastSingleSelectedBtn.setSelected(true);
                            lastSingleSelectedOption.setSelected(true);
                            if (listener != null) {
                                listener.onSelectStateChange(option, finalI, true);
                            }
                        }
                    }
                });
            } else {
                optionBtnBinding.ivIc.setImageResource(R.drawable.base_selector_check);
                optionBtnBinding.selectContainer.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.isSelected()) {
                            option.setSelected(false);
                            v.setSelected(false);
                        } else {
                            option.setSelected(true);
                            v.setSelected(true);
                        }
                        if (listener != null) {
                            listener.onSelectStateChange(option, finalI, v.isSelected());
                        }
                    }
                });
            }
            optionBtnBinding.tvLabel.setText(option.getText());
            if (option.getImgList() != null) {
                for (String imgUrl : option.getImgList()) {
                    ImageView iv = new ImageView(getContext());
                    // 要使setMaxWidth，setMaxHeight有效，必须先设置setAdjustViewBounds为true
                    iv.setAdjustViewBounds(true);
                    iv.setMaxWidth(mMaxImgWidth);
                    iv.setMaxHeight(mMaxImgHeight);
                    iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    optionBtnBinding.imageContainer.addView(iv);
                    ImageLoaderManager.getInstance().loadImage(getContext(), imgUrl, iv);
                }
            }

            LayoutParams params = new LayoutParams();
            params.width = LayoutParams.WRAP_CONTENT;
            params.height = LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setGravity(Gravity.CENTER_VERTICAL);
            item.setLayoutParams(params);

            item.setSelected(option.selected);
            addView(item);
        }
    }

    public List<Integer> getSelectedList() {
        List<Integer> list = new ArrayList<>();
        if (mData != null) {
            for (int i = 0; i < mData.size(); i++) {
                if (mData.get(i).isSelected()) {
                    list.add(i);
                }
            }
        }
        return list;
    }

    public class Option {
        private String text;
        private List<String> imgList;
        private int score;
        private boolean selected;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public List<String> getImgList() {
            return imgList;
        }

        public void setImgList(List<String> imgList) {
            this.imgList = imgList;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

    public interface IOnSelectListener {
        void onSelectStateChange(Option option, int index, boolean selected);
    }
}
