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

package com.hawk.contact.module.library;

import android.accounts.AccountManager;

import com.hawk.contact.account.AndroidAccountManager;
import com.hawk.contact.accounts.ContactAccountManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = ContextProvider.class,
        library = true
)
public class AccountsProvider {

    @Provides @Singleton
    public ContactAccountManager provideAccountManager(AccountManager androidAccountManager) {
        return new AndroidAccountManager(androidAccountManager);
    }

}
