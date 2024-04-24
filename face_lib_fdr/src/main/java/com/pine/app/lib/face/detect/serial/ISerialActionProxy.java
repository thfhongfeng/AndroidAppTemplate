package com.pine.app.lib.face.detect.serial;

public interface ISerialActionProxy {
    int ACTIONS_START = 1;

    boolean onSerialAction(int actionStep);
}
