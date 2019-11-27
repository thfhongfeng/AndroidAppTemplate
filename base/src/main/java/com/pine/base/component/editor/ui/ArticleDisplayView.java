package com.pine.base.component.editor.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pine.base.component.editor.bean.TextImageEntity;
import com.pine.base.component.editor.bean.TextImageItemEntity;

import java.util.ArrayList;
import java.util.List;

public class ArticleDisplayView extends LinearLayout {
    private List<TextImageEntity> mDayList = new ArrayList<>();

    public ArticleDisplayView(Context context) {
        super(context);
        setOrientation(VERTICAL);
    }

    public ArticleDisplayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public ArticleDisplayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
    }

    public void init(@NonNull List<TextImageEntity> dayList) {
        removeAllViews();
        mDayList = dayList;
        for (int i = 0; i < mDayList.size(); i++) {
            TextImageEntity day = mDayList.get(i);
            List<TextImageItemEntity> dayContentList = day.getItemList();
            if (dayContentList == null || dayContentList.size() < 1) {
                return;
            }
            BaseTextImageDisplayView itemView = new BaseTextImageDisplayView(getContext());
            itemView.setupView(day);
            addView(itemView);
        }
    }
}

