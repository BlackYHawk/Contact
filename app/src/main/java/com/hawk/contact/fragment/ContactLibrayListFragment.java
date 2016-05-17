package com.hawk.contact.fragment;

import com.hawk.contact.controller.UserController;
import com.hawk.contact.fragment.base.ContactListFragment;

/**
 * Created by heyong on 16/5/2.
 */
public class ContactLibrayListFragment extends ContactListFragment implements UserController.UserListUi {

    @Override
    public UserController.UserQueryType getUserQueryType() {
        return UserController.UserQueryType.LIBRARY;
    }
}
