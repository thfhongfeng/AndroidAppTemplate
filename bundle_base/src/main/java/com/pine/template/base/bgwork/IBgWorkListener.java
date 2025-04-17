package com.pine.template.base.bgwork;

public interface IBgWorkListener<T> {
    boolean onBgWork(String actionType, T data);
}
