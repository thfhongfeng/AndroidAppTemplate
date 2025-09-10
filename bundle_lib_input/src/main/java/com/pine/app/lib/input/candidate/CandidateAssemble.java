package com.pine.app.lib.input.candidate;

import java.util.List;

public class CandidateAssemble {
    private String pinyin;

    private List<CandidateBean> data;
    private List<CandidateBean> dataA;
    private List<CandidateBean> dataB;
    private List<CandidateBean> dataC;

    private List<CandidateBean> dataD;
    private List<CandidateBean> dataE;

    public int size() {
        return data == null ? 0 : data.size();
    }

    public CandidateBean get(int index) {
        if (data == null || index < 0 || index >= data.size()) {
            return null;
        }
        return data.get(index);
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

    @Override
    public String toString() {
        return "CandidateAssemble{" +
                "pinyin='" + pinyin + '\'' +
                ", data=" + data +
                ", dataA=" + dataA +
                ", dataB=" + dataB +
                ", dataC=" + dataC +
                ", dataD=" + dataD +
                ", dataE=" + dataE +
                '}';
    }
}
