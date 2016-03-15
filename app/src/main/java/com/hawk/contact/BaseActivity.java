package com.hawk.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.hawk.contact.base.ContactApplication;
import com.hawk.contact.controller.MainController;
import com.hawk.contact.display.AndroidDisplay;

/**
 * Created by heyong on 16/3/11.
 */
public class BaseActivity extends AppCompatActivity {

    private MainController mMainController;
    private Display mDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewLayoutId());

        mMainController = ContactApplication.from(this).getMainController();
        mDisplay = new AndroidDisplay(this);

        handleIntent(getIntent(), getDisplay());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, getDisplay());
    }

    protected void handleIntent(Intent intent, Display display) {

    }

    protected int getContentViewLayoutId() {
        return R.layout.activity_main;
    }

    public Display getDisplay() {
        return mDisplay;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mMainController.attachDisplay(mDisplay);
        mMainController.init();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mMainController.attachDisplay(null);
        mMainController.suspend();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDisplay = null;
    }

    protected final MainController getMainController() {
        return mMainController;
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar, boolean handleBackground) {
        setSupportActionBar(toolbar);
        getDisplay().setSupportActionBar(toolbar, handleBackground);
    }
}
