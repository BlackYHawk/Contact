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


import android.content.Context;

import com.hawk.contact.qualifier.ApplicationContext;
import com.hawk.contact.qualifiers.ForDatabase;
import com.hawk.contact.state.AsyncDatabaseHelper;
import com.hawk.contact.state.AsyncDatabaseHelperImpl;
import com.hawk.contact.state.DatabaseHelper;
import com.hawk.contact.state.ContactSQLiteOpenHelper;
import com.hawk.contact.util.BackgroundExecutor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        includes = {
                ContextProvider.class,
                UtilProvider.class
        }
)
public class PersistenceProvider {

    @Provides @Singleton
    public DatabaseHelper getDatabaseHelper(@ApplicationContext Context context) {
        return new ContactSQLiteOpenHelper(context);
    }

    @Provides @Singleton
    public AsyncDatabaseHelper getAsyncDatabaseHelper(
            @ForDatabase BackgroundExecutor executor,
            DatabaseHelper databaseHelper) {
        return new AsyncDatabaseHelperImpl(executor, databaseHelper);
    }


}
