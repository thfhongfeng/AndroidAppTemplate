package com.pine.app.lib.input.pinyin;

import com.pine.app.lib.input.candidate.CandidateBean;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PinyinNode {
    private String letter;
    private String pinyin;

    private List<CandidateBean> data;
    private List<CandidateBean> dataA;
    private List<CandidateBean> dataB;
    private List<CandidateBean> dataC;

    private List<CandidateBean> dataD;
    private List<CandidateBean> dataE;
    private LinkedHashMap<String, PinyinNode> childMap = new LinkedHashMap<>();

    public PinyinNode(String letter) {
        this.letter = letter;
    }

    public void setListData(ArrayList<List<CandidateBean>> listList) {
        if (listList == null || listList.size() < 1) {
            return;
        }
        data = new ArrayList<>();
        if (listList.size() > 0) {
            dataA = listList.get(0);
            if (dataA != null) {
                data.addAll(dataA);
            }
        }
        if (listList.size() > 1) {
            dataB = listList.get(1);
            if (dataB != null) {
                data.addAll(dataB);
            }
        }
        if (listList.size() > 2) {
            dataC = listList.get(2);
            if (dataC != null) {
                data.addAll(dataC);
            }
        }
        if (listList.size() > 3) {
            dataD = listList.get(3);
            if (dataD != null) {
                data.addAll(dataD);
            }
        }
        if (listList.size() > 4) {
            dataE = listList.get(4);
            if (dataE != null) {
                data.addAll(dataE);
            }
        }
    }

    public PinyinNode getChildNode(String letter) {
        return childMap.get(letter);
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public List<CandidateBean> getData() {
        return data;
    }

    public void setData(List<CandidateBean> data) {
        this.data = data;
    }

    public List<CandidateBean> getDataA() {
        return dataA;
    }

    public void setDataA(List<CandidateBean> dataA) {
        this.dataA = dataA;
    }

    public List<CandidateBean> getDataB() {
        return dataB;
    }

    public void setDataB(List<CandidateBean> dataB) {
        this.dataB = dataB;
    }

    public List<CandidateBean> getDataC() {
        return dataC;
    }

    public void setDataC(List<CandidateBean> dataC) {
        this.dataC = dataC;
    }

    public List<CandidateBean> getDataD() {
        return dataD;
    }

    public void setDataD(List<CandidateBean> dataD) {
        this.dataD = dataD;
    }

    public List<CandidateBean> getDataE() {
        return dataE;
    }

    public void setDataE(List<CandidateBean> dataE) {
        this.dataE = dataE;
    }

    public LinkedHashMap<String, PinyinNode> getChildMap() {
        return childMap;
    }

    public void setChildMap(LinkedHashMap<String, PinyinNode> childMap) {
        this.childMap = childMap;
    }
}
