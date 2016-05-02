package com.hawk.contact.controller;

import android.content.Intent;

import com.google.common.base.Preconditions;
import com.hawk.contact.Display;
import com.hawk.contact.state.ApplicationState;
import com.hawk.contact.state.AsyncDatabaseHelper;
import com.hawk.contact.util.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by heyong on 16/3/10.
 */
@Singleton
public class MainController extends BaseUIController<MainController.MainControllerUi,
        MainController.MainControllerUiCallbacks> {

    private static final String LOG_TAG = MainController.class.getSimpleName();


    public interface HostCallbacks {
        void finish();

        void setAccountAuthenticatorResult(String username, String authToken, String accountType);
    }

    public interface MainControllerUi extends BaseUIController.Ui<MainControllerUiCallbacks> {

    }

    public interface MainUi extends MainControllerUi {
        void showLoginPrompt();
    }

    public interface MainControllerUiCallbacks {
        void addAccountRequested();

        void setShownLoginPrompt();
    }

    private UserController mUserController;
    private ApplicationState mApplicationState;
    private AsyncDatabaseHelper mAsyncDatabaseHelper;
    private Logger mLogger;
    private HostCallbacks mHostCallbacks;

    @Inject
    public MainController(UserController mUserController, ApplicationState mApplicationState,
                          AsyncDatabaseHelper mAsyncDatabaseHelper, Logger mLogger) {
        this.mUserController = Preconditions.checkNotNull(mUserController, "UserController cannot be null");
        this.mApplicationState = Preconditions.checkNotNull(mApplicationState, "ApplicationState cannot be null");
        this.mAsyncDatabaseHelper = Preconditions.checkNotNull(mAsyncDatabaseHelper, "AsyncDatabaseHelper cannot be null");
        this.mLogger = Preconditions.checkNotNull(mLogger, "Logger cannot be null");

        mUserController.setControllerCallbacks(new UserController.ControllerCallbacks() {
            @Override
            public void onAddAccountCompleted(String username, String authToken, String authTokenType) {
                if (mHostCallbacks != null) {
                    mHostCallbacks.setAccountAuthenticatorResult(username, authToken, authTokenType);
                    mHostCallbacks.finish();
                }
            }
        });
    }

    @Override
    public boolean handleIntent(Intent intent) {
        mLogger.d(LOG_TAG, "handleIntent: " + intent);

        return mUserController.handleIntent(intent);
    }

    @Override
    protected void onInited() {
        super.onInited();
        mApplicationState.registerForEvents(this);

        mUserController.init();
    }

    @Override
    protected MainControllerUiCallbacks createUICallback(MainControllerUi ui) {
        return new MainControllerUiCallbacks() {

            @Override
            public void addAccountRequested() {

            }

            @Override
            public void setShownLoginPrompt() {

            }
        };
    }

    @Override
    protected void onSuspended() {
        mUserController.suspend();

        mAsyncDatabaseHelper.close();
        mApplicationState.unregisterForEvents(this);

        super.onSuspended();
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

    public void setHostCallbacks(HostCallbacks hostCallbacks) {
        mHostCallbacks = hostCallbacks;
    }

    public final UserController getUserController() {
        return mUserController;
    }
}
