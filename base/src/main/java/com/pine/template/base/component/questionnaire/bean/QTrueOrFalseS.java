package com.pine.template.base.component.questionnaire.bean;

public class QTrueOrFalseS {
    public static final String ANSWER_TRUE = "true";
    public static final String ANSWER_FALSE = "false";

    private int trueScore;
    private int falseScore;
    private String trueText;
    private String falseText;

    public int getTrueScore() {
        return trueScore;
    }

    public void setTrueScore(int trueScore) {
        this.trueScore = trueScore;
    }

    public int getFalseScore() {
        return falseScore;
    }

    public void setFalseScore(int falseScore) {
        this.falseScore = falseScore;
    }

    public String getTrueText() {
        return trueText;
    }

    public void setTrueText(String trueText) {
        this.trueText = trueText;
    }

    public String getFalseText() {
        return falseText;
    }

    public void setFalseText(String falseText) {
        this.falseText = falseText;
    }

    @Override
    public String toString() {
        return "QTrueOrFalse{" +
                "trueScore=" + trueScore +
                ", falseScore=" + falseScore +
                ", trueText='" + trueText + '\'' +
                ", falseText='" + falseText + '\'' +
                '}';
    }
}
