package com.hawk.contact.util;

import android.util.Log;

/**
 * Created by heyong on 16/3/11.
 */
public class AndroidLogger implements Logger {

    @Override
    public void i(String tag, String message) {
        Log.i(tag, message);
    }

    @Override
    public void d(String tag, String message) {
        Log.d(tag, message);
    }

    @Override
    public void e(String tag, String message) {
        Log.e(tag, message);
    }
}
