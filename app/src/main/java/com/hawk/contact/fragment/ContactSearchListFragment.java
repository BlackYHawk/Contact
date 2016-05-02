package com.hawk.contact.fragment;

import com.hawk.contact.controller.UserController;
import com.hawk.contact.fragment.base.ContactListFragment;

/**
 * Created by heyong on 16/5/2.
 */
public class ContactSearchListFragment extends ContactListFragment implements UserController.SearchPersonUi {

    @Override
    public UserController.UserQueryType getUserQueryType() {
        return UserController.UserQueryType.SEARCH_PEOPLE;
    }
}
