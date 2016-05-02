package com.hawk.contact.model;

/**
 * Created by heyong on 16/4/24.
 */
public abstract class ContactModel<T> implements ListItem<T> {

    @Override
    public int getListType() {
        return ListItem.TYPE_ITEM;
    }

    @Override
    public T getListItem() {
        return (T)this;
    }

    @Override
    public int getListSectionTitle() {
        return 0;
    }
}
