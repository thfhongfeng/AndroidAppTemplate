package com.pine.template.base.component.questionnaire.bean;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pine.tool.util.LogUtils;

import java.util.List;

public class QSubjectBean {
    public final static int TYPE_TITLE = 0;
    public final static int TYPE_CHOICE_OPTION = 1;
    public final static int TYPE_TRUE_OR_FALSE = 2;
    public final static int TYPE_SHOR_ANSWER = 3;

    private String subjectId;
    private int type;
    private String subject;
    private List<String> subjectImages;
    private String tip;
    private String explain;

    // json字符串，具体格式参看对应类型的题内容对象
    private String content;

    private List<Integer> answerIndexes;
    private String answer;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getSubjectImages() {
        return subjectImages;
    }

    public void setSubjectImages(List<String> subjectImages) {
        this.subjectImages = subjectImages;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Integer> getAnswerIndexes() {
        return answerIndexes;
    }

    public void setAnswerIndexes(List<Integer> answerIndexes) {
        this.answerIndexes = answerIndexes;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void clearAnswer() {
        answerIndexes = null;
        answer = "";
    }

    public boolean isAnswered() {
        return !TextUtils.isEmpty(answer) || (answerIndexes != null && answerIndexes.size() > 0);
    }

    public QChoiceOptionS parseToChoiceOption() {
        QChoiceOptionS q = null;
        if (type == TYPE_CHOICE_OPTION && !TextUtils.isEmpty(content)) {
            try {
                Gson sGson = new GsonBuilder().disableHtmlEscaping().create();
                q = sGson.fromJson(content, QChoiceOptionS.class);
            } catch (Exception e) {
                LogUtils.e("QBean", "parseToChoiceOption content e:" + e);
            }
        }
        return q;
    }

    public QTrueOrFalseS parseToTrueOrFalse() {
        QTrueOrFalseS q = null;
        if (type == TYPE_TRUE_OR_FALSE && !TextUtils.isEmpty(content)) {
            try {
                Gson sGson = new GsonBuilder().disableHtmlEscaping().create();
                q = sGson.fromJson(content, QTrueOrFalseS.class);
            } catch (Exception e) {
                LogUtils.e("QBean", "parseToTrueOrFalse content e:" + e);
            }
        }
        return q;
    }

    public QShortAnswerS parseToShortAnswer() {
        QShortAnswerS q = null;
        if (type == TYPE_SHOR_ANSWER && !TextUtils.isEmpty(content)) {
            try {
                Gson sGson = new GsonBuilder().disableHtmlEscaping().create();
                q = sGson.fromJson(content, QShortAnswerS.class);
            } catch (Exception e) {
                LogUtils.e("QBean", "parseToShortAnswer content e:" + e);
            }
        }
        return q;
    }

    @Override
    public String toString() {
        return "QBean{" +
                "type=" + type +
                ", subjectId='" + subjectId + '\'' +
                ", subject='" + subject + '\'' +
                ", subjectImages=" + subjectImages +
                ", tip='" + tip + '\'' +
                ", explain='" + explain + '\'' +
                ", content='" + content + '\'' +
                ", answerIndexes=" + answerIndexes +
                ", answer='" + answer + '\'' +
                '}';
    }
}
