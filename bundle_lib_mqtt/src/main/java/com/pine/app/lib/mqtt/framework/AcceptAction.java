package com.pine.app.lib.mqtt.framework;

import androidx.annotation.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AcceptAction {
    String tag() default "";

    String acceptAction();
}

