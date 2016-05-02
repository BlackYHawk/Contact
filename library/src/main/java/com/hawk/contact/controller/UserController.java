package com.hawk.contact.controller;

import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.hawk.contact.Display;
import com.hawk.contact.accounts.ContactAccountManager;
import com.hawk.contact.lib.R;
import com.hawk.contact.model.ContactAccount;
import com.hawk.contact.model.ContactPerson;
import com.hawk.contact.model.ListItem;
import com.hawk.contact.qualifiers.GeneralPurpose;
import com.hawk.contact.state.UserState;
import com.hawk.contact.util.BackgroundExecutor;
import com.hawk.contact.util.CollectionsUtil;
import com.hawk.contact.util.Logger;
import com.hawk.contact.util.StringFetcher;
import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Administrator on 2016/3/8.
 */
@Singleton
public class UserController extends BaseUIController<UserController.UserUi, UserController.UserUiCallbacks> {

    private static final String USER_TAG = UserController.class.getSimpleName();

    public static enum Error {
        BAD_AUTH, BAD_CREATE
    }

    interface ControllerCallbacks {
        void onAddAccountCompleted(String username, String authToken, String authTokenType);
    }

    public interface UserUi extends BaseUIController.Ui<UserUiCallbacks> {
        void showLoadingProgress(boolean visible);

        void showError(Error error);

        String getRequestParameter();
    }

    public interface UserUiCallbacks {

        void onTitleChanged(String newTitle);

        boolean isUsernameValid(String username);

        boolean isPasswordValid(String password);

        void login(String username, String password);

        void createUser(String username, String password);

        void onScrolledToBottom();

        void requestReLogin();

        String getUiTitle();
    }

    private final UserState mUserState;
    private final BackgroundExecutor mBackgroundExecutor;
    private final ContactAccountManager mContactAccountManager;
    private final StringFetcher mStringFetcher;
    private final Logger mLogger;
    private ControllerCallbacks mControllerCallbacks;

    @Inject
    public UserController(UserState userState, @GeneralPurpose BackgroundExecutor backgroundExecutor,
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

    @Override
    protected void populateUi(UserUi ui) {

        if(ui instanceof UserTabUi) {
            populateTabUi((UserTabUi)ui);
        }
    }

    private void populateTabUi(UserTabUi userTabUi) {
        userTabUi.setTabs(ContactTab.DIAL, ContactTab.PEOPLE);
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

    void setControllerCallbacks(ControllerCallbacks controllerCallbacks) {
        mControllerCallbacks = controllerCallbacks;
    }

    @Override
    protected void onSuspended() {
        super.onSuspended();
        mUserState.unregisterForEvents(this);
    }

    @Override
    protected UserUiCallbacks createUICallback(final UserUi ui) {
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
            public void onScrolledToBottom() {

            }

            @Override
            public void requestReLogin() {
                Display display = getDisplay();
                if(display != null) {
                    display.startAddAccountActivity();
                }
            }

            @Override
            public String getUiTitle() {
                return UserController.this.getUiTitle(ui);
            }
        };
    }

    public static enum ContactTab {
        DIAL, PEOPLE
    }

    public interface Filter<T> extends ListItem<T> {
        boolean isFiltered(T item);
        void sortListItems(List<ListItem<T>> items);
    }

    public static enum UserFilter implements Filter<ContactPerson> {
        /**
         * Filters {@link ContactPerson} that are in the user's collection.
         */
        COLLECTION,
        /**
         * Filters {@link ContactPerson} that have been watched by the user.
         */
        SEEN,
        /**
         * Filters {@link ContactPerson} that have not been watched by the user.
         */
        UNSEEN;

        @Override
        public boolean isFiltered(ContactPerson person) {
            Preconditions.checkNotNull(person, "person cannot be null");
            switch (this) {
                case COLLECTION:
                    return person.inCollection();
                case SEEN:
                    return person.isWatched();
                case UNSEEN:
                    return !person.isWatched();
            }
            return false;
        }

        @Override
        public void sortListItems(List<ListItem<ContactPerson>> listItems) {
            switch (this) {
                default:
                    Collections.sort(listItems, ContactPerson.COMPARATOR_LIST_ITEM_DATE_ASC);
                    break;
            }
        }

        @Override
        public int getListType() {
            return ListItem.TYPE_SECTION;
        }

        @Override
        public ContactPerson getListItem() {
            return null;
        }

        @Override
        public int getListSectionTitle() {
            switch (this) {
                case SEEN:
                    return R.string.filter_seen;
                case UNSEEN:
                    return R.string.filter_unseen;
            }
            return 0;
        }
    }

    public interface UserTabUi extends UserUi {
        void setTabs(ContactTab... tabs);
    }

    public interface BaseUserListUi<E> extends UserUi {
        void setItems(List<ListItem<E>> items);
    }

    public interface UserListUi extends BaseUserListUi<ContactPerson> {

    }

}
