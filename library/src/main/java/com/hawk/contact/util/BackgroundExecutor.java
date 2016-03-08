package com.hawk.contact.util;

import com.hawk.contact.network.BackgroundCallRunnable;

/**
 * Created by Administrator on 2016/3/8.
 */
public interface BackgroundExecutor {

    public <R> void execute(BackgroundCallRunnable<R> runnable);

}
