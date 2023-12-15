package com.pine.tool.helper;

public class LanguageEntity {
    public String value;
    public String name;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "LanguageEntity{" +
                "value='" + value + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
