package com.hawk.contact;

import com.hawk.contact.model.ColorScheme;

/**
 * Created by heyong on 16/3/8.
 */
public interface Display {

    public boolean hasMainFragment();

    public void showMainFragment();

    public void startAddAccountActivity();

    public void setActionBarTitle(CharSequence title);    //设置标题名

    public void setActionBarSubtitle(CharSequence title);

    public void showUpNavigation(boolean show);

    public void setColorScheme(ColorScheme colorScheme);

    public void setSupportActionBar(Object toolbar, boolean handleBackground);

}
