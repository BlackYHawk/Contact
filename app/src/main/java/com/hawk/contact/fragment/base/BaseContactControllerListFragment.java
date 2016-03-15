package com.hawk.contact.fragment.base;

import android.widget.AbsListView;

import com.hawk.contact.controller.UserController;

/**
 * Created by heyong on 16/3/15.
 */
public abstract class BaseContactControllerListFragment<E extends AbsListView, T>
    extends ListFragment<E> implements UserController.BaseUserListUi, AbsListView.OnScrollListener {



}
