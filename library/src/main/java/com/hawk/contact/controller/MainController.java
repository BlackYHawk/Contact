package com.hawk.contact.controller;

import com.google.common.base.Preconditions;
import com.hawk.contact.Display;
import com.hawk.contact.state.ApplicationState;
import com.hawk.contact.state.AsyncDatabaseHelper;
import com.hawk.contact.util.Logger;

/**
 * Created by heyong on 16/3/10.
 */
public class MainController extends BaseUIController<MainController.MainUi, MainController.MainUiCallbacks> {

    private static final String MAIN_TAG = MainController.class.getSimpleName();

    public interface MainUi extends BaseUIController.Ui<MainUiCallbacks> {

    }

    public interface MainUiCallbacks {
        void setShowLoginPrompt();
        void addRequestLogin();
    }

    private UserController mUserController;
    private ApplicationState mApplicationState;
    private AsyncDatabaseHelper mAsyncDatabaseHelper;
    private Logger mLogger;

    public MainController(UserController mUserController, ApplicationState mApplicationState,
                          AsyncDatabaseHelper mAsyncDatabaseHelper, Logger mLogger) {
        this.mUserController = Preconditions.checkNotNull(mUserController, "UserController cannot be null");
        this.mApplicationState = Preconditions.checkNotNull(mApplicationState, "ApplicationState cannot be null");
        this.mAsyncDatabaseHelper = Preconditions.checkNotNull(mAsyncDatabaseHelper, "AsyncDatabaseHelper cannot be null");
        this.mLogger = Preconditions.checkNotNull(mLogger, "Logger cannot be null");
    }

    @Override
    protected MainUiCallbacks createUICallback(MainUi ui) {
        return new MainUiCallbacks() {
            @Override
            public void setShowLoginPrompt() {

            }

            @Override
            public void addRequestLogin() {

            }
        };
    }

    @Override
    protected void onInited() {
        super.onInited();
        mApplicationState.registerForEvents(this);

        mUserController.init();
    }

    @Override
    protected void onSuspended() {
        super.onSuspended();
        mAsyncDatabaseHelper.close();
        mApplicationState.unregisterForEvents(this);

        mUserController.suspend();
    }

    public void attachDisplay(Display display) {
        Preconditions.checkNotNull(display, "Display is null");
        Preconditions.checkState(getDisplay() == null, "already have a display");
        setDisplay(display);
    }

    public void detachDisplay(Display display) {
        Preconditions.checkNotNull(display, "Display is null");
        Preconditions.checkState(getDisplay() == display, "Display didn't attach");
        setDisplay(null);
    }

    @Override
    public void setDisplay(Display display) {
        super.setDisplay(display);

        mUserController.setDisplay(display);
    }

    public UserController getUserController() {
        return mUserController;
    }
}
