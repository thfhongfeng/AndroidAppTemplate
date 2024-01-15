package com.pine.template.base.component.questionnaire.bean;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireBean {
    private String dataVersion;

    private String questionnaireId;
    private String title;
    private String subRemark;

    // 答题时间限制，没有设置则表示无限制。答题时间限制只有在自动答题时（QConfig的auto为false）才有效
    // 单位：秒
    private int answerTime;
    private int answerWarningTime = 60; // 答题剩余告警时间.单位：秒

    private boolean canGoPre = true; // 是否允许主动前往上一题
    private boolean canGoNext = true; // 是否允许主动前往下一题

    private boolean complete;
    private List<QSubjectBean> entities;

    private int countPerPage;// 每页显示的题目数量

    // 选择项排列列数（1：纵向排列；其它-网格排列）
    private int choiceOptionColumn = 1;

    public String getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(String dataVersion) {
        this.dataVersion = dataVersion;
    }

    public String getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(String questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubRemark() {
        return subRemark;
    }

    public void setSubRemark(String subRemark) {
        this.subRemark = subRemark;
    }

    public int getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(int answerTime) {
        this.answerTime = answerTime;
    }

    public int getAnswerWarningTime() {
        return answerWarningTime;
    }

    public void setAnswerWarningTime(int answerWarningTime) {
        this.answerWarningTime = answerWarningTime;
    }

    public boolean isCanGoPre() {
        return canGoPre;
    }

    public void setCanGoPre(boolean canGoPre) {
        this.canGoPre = canGoPre;
    }

    public boolean isCanGoNext() {
        return canGoNext;
    }

    public void setCanGoNext(boolean canGoNext) {
        this.canGoNext = canGoNext;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public List<QSubjectBean> getEntities() {
        return entities;
    }

    public void setEntities(List<QSubjectBean> entities) {
        this.entities = entities;
    }

    public int getCountPerPage() {
        return countPerPage;
    }

    public void setCountPerPage(int countPerPage) {
        this.countPerPage = countPerPage;
    }

    public int getChoiceOptionColumn() {
        return choiceOptionColumn;
    }

    public void setChoiceOptionColumn(int choiceOptionColumn) {
        this.choiceOptionColumn = choiceOptionColumn;
    }

    public int gradeScore() {
        int score = -1;
        if (complete && entities != null) {
            score = 0;
            for (QSubjectBean bean : entities) {
                if (bean.getType() == QSubjectBean.TYPE_CHOICE_OPTION) {
                    QChoiceOptionS subject = bean.parseToChoiceOption();
                    if (subject != null && subject.getOptionList() != null && bean.getAnswerIndexes() != null) {
                        for (int index : bean.getAnswerIndexes()) {
                            if (index >= 0 && index < subject.getOptionList().size()) {
                                score += subject.getOptionList().get(index).getScore();
                            }
                        }
                    }
                } else if (bean.getType() == QSubjectBean.TYPE_TRUE_OR_FALSE) {
                    QTrueOrFalseS subject = bean.parseToTrueOrFalse();
                    if (subject != null) {
                        if (QTrueOrFalseS.ANSWER_FALSE.equals(bean.getAnswer())) {
                            score += subject.getFalseScore();
                        } else if (QTrueOrFalseS.ANSWER_TRUE.equals(bean.getAnswer())) {
                            score += subject.getTrueScore();
                        }
                    }
                } else if (bean.getType() == QSubjectBean.TYPE_SHOR_ANSWER) {
                    QShortAnswerS subject = bean.parseToShortAnswer();
                    score += calShorAnswerScore(subject, bean.getAnswer());
                }
            }
        }
        return score;
    }

    private int calShorAnswerScore(QShortAnswerS subject, String answerText) {
        if (subject != null && !TextUtils.isEmpty(answerText)) {
            return subject.getScore();
        }
        return 0;
    }

    public List<QSubjectBean> getCompleteSubjectList() {
        List<QSubjectBean> list = new ArrayList<>();
        if (complete && entities != null) {
            for (QSubjectBean bean : entities) {
                if (bean.getAnswerIndexes() != null
                        || !TextUtils.isEmpty(bean.getAnswer())) {
                    list.add(bean);
                }
            }
        }
        return list;
    }

    public boolean canSlideScroll() {
        return canGoPre && canGoNext;
    }

    @Override
    public String toString() {
        return "QuestionnaireBean{" +
                "dataVersion='" + dataVersion + '\'' +
                ", questionnaireId='" + questionnaireId + '\'' +
                ", title='" + title + '\'' +
                ", subRemark='" + subRemark + '\'' +
                ", answerTime=" + answerTime +
                ", answerWarningTime=" + answerWarningTime +
                ", canGoPre=" + canGoPre +
                ", canGoNext=" + canGoNext +
                ", complete=" + complete +
                ", entities=" + entities +
                ", countPerPage=" + countPerPage +
                ", choiceOptionColumn=" + choiceOptionColumn +
                '}';
    }
}
