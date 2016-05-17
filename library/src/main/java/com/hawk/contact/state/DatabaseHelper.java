package com.hawk.contact.state;

import com.hawk.contact.model.ContactPerson;

import java.util.Collection;
import java.util.List;

/**
 * Created by heyong on 16/3/10.
 */
public interface DatabaseHelper {

    List<ContactPerson> getContactPersonlist();

    ContactPerson getPerson(String username);

    void putPerson(ContactPerson contactPerson);

    void deletePerson(ContactPerson contactPerson);

    void put(Collection<ContactPerson> contactPersons);

    void delete(Collection<ContactPerson> contactPersons);

    void deleteAllContactPersons();

    List<ContactPerson> fetchSystemContact();

    void close();

    boolean isClosed();

}
