package com.hawk.contact.accounts;

import com.hawk.contact.model.ContactAccount;

import java.util.List;

/**
 * Created by Administrator on 2016/3/8.
 */
public interface ContactAccountManager {

    public List<ContactAccount> getAccounts();

    public void addAccount(ContactAccount account);

    public void removeAccount(ContactAccount account);

    public void updateAccount(ContactAccount account);

}
