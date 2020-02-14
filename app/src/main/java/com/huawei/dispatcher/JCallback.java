package com.huawei.dispatcher;

public class JCallback<T> {
    boolean cancel;
    Callback<T> callback;

    public JCallback(Callback<T> callback) {
        this.callback = callback;
    }

    public void cancel() {
        this.cancel = true;
    }
}
