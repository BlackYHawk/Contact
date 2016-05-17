package com.hawk.contact.controller;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.hawk.contact.Display;
import com.hawk.contact.accounts.ContactAccountManager;
import com.hawk.contact.lib.R;
import com.hawk.contact.model.ColorScheme;
import com.hawk.contact.model.ContactAccount;
import com.hawk.contact.model.ContactPerson;
import com.hawk.contact.model.ListItem;
import com.hawk.contact.qualifiers.GeneralPurpose;
import com.hawk.contact.state.AsyncDatabaseHelper;
import com.hawk.contact.state.UserState;
import com.hawk.contact.util.BackgroundExecutor;
import com.hawk.contact.util.CollectionsUtil;
import com.hawk.contact.util.Logger;
import com.hawk.contact.util.StringFetcher;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private final AsyncDatabaseHelper mDbHelper;
    private final StringFetcher mStringFetcher;
    private final Logger mLogger;

    private boolean mPopulatedLibraryFromDb = false;

    private ControllerCallbacks mControllerCallbacks;

    @Inject
    public UserController(UserState userState, @GeneralPurpose BackgroundExecutor backgroundExecutor,
                          ContactAccountManager contactAccountManager, AsyncDatabaseHelper mAsyncDatabaseHelper,
                          StringFetcher stringFetcher, Logger logger) {
        super();
        mUserState = Preconditions.checkNotNull(userState, "userState cannot be null");
        mBackgroundExecutor = Preconditions.checkNotNull(backgroundExecutor, "backgroundExecutor cannot be null");
        mContactAccountManager = Preconditions.checkNotNull(contactAccountManager, "contactAccountManager cannot be null");
        mDbHelper = Preconditions.checkNotNull(mAsyncDatabaseHelper, "AsyncDatabaseHelper cannot be null");
        mStringFetcher = Preconditions.checkNotNull(stringFetcher, "stringFetcher cannot be null");
        mLogger = Preconditions.checkNotNull(logger, "logger cannot be null");
    }

    @Override
    protected void onInited() {
        super.onInited();
        mUserState.registerForEvents(this);

        populateStateFromDb();
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

    @Override
    protected void onUiAttached(final UserUi ui) {
        final UserQueryType queryType = ui.getUserQueryType();

        if (queryType.requireLogin()) {
            return;
        }

        String title = null;
        String subtitle = null;

        final int callingId = getId(ui);

        switch (queryType) {
            case LIBRARY:
                fetchLibraryIfNeeded(callingId);
                break;
        }

        final Display display = getDisplay();
        if (display != null) {
            if (!ui.isModal()) {
                display.showUpNavigation(queryType != null && queryType.showUpNavigation());
                display.setColorScheme(getColorSchemeForUi(ui));
            }
            display.setActionBarSubtitle(subtitle);
        }
    }

    @Override
    protected void populateUi(UserUi ui) {

        if(ui instanceof UserTabUi) {
            populateTabUi((UserTabUi)ui);
        } else if (ui instanceof UserListUi) {
            populateUserListUi((UserListUi) ui);
        }
    }

    private void populateTabUi(UserTabUi userTabUi) {
        userTabUi.setTabs(ContactTab.DIAL, ContactTab.PEOPLE);
    }

    private void populateUserListUi(UserListUi ui) {
        final UserQueryType queryType = ui.getUserQueryType();

        Set<UserFilter> filters = null;

/*        if (isLoggedIn()) {
            if (queryType.supportFiltering()) {
                ui.setFiltersVisibility(true);
                filters = mMoviesState.getFilters();
                ui.showActiveFilters(filters);
            }
        } else {
            ui.setFiltersVisibility(false);
        }*/

        List<ContactPerson> items = null;

        List<UserFilter> sections = queryType.getSections();
        List<UserFilter> sectionProcessingOrder = queryType.getSectionsProcessingOrder();

        switch (queryType) {
/*            case TRENDING:
                items = mUserState.getTrending();
                break;
            case POPULAR:
                MoviesState.MoviePaginatedResult popular = mUserState.getPopular();
                if (popular != null) {
                    items = popular.items;
                }
                break;*/
            case LIBRARY:
                items = mUserState.getLibrary();
                break;
        }

        if (!CollectionsUtil.isEmpty(items)) {
            // Always filter movies (for adult)
        //    items = filterContactPersons(items, filters);
        }

        if (items == null) {
            ui.setItems(null);
        } else if (CollectionsUtil.isEmpty(sections)) {
            ui.setItems(createListItemList(items));
/*
            if (isLoggedIn()) {
                ui.allowedBatchOperations(MovieOperation.MARK_SEEN,
                        MovieOperation.ADD_TO_COLLECTION, MovieOperation.ADD_TO_WATCHLIST);
            } else {
                ui.disableBatchOperations();
            }*/
        } else {
            ui.setItems(createSectionedListItemList(items, sections, sectionProcessingOrder));
        }
    }


    private void fetchLibraryIfNeeded(final int callingId) {
        if (mPopulatedLibraryFromDb && CollectionsUtil.isEmpty(mUserState.getLibrary())) {
         //   fetchLibrary(callingId);
        }
    }

    private void populateStateFromDb() {
        if (CollectionsUtil.isEmpty(mUserState.getLibrary())) {
            mDbHelper.getLibrary(new LibraryDbLoadCallback());
        }
    }

    private ColorScheme getColorSchemeForUi(UserUi ui) {
        switch (ui.getUserQueryType()) {
            case LIBRARY:
            case WATCHLIST:
            case RECOMMENDED:
                ContactPerson contactPerson = mUserState.getContactPerson(ui.getRequestParameter());
                if (contactPerson != null) {
          //          return contactPerson.getColorScheme();
                }
                break;
        }

        return null;
    }

    private <T extends ListItem<T>> List<ListItem<T>> createListItemList(final List<T> items) {
        Preconditions.checkNotNull(items, "items cannot be null");
        ArrayList<ListItem<T>> listItems = new ArrayList<>(items.size());
        for (ListItem<T> item : items) {
            listItems.add(item);
        }
        return listItems;
    }

    private <T extends ListItem<T>, F extends Filter<T>> List<ListItem<T>> createSectionedListItemList(
            final List<T> items,
            final List<F> sections,
            List<F> sectionProcessingOrder) {
        Preconditions.checkNotNull(items, "items cannot be null");
        Preconditions.checkNotNull(sections, "sections cannot be null");

        if (sectionProcessingOrder != null) {
            Preconditions.checkArgument(sections.size() == sectionProcessingOrder.size(),
                    "sections and sectionProcessingOrder must be the same size");
        } else {
            sectionProcessingOrder = sections;
        }

        final List<ListItem<T>> result = new ArrayList<>(items.size());
        final HashSet<T> movies = new HashSet<>(items);

        Map<F, List<ListItem<T>>> sectionsItemLists = null;

        for (F filter : sectionProcessingOrder) {
            List<ListItem<T>> sectionItems = null;

            for (Iterator<T> i = movies.iterator(); i.hasNext(); ) {
                T item = i.next();
                if (item != null && filter.isFiltered(item)) {
                    if (sectionItems == null) {
                        sectionItems = new ArrayList<>();
                        sectionItems.add(filter);
                    }
                    sectionItems.add(item);
                    i.remove();
                }
            }

            if (!CollectionsUtil.isEmpty(sectionItems)) {
                if (sectionsItemLists == null) {
                    sectionsItemLists = new ArrayMap<>();
                }
                filter.sortListItems(sectionItems);
                sectionsItemLists.put(filter, sectionItems);
            }
        }

        if (sectionsItemLists != null) {
            for (F filter : sections) {
                if (sectionsItemLists.containsKey(filter)) {
                    result.addAll(sectionsItemLists.get(filter));
                }
            }
        }

        return result;
    }

    private List<ContactPerson> filterContactPersons(List<ContactPerson> movies, Set<UserFilter> filters) {
        Preconditions.checkNotNull(movies, "movies cannot be null");

        ArrayList<ContactPerson> filteredMovies = new ArrayList<>(movies.size());
        for (ContactPerson movie : movies) {
            boolean included = true;

            if (!CollectionsUtil.isEmpty(filters)) {
                for (UserFilter filter : filters) {
                    if (filter.isFiltered(movie)) {
                        included = false;
                        break;
                    }
                }
            }

            if (included) {
                included = false;
            }

            if (included) {
                filteredMovies.add(movie);
            }
        }
        return filteredMovies;
    }

    private void prefetchLibraryIfNeeded() {
        UserUi ui = findUiFromQueryType(UserQueryType.LIBRARY);
        fetchLibraryIfNeeded(ui != null ? getId(ui) : 0);
    }

    private UserUi findUiFromQueryType(UserQueryType queryType) {
        for (UserUi ui : getUis()) {
            if (ui.getUserQueryType() == queryType) {
                return ui;
            }
        }
        return null;
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

    public static enum UserQueryType {
        LIBRARY, DISCOVER, WATCHLIST, RECOMMENDED, SEARCH, SEARCH_PEOPLE,
        PERSON_DETAIL, PERSON_RELATED, PERSON_IMAGES,
        NONE;

        private static final List<UserFilter> USERLIST_SECTIONS_DISPLAY = Arrays.asList(
                UserFilter.COLLECTION, UserFilter.SEEN, UserFilter.UNSEEN);
        private static final List<UserFilter> USERLIST_SECTIONS_PROCESSING = Arrays.asList(
                UserFilter.COLLECTION, UserFilter.SEEN, UserFilter.UNSEEN);

        public boolean requireLogin() {
            switch (this) {
           //     case LIBRARY:
                case WATCHLIST:
                case RECOMMENDED:
                    return true;
                default:
                    return false;
            }
        }

        public boolean supportFiltering() {
            switch (this) {
                case LIBRARY:
                case WATCHLIST:
                case RECOMMENDED:
                    return true;
                default:
                    return false;
            }
        }

        public boolean showUpNavigation() {
            switch (this) {
                case PERSON_IMAGES:
                case PERSON_DETAIL:
                case SEARCH_PEOPLE:
                    return true;
                default:
                    return false;
            }
        }

        public List<UserFilter> getSections() {
            switch (this) {
                case WATCHLIST:
                    return USERLIST_SECTIONS_DISPLAY;
            }
            return null;
        }

        public List<UserFilter> getSectionsProcessingOrder() {
            switch (this) {
                case WATCHLIST:
                    return USERLIST_SECTIONS_PROCESSING;
            }
            return null;
        }
    }

    public interface UserUi extends BaseUIController.Ui<UserUiCallbacks> {
        void showLoadingProgress(boolean visible);

        void showError(Error error);

        UserQueryType getUserQueryType();

        String getRequestParameter();
    }

    public interface UserTabUi extends UserUi {
        void setTabs(ContactTab... tabs);
    }

    public interface BaseUserListUi<E> extends UserUi {
        void setItems(List<ListItem<E>> items);
    }

    public interface UserListUi extends BaseUserListUi<ContactPerson> {
        void setFiltersVisibility(boolean visible);

        void showActiveFilters(Set<UserFilter> filters);
    }

    public interface SearchPersonUi extends UserListUi {}

    private class LibraryDbLoadCallback implements AsyncDatabaseHelper.Callback<List<ContactPerson>> {

        @Override
        public void onFinished(List<ContactPerson> result) {
            mUserState.setLibrary(result);
            if (!CollectionsUtil.isEmpty(result)) {
                for (ContactPerson contactPerson : result) {
                    mUserState.putContactPerson(contactPerson);
                }
            }
            mPopulatedLibraryFromDb = true;

            prefetchLibraryIfNeeded();
        }
    }
}
