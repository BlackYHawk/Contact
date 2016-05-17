package com.hawk.contact.state;

import android.support.v4.util.ArrayMap;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.hawk.contact.model.ContactAccount;
import com.hawk.contact.model.ContactPerson;
import com.hawk.contact.util.TextUtils;
import com.squareup.otto.Bus;

import java.util.List;
import java.util.Map;

/**
 * Created by heyong on 16/3/10.
 */
public final class ApplicationState implements BaseState, UserState {

    private static final int INITIAL_MOVIE_MAP_CAPACITY = 200;
    private Bus mEventBus;
    private ContactAccount contactAccount;
    private List<ContactPerson> mLibrary;
    private Map<String, ContactPerson> mIdContactPerson;
    private String username;

    public ApplicationState(Bus eventBus) {
        mEventBus = Preconditions.checkNotNull(eventBus, "EventBus cannot be null");
        mIdContactPerson = new ArrayMap<>(INITIAL_MOVIE_MAP_CAPACITY);
    }

    @Override
    public void setCurrentAccount(ContactAccount account) {
        if(!Objects.equal(contactAccount, account)) {
            contactAccount = account;
            mEventBus.post(new AccountChangedEvent());
        }
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public ContactAccount getCurrentAccount() {
        return contactAccount;
    }

    @Override
    public List<ContactPerson> getLibrary() {
        return mLibrary;
    }

    @Override
    public void setLibrary(List<ContactPerson> items) {
        if (!Objects.equal(items, mLibrary)) {
            mLibrary = items;
            mEventBus.post(new LibraryChangedEvent());
        }
    }

    @Override
    public ContactPerson getContactPerson(final String id) {
        ContactPerson contactPerson = mIdContactPerson.get(id);

        return contactPerson;
    }

    @Override
    public ContactPerson getContactPerson(int id) {
        return getContactPerson(String.valueOf(id));
    }

    @Override
    public void putContactPerson(ContactPerson contactPerson) {
        if (!TextUtils.isEmpty(contactPerson.get_id())) {
            mIdContactPerson.put(contactPerson.get_id(), contactPerson);
        }
    }

    @Override
    public void registerForEvents(Object receiver) {
        mEventBus.register(receiver);
    }

    @Override
    public void unregisterForEvents(Object receiver) {
        mEventBus.unregister(receiver);
    }
}
