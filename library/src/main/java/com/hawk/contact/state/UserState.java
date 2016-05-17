package com.hawk.contact.state;

import com.hawk.contact.model.ContactAccount;
import com.hawk.contact.model.ContactPerson;

import java.util.List;

/**
 * Created by Administrator on 2016/3/8.
 */
public interface UserState extends BaseState {

    public void setCurrentAccount(ContactAccount account);

    public void setUsername(String username);

    public List<ContactPerson> getLibrary();

    public void setLibrary(List<ContactPerson> library);

    public ContactPerson getContactPerson(String id);

    public ContactPerson getContactPerson(int id);

    public void putContactPerson(ContactPerson movie);

    public static class AccountChangedEvent {}

    public static class LibraryChangedEvent {}

}
