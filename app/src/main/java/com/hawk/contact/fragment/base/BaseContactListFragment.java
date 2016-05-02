package com.hawk.contact.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.AbsListView;

import com.hawk.contact.controller.UserController;
import com.hawk.contact.model.ContactPerson;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by heyong on 16/4/24.
 */
public abstract class BaseContactListFragment<E extends AbsListView> extends
        BaseContactControllerListFragment<E, ContactPerson> implements UserController.UserListUi,
        AbsListView.OnScrollListener {

    private Set<UserController.UserFilter> mFilters;
    private boolean mFiltersItemVisible;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mFilters = new HashSet<>();
    }

    @Override
    public void showActiveFilters(Set<UserController.UserFilter> filters) {
        mFilters = filters;
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void setFiltersVisibility(boolean visible) {
        if (mFiltersItemVisible != visible) {
            mFiltersItemVisible = visible;
            getActivity().invalidateOptionsMenu();
        }
    }
}
