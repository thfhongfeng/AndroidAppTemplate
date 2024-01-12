package com.pine.template.base.component.questionnaire.bean;

import com.pine.template.base.widget.view.OptionSelector;

import java.util.List;

public class QChoiceOptionS {
    private int type;
    private List<OptionSelector.Option> optionList;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<OptionSelector.Option> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<OptionSelector.Option> optionList) {
        this.optionList = optionList;
    }

    @Override
    public String toString() {
        return "QChoiceOption{" +
                "type=" + type +
                ", optionList=" + optionList +
                '}';
    }
}
