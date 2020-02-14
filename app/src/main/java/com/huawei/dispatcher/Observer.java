package com.huawei.dispatcher;

public interface Observer<T> {
    void onResult(T t, Throwable error);
}
