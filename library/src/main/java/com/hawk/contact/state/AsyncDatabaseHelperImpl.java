package com.hawk.contact.state;

import com.google.common.base.Preconditions;
import com.hawk.contact.model.ContactPerson;
import com.hawk.contact.network.BackgroundCallRunnable;
import com.hawk.contact.util.BackgroundExecutor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/3/8.
 */
public class AsyncDatabaseHelperImpl implements AsyncDatabaseHelper {

    private BackgroundExecutor mBackgroundExecutor;
    private DatabaseHelper mDatabaseHelper;

    public AsyncDatabaseHelperImpl(BackgroundExecutor backgroundExecutor, DatabaseHelper databaseHelper) {
        mBackgroundExecutor = Preconditions.checkNotNull(backgroundExecutor, "BackgroundExecutor cannot be null");
        mDatabaseHelper = Preconditions.checkNotNull(databaseHelper, "DatabaseHelper cannot be null");
    }

    @Override
    public void getContactPersonlist(final Callback<List<ContactPerson>> callback) {
        mBackgroundExecutor.execute(new DatabaseBackgroundRunnable<List<ContactPerson>>() {
            @Override
            public List<ContactPerson> doDatabaseCall(DatabaseHelper dbHelper) {
                List<ContactPerson> library = dbHelper.getContactPersonlist();
                if (library != null) {
                    Collections.sort(library, ContactPerson.COMPARATOR_SORT_TITLE);
                }
                return library;
            }

            @Override
            public void postExecute(List<ContactPerson> result) {
                callback.onFinished(result);
            }
        });
    }

    @Override
    public void getPerson(final String username, final Callback<ContactPerson> callback) {
        mBackgroundExecutor.execute(new DatabaseBackgroundRunnable<ContactPerson>() {
            @Override
            public ContactPerson doDatabaseCall(DatabaseHelper databaseHelper) {
                return mDatabaseHelper.getPerson(username);
            }

            @Override
            public void postExecute(ContactPerson result) {
                callback.onFinished(result);
            }
        });
    }

    @Override
    public void putPerson(final ContactPerson contactPerson) {
        mBackgroundExecutor.execute(new DatabaseBackgroundRunnable<Void>() {
            @Override
            public Void doDatabaseCall(DatabaseHelper databaseHelper) {
                mDatabaseHelper.putPerson(contactPerson);
                return null;
            }
        });
    }

    @Override
    public void deletePerson(final ContactPerson contactPerson) {
        mBackgroundExecutor.execute(new DatabaseBackgroundRunnable<Void>() {
            @Override
            public Void doDatabaseCall(DatabaseHelper databaseHelper) {
                mDatabaseHelper.deletePerson(contactPerson);
                return null;
            }
        });
    }

    @Override
    public void put(final Collection<ContactPerson> contactPersons) {
        mBackgroundExecutor.execute(new DatabaseBackgroundRunnable<Void>() {
            @Override
            public Void doDatabaseCall(DatabaseHelper dbHelper) {
                dbHelper.delete(contactPersons);
                return null;
            }
        });
    }

    @Override
    public void delete(final Collection<ContactPerson> contactPersons) {
        mBackgroundExecutor.execute(new DatabaseBackgroundRunnable<Void>() {
            @Override
            public Void doDatabaseCall(DatabaseHelper dbHelper) {
                dbHelper.delete(contactPersons);
                return null;
            }
        });
    }

    @Override
    public void deleteAllContactPersons() {
        mBackgroundExecutor.execute(new DatabaseBackgroundRunnable<Void>() {
            @Override
            public Void doDatabaseCall(DatabaseHelper dbHelper) {
                dbHelper.deleteAllContactPersons();
                return null;
            }
        });
    }

    @Override
    public void getLibrary(final Callback<List<ContactPerson>> callback) {
        mBackgroundExecutor.execute(new DatabaseBackgroundRunnable<List<ContactPerson>>() {
            @Override
            public List<ContactPerson> doDatabaseCall(DatabaseHelper dbHelper) {
                List<ContactPerson> library = dbHelper.fetchSystemContact();
                if (library != null) {
                    Collections.sort(library, ContactPerson.COMPARATOR_SORT_TITLE);
                }
                return library;
            }

            @Override
            public void postExecute(List<ContactPerson> result) {
                callback.onFinished(result);
            }
        });
    }

    @Override
    public void close() {
        mDatabaseHelper.close();
    }

    private abstract class DatabaseBackgroundRunnable<R> extends BackgroundCallRunnable<R> {

        @Override
        public R runAsync() {
            final DatabaseHelper databaseHelper = mDatabaseHelper;
            if(databaseHelper.isClosed()) {
                return null;
            }
            
            return doDatabaseCall(databaseHelper);
        }

        public abstract R doDatabaseCall(DatabaseHelper databaseHelper);
    }

}
