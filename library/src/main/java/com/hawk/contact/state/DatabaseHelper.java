package com.hawk.contact.state;

import com.hawk.contact.model.ContactUserProfile;

/**
 * Created by heyong on 16/3/10.
 */
public interface DatabaseHelper {

    public ContactUserProfile getUserProfile(String username);

    public void putUserProfile(ContactUserProfile contactUserProfile);

    public void deleteUserProfile(ContactUserProfile contactUserProfile);

    void close();

    boolean isClosed();

}
