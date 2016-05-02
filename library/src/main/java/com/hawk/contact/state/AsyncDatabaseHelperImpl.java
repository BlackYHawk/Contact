package com.hawk.contact.state;

import com.google.common.base.Preconditions;
import com.hawk.contact.model.ContactUserProfile;
import com.hawk.contact.network.BackgroundCallRunnable;
import com.hawk.contact.util.BackgroundExecutor;

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
    public void getUserProfile(final String username, final Callback<ContactUserProfile> callback) {
        mBackgroundExecutor.execute(new DatabaseBackgroundRunnable<ContactUserProfile>() {
            @Override
            public ContactUserProfile doDatabaseCall(DatabaseHelper databaseHelper) {
                return mDatabaseHelper.getUserProfile(username);
            }

            @Override
            public void postExecute(ContactUserProfile result) {
                callback.onFinished(result);
            }
        });
    }

    @Override
    public void putUserProfile(final ContactUserProfile contactUserProfile) {
        mBackgroundExecutor.execute(new DatabaseBackgroundRunnable<Void>() {
            @Override
            public Void doDatabaseCall(DatabaseHelper databaseHelper) {
                mDatabaseHelper.putUserProfile(contactUserProfile);
                return null;
            }
        });
    }

    @Override
    public void deleteUserProfile(final ContactUserProfile contactUserProfile) {
        mBackgroundExecutor.execute(new DatabaseBackgroundRunnable<Void>() {
            @Override
            public Void doDatabaseCall(DatabaseHelper databaseHelper) {
                mDatabaseHelper.deleteUserProfile(contactUserProfile);
                return null;
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
