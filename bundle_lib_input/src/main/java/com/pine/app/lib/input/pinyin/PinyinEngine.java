package com.pine.app.lib.input.pinyin;

import android.content.Context;
import android.text.TextUtils;

import com.pine.app.lib.input.candidate.CandidateAssemble;
import com.pine.app.lib.input.candidate.CandidateBean;
import com.pine.tool.util.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PinyinEngine {
    private final String TAG = this.getClass().getSimpleName();

    private static PinyinEngine instance;

    public synchronized static PinyinEngine getInstance() {
        if (instance == null) {
            instance = new PinyinEngine();
        }
        return instance;
    }

    private PinyinEngine() {
    }

    private final String PINYIN_DICT = "pinyin_to_char";
    private PinyinNode mPinyinNode = new PinyinNode("");

    public void loadDictionary(Context context) {
        try {
            mPinyinNode = PinyinFileUtils.readJsonToNode(context, PINYIN_DICT);
        } catch (Exception e) {
            LogUtils.d(TAG, "loadDictionary exception:" + e);
        }
        if (mPinyinNode == null) {
            mPinyinNode = new PinyinNode("");
        }
    }

    public CandidateAssemble getCandidates(PinyinEntity entity) {
        LogUtils.d(TAG, "getCandidates pinyin:" + entity);
        CandidateAssemble ret = new CandidateAssemble();
        String pinyin = entity.getLeftPinyin().toString();
        if (TextUtils.isEmpty(pinyin)) {
            return null;
        }
        int index = 0;
        PinyinNode node = mPinyinNode;
        StringBuilder doStr = new StringBuilder();
        PinyinNode subNode;
        while (index < pinyin.length() && node != null) {
            String doChar = String.valueOf(pinyin.charAt(index));
            subNode = node.getChildNode(doChar);
            if (subNode != null) {
                node = subNode;
                index++;
                doStr.append(doChar);
            } else {
                break;
            }
        }
        List<CandidateBean> list = new ArrayList<>();
        String donePinyin = "";
        if (index == 0) {
            donePinyin = pinyin.substring(0, 1);
        } else {
            donePinyin = doStr.toString();
        }
        ret.setPinyin(donePinyin);
        CandidateBean pinyinCb = new CandidateBean();
        pinyinCb.setKey(donePinyin);
        pinyinCb.setCandidate(donePinyin.toLowerCase());
        list.add(pinyinCb);
        pinyinCb = new CandidateBean();
        pinyinCb.setKey(donePinyin);
        pinyinCb.setCandidate(donePinyin.toUpperCase());
        list.add(pinyinCb);

        if (index == 0) {
            ret.setData(list);
            return ret;
        }
        List<CandidateBean> nodeList = node.getData();
        if (nodeList != null && nodeList.size() > 0) {
            list.addAll(nodeList);
            ret.setData(list);
            ret.setDataA(node.getDataA());
            ret.setDataB(node.getDataB());
            ret.setDataC(node.getDataC());
            ret.setDataD(node.getDataD());
            ret.setDataE(node.getDataE());
        } else {
            Set<String> keys = node.getChildMap().keySet();
            for (String key : keys) {
                PinyinNode child = node.getChildNode(key);
                if (child != null && child.getDataA() != null) {
                    list.addAll(child.getDataA());
                    ret.setDataA(child.getDataA());
                }
            }
            ret.setData(list);
        }
        LogUtils.d(TAG, "getCandidates CandidateAssemble:" + ret);
        return ret;
    }
}