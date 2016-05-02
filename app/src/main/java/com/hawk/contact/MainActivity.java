package com.hawk.contact;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.hawk.contact.controller.MainController;


public class MainActivity extends BaseActivity implements MainController.MainUi {

    private MainController.MainControllerUiCallbacks mMainUiCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMainController().attachUi(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getMainController().detachUi(this);
    }

    @Override
    public void showLoginPrompt() {

    }

    @Override
    public void setCallback(MainController.MainControllerUiCallbacks mainUiCallbacks) {
        mMainUiCallbacks = mainUiCallbacks;
    }

    @Override
    public boolean isModal() {
        return false;
    }

    @Override
    protected void handleIntent(Intent intent, Display display) {
        if(intent.getAction().equals(Intent.ACTION_MAIN)) {
            if(!display.hasMainFragment()) {
                display.showMainFragment();
            }
        }
    }
}
