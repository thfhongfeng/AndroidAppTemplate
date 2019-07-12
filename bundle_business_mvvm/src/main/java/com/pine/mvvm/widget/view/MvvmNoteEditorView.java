package com.pine.mvvm.widget.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.pine.base.component.editor.bean.TextImageEditorItemData;
import com.pine.base.component.editor.ui.TextImageEditorView;
import com.pine.base.component.uploader.ui.UploadFileLinearLayout;
import com.pine.mvvm.MvvmUrlConstants;
import com.pine.mvvm.R;
import com.pine.tool.ui.Activity;
import com.pine.tool.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MvvmNoteEditorView extends LinearLayout {
    public MvvmNoteEditorView(Context context) {
        super(context);
        setOrientation(VERTICAL);
    }

    public MvvmNoteEditorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public MvvmNoteEditorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
    }

    public List<List<TextImageEditorItemData>> getNoteDayList() {
        List<List<TextImageEditorItemData>> list = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            list.add(((TextImageEditorView) getChildAt(i)).getData());
        }
        return list;
    }

    public void setDayCount(Activity activity, int dayCount, List<List<TextImageEditorItemData>> dayList,
                            UploadFileLinearLayout.OneByOneUploadAdapter adapter) {
        int childCount = getChildCount();
        if (dayCount > childCount) {
            for (int i = childCount; i < dayCount; i++) {
                addDayView(activity, dayList == null ? null : dayList.get(i + 1), i + 1, adapter);
            }
        } else if (dayCount < childCount) {
            removeViews(dayCount, childCount - dayCount);
        }
        if (dayCount == 1) {
            ((TextImageEditorView) getChildAt(0)).setTitle("");
        } else if (dayCount > 1) {
            ((TextImageEditorView) getChildAt(0))
                    .setTitle(getContext().getString(R.string.mvvm_note_release_day_note_title,
                            StringUtils.toChineseNumber(1)));
        }
        invalidate();
    }

    private void addDayView(Activity activity, List<TextImageEditorItemData> data, int day,
                            UploadFileLinearLayout.OneByOneUploadAdapter adapter) {
        TextImageEditorView view = new TextImageEditorView(getContext());
        String title = getContext().getString(R.string.mvvm_note_release_day_note_title, StringUtils.toChineseNumber(day));
        view.init(activity, MvvmUrlConstants.Upload_Single_File, day, title,
                adapter, 100 + day);
        if (data != null) {
            view.setData(data);
        }
        addView(view);
    }
}
