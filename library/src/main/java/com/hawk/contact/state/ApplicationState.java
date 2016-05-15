package com.hawk.contact.state;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.hawk.contact.model.ContactAccount;
import com.hawk.contact.model.ContactPerson;
import com.squareup.otto.Bus;

/**
 * Created by heyong on 16/3/10.
 */
public class ApplicationState implements BaseState, UserState {

    private Bus mEventBus;
    private ContactAccount contactAccount;
    private ContactPerson contactPerson;
    private String username;

    public ApplicationState(Bus eventBus) {
        mEventBus = Preconditions.checkNotNull(eventBus, "EventBus cannot be null");
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
    public void registerForEvents(Object receiver) {
        mEventBus.register(receiver);
    }

    @Override
    public void unregisterForEvents(Object receiver) {
        mEventBus.unregister(receiver);
    }
}
