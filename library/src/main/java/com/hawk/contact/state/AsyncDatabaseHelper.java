package com.hawk.contact.state;

import com.hawk.contact.model.ContactUserProfile;

/**
 * Created by Administrator on 2016/3/8.
 */
public interface AsyncDatabaseHelper {

    public void getUserProfile(String username, Callback<ContactUserProfile> callback);

    public void putUserProfile(ContactUserProfile contactUserProfile);

    public void deleteUserProfile(ContactUserProfile contactUserProfile);

    public void close();

    public interface Callback<T> {
        public void onFinished(T result);
    }

}
