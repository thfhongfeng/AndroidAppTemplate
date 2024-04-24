package com.pine.template.face.vm;

import androidx.lifecycle.MutableLiveData;

import com.pine.tool.architecture.mvvm.vm.ViewModel;

public class GetFaceMarkVm extends ViewModel {

    // 提示数据
    MutableLiveData<Integer> tipData = new MutableLiveData<>();

    public MutableLiveData<Integer> getTipData() {
        return tipData;
    }
}
