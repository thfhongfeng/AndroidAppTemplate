package com.pine.app.lib.input.candidate;

import android.text.TextUtils;

public class CandidateBean {
    private String candidate;

    private String key;

    public boolean isInvalid() {
        return TextUtils.isEmpty(candidate) || TextUtils.isEmpty(key);
    }

    public String getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "CandidateBean{" +
                "candidate='" + candidate + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
