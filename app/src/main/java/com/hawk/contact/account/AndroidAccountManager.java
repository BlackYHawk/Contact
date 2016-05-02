/*
 * Copyright 2014 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hawk.contact.account;

import android.accounts.Account;
import android.accounts.AccountManager;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.hawk.contact.Constants;
import com.hawk.contact.accounts.ContactAccountManager;
import com.hawk.contact.model.ContactAccount;

import java.util.ArrayList;
import java.util.List;


public class AndroidAccountManager implements ContactAccountManager {

    private final AccountManager mAccountManager;

    public AndroidAccountManager(AccountManager accountManager) {
        mAccountManager = Preconditions.checkNotNull(accountManager,
                "accountManager cannot be null");
    }

    @Override
    public List<ContactAccount> getAccounts() {
        final Account[] accounts = mAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        ArrayList<ContactAccount> philmAccounts = new ArrayList<>(accounts.length);

        for (int i = 0; i < accounts.length ; i++) {
            final Account account = accounts[i];

            String password = mAccountManager.getPassword(account);
            philmAccounts.add(new ContactAccount(account.name, password));
        }

        return philmAccounts;
    }

    
    @Override
    public void addAccount(ContactAccount account) {
        Account mAccount = new Account(account.getUsername(), Constants.ACCOUNT_TYPE);

        mAccountManager.addAccountExplicitly(mAccount, account.getPassword(), null);

        mAccountManager.setAuthToken(mAccount, account.getAuthToken(),
                account.getAuthTokenType());
    }

    @Override
    public void removeAccount(ContactAccount account) {
        Account mAccount = new Account(account.getUsername(), Constants.ACCOUNT_TYPE);
        mAccountManager.removeAccount(mAccount, null, null);
    }

    @Override
    public void updateAccount(ContactAccount account) {
        final Account[] accounts = mAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        for (int i = 0; i < accounts.length ; i++) {
            final Account mAccount = accounts[i];

            if (Objects.equal(mAccount.name, account.getUsername())) {
                mAccountManager.setPassword(mAccount, account.getPassword());
                return;
            }
        }
    }

}
