package com.hawk.contact.controller;

import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.hawk.contact.Display;
import com.hawk.contact.accounts.ContactAccountManager;
import com.hawk.contact.model.ContactAccount;
import com.hawk.contact.state.UserState;
import com.hawk.contact.util.BackgroundExecutor;
import com.hawk.contact.util.CollectionsUtil;
import com.hawk.contact.util.Logger;
import com.hawk.contact.util.StringFetcher;
import com.hawk.mylibrary.R;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 2016/3/8.
 */
public class UserController extends BaseUIController<UserController.UserUi, UserController.UserUiCallbacks> {

    private static final String USER_TAG = UserController.class.getSimpleName();

    public static enum Error {
        BAD_AUTH, BAD_CREATE
    }

    public interface UserUi extends BaseUIController.Ui<UserUiCallbacks> {
        void showLoadingProgress(boolean visible);

        void showError(Error error);
    }

    public interface UserUiCallbacks {
        void onTitleChanged(String newTitle);

        boolean isUsernameValid(String username);

        boolean isPasswordValid(String password);

        void login(String username, String password);

        void createUser(String username, String password);

        void requestReLogin();
    }

    private final UserState mUserState;
    private final BackgroundExecutor mBackgroundExecutor;
    private final ContactAccountManager mContactAccountManager;
    private final StringFetcher mStringFetcher;
    private final Logger mLogger;

    @Inject
    public UserController(UserState userState, BackgroundExecutor backgroundExecutor,
                          ContactAccountManager contactAccountManager, StringFetcher stringFetcher,
                          Logger logger) {
        super();
        mUserState = Preconditions.checkNotNull(userState, "userState cannot be null");
        mBackgroundExecutor = Preconditions.checkNotNull(backgroundExecutor, "backgroundExecutor cannot be null");
        mContactAccountManager = Preconditions.checkNotNull(contactAccountManager, "contactAccountManager cannot be null");
        mStringFetcher = Preconditions.checkNotNull(stringFetcher, "stringFetcher cannot be null");
        mLogger = Preconditions.checkNotNull(logger, "logger cannot be null");
    }

    @Override
    protected void onInited() {
        super.onInited();

        mUserState.registerForEvents(this);
        ContactAccount currentAccount = mUserState.getCurrentAccount();
        List<ContactAccount> accounts = mContactAccountManager.getAccounts();

        if(currentAccount == null) {
            if(!CollectionsUtil.isEmpty(accounts)) {
                mUserState.setCurrentAccount(accounts.get(0));
            }
        }
        else {
            boolean found = false;
            for(int i=0; i<accounts.size(); i++) {
                if(currentAccount.getUsername().equals(accounts.get(i).getUsername())) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                mUserState.setCurrentAccount(null);
            }
        }
    }

    @Subscribe
    public void onAccountChanged(UserState.AccountChangedEvent event) {
        ContactAccount currentAccount = mUserState.getCurrentAccount();

        if(currentAccount != null) {
            final String username = currentAccount.getUsername();
            mUserState.setUsername(username);

        }
        else {
            mUserState.setUsername(null);
        }

        mLogger.d(USER_TAG, "onAccountChanged : " + mUserState.getUsername());
    }

    @Override
    protected String getUiTitle(UserUi ui) {
        if(ui instanceof UserUi) {
            return mStringFetcher.getString(R.string.account_login);
        }
        return null;
    }

    @Override
    protected void onSuspended() {
        super.onSuspended();
        mUserState.unregisterForEvents(this);
    }

    @Override
    protected UserUiCallbacks createUICallback(UserUi ui) {
        return new UserUiCallbacks() {
            @Override
            public void onTitleChanged(String newTitle) {
                updateDisplayTitle(newTitle);
            }

            @Override
            public boolean isUsernameValid(String username) {
                return !TextUtils.isEmpty(username);
            }

            @Override
            public boolean isPasswordValid(String password) {
                return !TextUtils.isEmpty(password);
            }

            @Override
            public void login(String username, String password) {

            }

            @Override
            public void createUser(String username, String password) {

            }

            @Override
            public void requestReLogin() {
                Display display = getDisplay();
                if(display != null) {
                    display.startAddAccountActivity();
                }
            }
        };
    }
}
