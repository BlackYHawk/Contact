package com.hawk.contact.network;

/**
 * Created by Administrator on 2016/3/8.
 */
public abstract class BackgroundCallRunnable<R> {

    public void preExecute() {}

    public abstract R runAsync();

    public void postExecute(R result) {}

}
