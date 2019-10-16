package com.pine.tool.access;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by tanghongfeng on 2018/9/16
 */

@Documented
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface UiAccessAnnotation {
    // 准入类别
    String[] AccessTypes();

    // 准入参数，元素个数与准入类别一一对应
    String[] AccessArgs();

    // 准入行为
    String[] AccessActions();
}
