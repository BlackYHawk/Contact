package com.hawk.contact.adapters;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.hawk.contact.R;
import com.hawk.contact.base.ContactApplication;
import com.hawk.contact.model.ContactPerson;
import com.hawk.contact.model.ListItem;
import com.hawk.contact.widget.ContactImageView;

import java.text.DateFormat;
import java.util.Date;

import javax.inject.Inject;

/**
 * Created by heyong on 16/4/26.
 */
public class ContactSectionedListAdapter extends BaseContactSectionedListAdapter<ContactPerson> {

    private static final String LOG_TAG = ContactSectionedListAdapter.class.getSimpleName();

    @Inject
    DateFormat mMediumDateFormatter;
    private final Date mDate;

    public ContactSectionedListAdapter(Activity activity) {
        super(activity, R.layout.item_list_3line, R.layout.item_list_contact_section_header);
        mDate = new Date();
        ContactApplication.from(activity).inject(this);
    }

    @Override
    protected void bindView(int position, View view, ListItem<ContactPerson> item) {
        ContactPerson person = item.getListItem();

        final TextView title = (TextView) view.findViewById(R.id.textview_title);
        if (person.getAge() > 0) {
            title.setText(mActivity.getString(R.string.person_title_year,
                    person.getName(), person.getAge()));
        } else {
            title.setText(person.getName());
        }

        final TextView ratingTextView = (TextView) view.findViewById(R.id.textview_subtitle_1);
        ratingTextView.setText(mActivity.getString(R.string.person_rating_votes,
                person.getAge(), person.getName()));

        final TextView release = (TextView) view.findViewById(R.id.textview_subtitle_2);
        mDate.setTime(person.getDateOfAdd()!= null ? person.getDateOfAdd().getTime() : 0);
        release.setText(mActivity.getString(R.string.person_add_date,
                mMediumDateFormatter.format(mDate)));

        final ContactImageView imageView = (ContactImageView) view.findViewById(R.id.imageview_poster);
     //   imageView.loadPoster(person);
    }
}
