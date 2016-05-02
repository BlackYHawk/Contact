package com.hawk.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hawk.contact.base.ContactApplication;
import com.hawk.contact.controller.MainController;
import com.hawk.contact.display.AndroidDisplay;

/**
 * Created by heyong on 16/3/11.
 */
public class BaseActivity extends AppCompatActivity implements MainController.HostCallbacks {

    private View mCardContainer;
    private MainController mMainController;
    private Display mDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewLayoutId());

        mCardContainer = findViewById(R.id.card_container);

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

    @Override
    protected void onResume() {
        super.onResume();

        mMainController.attachDisplay(mDisplay);
        mMainController.setHostCallbacks(this);
        mMainController.init();
    }

    @Override
    public void setAccountAuthenticatorResult(String username, String authToken, String accountType) {

    }

    @Override
    protected void onPause() {
        mMainController.suspend();
        mMainController.setHostCallbacks(null);
        mMainController.detachDisplay(mDisplay);

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDisplay = null;
    }

    protected final MainController getMainController() {
        return mMainController;
    }


    protected int getContentViewLayoutId() {
        return R.layout.activity_main;
    }

    public Display getDisplay() {
        return mDisplay;
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
