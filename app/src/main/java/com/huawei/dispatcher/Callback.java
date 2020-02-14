package com.huawei.dispatcher;

public interface Callback<T> {
    void onResult(T t, Throwable error);
}
