package com.pine.template.base.component.questionnaire.bean;

public class QShortAnswerS {
    private int score;
    private int lines;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLines() {
        return lines;
    }

    public void setLines(int lines) {
        this.lines = lines;
    }

    @Override
    public String toString() {
        return "QShortAnswer{" +
                "score=" + score +
                ", lines=" + lines +
                '}';
    }
}
