package com.hawk.contact.controller;

import android.content.Intent;

import com.google.common.base.Preconditions;
import com.hawk.contact.Display;

/**
 * Created by heyong on 16/3/8.
 */
public abstract class BaseController {

    private Display display;
    private boolean mInited;

    public final void init() {
        Preconditions.checkState(mInited == false, "Already Inited");
        mInited = true;
        onInited();
    }

    public final void suspend() {
        Preconditions.checkState(mInited == true, "Not Inited");
        mInited = false;
        onSuspended();
    }

    public final void assertInited() {
        Preconditions.checkState(mInited, "Must be init to the Perform Action");
    }

    protected void onInited() {};

    protected void onSuspended() {};

    public boolean isInited() {
        return mInited;
    }

    public boolean handleIntent(Intent intent) {
        return false;
    }

    public Display getDisplay() {
        return display;
    }

    public void setDisplay(Display display) {
        this.display = display;
    }

}
