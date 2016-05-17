package com.hawk.contact.fragment;

import android.support.v4.app.Fragment;

import com.google.common.base.Preconditions;
import com.hawk.contact.controller.UserController;
import com.hawk.contact.fragment.base.BaseContactTabFragment;
import com.hawk.contact.util.StringManager;

import java.util.ArrayList;

/**
 * Created by heyong on 16/3/14.
 */
public class ContactTabFragment extends BaseContactTabFragment implements UserController.UserTabUi {

    private UserController.ContactTab[] mTabs;

    @Override
    public UserController.UserQueryType getUserQueryType() {
        return UserController.UserQueryType.DISCOVER;
    }

    @Override
    public String getRequestParameter() {
        return null;
    }

    @Override
    public boolean isModal() {
        return false;
    }

    @Override
    public void setTabs(UserController.ContactTab... tabs) {
        Preconditions.checkNotNull(tabs, "tabs cannot be null");
        mTabs = tabs;

        if(getPagerAdapter().getCount() != tabs.length) {
            ArrayList<Fragment> fragments = new ArrayList<>();
            for(int i=0; i<tabs.length; i++) {
                fragments.add(createFragmentForTab(tabs[i]));
            }
            setFragments(fragments);
        }
    }

    @Override
    protected String getTabTitle(int position) {
        if(mTabs != null) {
            return getString(StringManager.getStringResId(mTabs[position]));
        }
        return null;
    }

    private Fragment createFragmentForTab(UserController.ContactTab tab) {
        switch (tab) {
            case DIAL:
                return new ContactLibrayListFragment();
            case PEOPLE:
                return new ContactSearchListFragment();
        }
        return null;
    }
}
