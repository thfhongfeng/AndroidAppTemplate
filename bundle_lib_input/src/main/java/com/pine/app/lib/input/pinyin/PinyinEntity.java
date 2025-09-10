package com.pine.app.lib.input.pinyin;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.app.lib.input.candidate.CandidateBean;

import java.util.LinkedList;

public class PinyinEntity {
    private StringBuilder pinyin = new StringBuilder();
    private StringBuilder leftPinyin = new StringBuilder();
    private LinkedList<String> selectCandidate = new LinkedList<>();
    private LinkedList<String> selectPinyin = new LinkedList<>();

    public void append(String str) {
        pinyin.append(str);
        leftPinyin.append(str);
    }

    public int length() {
        return pinyin.length();
    }

    public void clear() {
        pinyin.setLength(0);
        leftPinyin.setLength(0);
        selectCandidate.clear();
        selectPinyin.clear();
    }

    public boolean isComplete() {
        return leftPinyin.length() == 0;
    }

    public void onCandidateSelect(@NonNull String pinyin, @NonNull CandidateBean bean) {
        if (bean == null || bean.isInvalid() || TextUtils.isEmpty(pinyin)) {
            return;
        }
        String lastLeft = leftPinyin.toString();
        int length = pinyin.length();
        if (leftPinyin.length() <= length) {
            leftPinyin.setLength(0);
        } else {
            leftPinyin = new StringBuilder().append(lastLeft.substring(length, lastLeft.length()));
        }
        selectCandidate.add(bean.getCandidate());
        selectPinyin.add(pinyin);
    }

    public void backspace() {
        if (selectCandidate.size() < 1) {
            pinyin.deleteCharAt(pinyin.length() - 1);
            leftPinyin.deleteCharAt(leftPinyin.length() - 1);
        } else {
            selectCandidate.removeLast();
            String lastLeft = leftPinyin.toString();
            leftPinyin = new StringBuilder().append(selectPinyin.removeLast()).append(lastLeft);
        }
    }

    public String getText() {
        String ret = "";
        for (String str : selectCandidate) {
            ret += str;
        }
        ret += leftPinyin;
        return ret;
    }

    public StringBuilder getLeftPinyin() {
        return leftPinyin;
    }

    public String getShowTxt() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : selectCandidate) {
            stringBuilder.append(str);
        }
        return stringBuilder + leftPinyin.toString();
    }

    @NonNull
    @Override
    public String toString() {
        return pinyin.toString();
    }
}
