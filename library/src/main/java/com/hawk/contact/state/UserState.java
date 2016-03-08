package com.hawk.contact.state;

import com.hawk.contact.model.ContactAccount;

/**
 * Created by Administrator on 2016/3/8.
 */
public interface UserState extends BaseState {

    public static class AccountChangedEvent {

    }

    public void setCurrentAccount(ContactAccount account);

    public void setUsername(String username);

}
