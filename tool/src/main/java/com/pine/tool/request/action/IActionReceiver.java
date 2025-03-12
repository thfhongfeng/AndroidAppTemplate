package com.pine.tool.request.action;

public interface IActionReceiver {
    void onReceive(String action, String data);
}
