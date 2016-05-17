package com.hawk.contact.state;

import com.hawk.contact.model.ContactPerson;

import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 2016/3/8.
 */
public interface AsyncDatabaseHelper {

    void getContactPersonlist(Callback<List<ContactPerson>> callback);

    void getPerson(String username, Callback<ContactPerson> callback);

    void putPerson(ContactPerson contactPerson);

    void deletePerson(ContactPerson contactPerson);

    void put(Collection<ContactPerson> contactPersons);

    void delete(Collection<ContactPerson> contactPersons);

    void deleteAllContactPersons();

    void getLibrary(Callback<List<ContactPerson>> callback);

    void close();

    interface Callback<T> {
        void onFinished(T result);
    }

}
