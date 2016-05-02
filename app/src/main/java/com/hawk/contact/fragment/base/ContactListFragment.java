package com.hawk.contact.fragment.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.hawk.contact.R;
import com.hawk.contact.adapters.ContactSectionedListAdapter;
import com.hawk.contact.model.ContactPerson;
import com.hawk.contact.model.ListItem;

import java.util.List;

/**
 * Created by heyong on 16/4/24.
 */
public abstract class ContactListFragment extends BaseContactListFragment<ListView> {

    private ContactSectionedListAdapter mContactListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContactListAdapter = new ContactSectionedListAdapter(getActivity());
        setListAdapter(mContactListAdapter);
    }

    @Override
    public void setItems(List<ListItem<ContactPerson>> listItems) {
        mContactListAdapter.setItems(listItems);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (hasCallbacks()) {
            ListItem<ContactPerson> item = (ListItem<ContactPerson>) l.getItemAtPosition(position);
            if (item.getListType() == ListItem.TYPE_ITEM) {
/*                getCallbacks().showMovieDetail(item.getListItem(),
                        ActivityTransitions.scaleUpAnimation(v));*/
            }
        }
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
    protected ListView createListView(Context context, LayoutInflater inflater) {
        return (ListView) inflater.inflate(R.layout.view_pinned_list, null);
    }
}
