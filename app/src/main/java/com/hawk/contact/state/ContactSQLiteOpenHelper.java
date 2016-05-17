/*
 * Copyright 2014 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hawk.contact.state;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.hawk.contact.Constants;
import com.hawk.contact.model.ContactPerson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nl.qbusict.cupboard.DatabaseCompartment;
import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class ContactSQLiteOpenHelper extends SQLiteOpenHelper implements DatabaseHelper {

    private static String LOG_TAG = ContactSQLiteOpenHelper.class.getSimpleName();

    private Context context;
    private static final String DATABASE_NAME = "contact.db";
    private static final int DATABASE_VERSION = 1;
    private static final int LAST_DATABASE_NUKE_VERSION = 28;

    private static final Class[] ENTITIES = new Class[]{ContactPerson.class};

    static {
        // register our models
        for (Class clazz : ENTITIES) {
            cupboard().register(clazz);
        }
    }

    private boolean mIsClosed;

    public ContactSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public final void onCreate(SQLiteDatabase db) {
        // this will ensure that all tables are created
        cupboard().withDatabase(db).createTables();

        // TODO: add indexes and other database tweaks
    }

    @Override
    public final void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < LAST_DATABASE_NUKE_VERSION) {
            if (Constants.DEBUG) {
                Log.d(LOG_TAG, "Nuking Database. Old Version: " + oldVersion);
            }
            cupboard().withDatabase(db).dropAllTables();
            onCreate(db);
        } else {
            // this will upgrade tables, adding columns and new tables.
            // Note that existing columns will not be converted
            cupboard().withDatabase(db).upgradeTables();
        }
    }

    @Override
    public List<ContactPerson> getContactPersonlist() {
        return queryContactPersons(null);
    }

    @Override
    public ContactPerson getPerson(String username) {
        assetNotClosed();

        try {
            return cupboard().withDatabase(getReadableDatabase())
                    .query(ContactPerson.class)
                    .withSelection("username = ?", username)
                    .get();
        } catch (Exception e) {
          //  Crashlytics.logException(e);
            return null;
        }
    }

    @Override
    public void putPerson(ContactPerson contactPerson) {
        assetNotClosed();
        try {
            cupboard().withDatabase(getWritableDatabase()).put(contactPerson);
        } catch (Exception e) {
       //     Crashlytics.logException(e);
        }
    }

    @Override
    public void deletePerson(ContactPerson contactPerson) {
        assetNotClosed();
        try {
            cupboard().withDatabase(getWritableDatabase()).delete(contactPerson);
        } catch (Exception e) {
            //   Crashlytics.logException(e);
        }
    }

    @Override
    public void put(Collection<ContactPerson> contactPersons) {
        assetNotClosed();
        try {
            cupboard().withDatabase(getWritableDatabase()).put(contactPersons);
        } catch (Exception e) {
       //     Crashlytics.logException(e);
        }
    }

    @Override
    public void delete(Collection<ContactPerson> contactPersons) {
        assetNotClosed();

        SQLiteDatabase db = null;

        try {
            db = getWritableDatabase();
            db.beginTransaction();
            final DatabaseCompartment dbc = cupboard().withDatabase(db);
            for (ContactPerson movie : contactPersons) {
                dbc.delete(movie);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
        //    Crashlytics.logException(e);
        } finally {
            if (db != null) {
                db.endTransaction();
            }
        }
    }

    @Override
    public void deleteAllContactPersons() {
        assetNotClosed();
        try {
            final int numDeleted = cupboard().withDatabase(getWritableDatabase()).
                    delete(ContactPerson.class, null);
            if (Constants.DEBUG) {
                Log.d(LOG_TAG, "deleteAllContactPersons. Deleted " + numDeleted + " rows.");
            }
        } catch (Exception e) {
            //      Crashlytics.logException(e);
        }
    }

    private static final List<String> PROJECTION = new ArrayList<String>() {{
        add(ContactsContract.Contacts.Data.RAW_CONTACT_ID);
        add(ContactsContract.Contacts.Data.MIMETYPE);
        add(ContactsContract.Profile.DISPLAY_NAME);
        add(ContactsContract.CommonDataKinds.Contactables.PHOTO_URI);
        add(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME);
        add(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
        add(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME);
        add(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME);
        add(ContactsContract.CommonDataKinds.Phone.NUMBER);
        add(ContactsContract.CommonDataKinds.Phone.TYPE);
        add(ContactsContract.CommonDataKinds.Phone.LABEL);
        add(ContactsContract.CommonDataKinds.Email.DATA);
        add(ContactsContract.CommonDataKinds.Email.ADDRESS);
        add(ContactsContract.CommonDataKinds.Email.TYPE);
        add(ContactsContract.CommonDataKinds.Email.LABEL);
    }};

    @Override
    public List<ContactPerson> fetchSystemContact() {
        List<ContactPerson> contactPersonList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
//        Cursor contactCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
//
//        while (contactCursor.moveToNext()) {
//            String rawContactId = "";
//            String contactId = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts._ID));
//
//            Cursor contactRawCursor = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI, null,
//                    ContactsContract.RawContacts.CONTACT_ID+"=?", new String[]{contactId}, null);
//
//            if(contactRawCursor.moveToFirst()) {
//                rawContactId = contactRawCursor.getString(contactRawCursor.getColumnIndex(ContactsContract.RawContacts._ID));
//            }
//            contactRawCursor.close();
//
//
//
//        }
//        contactCursor.close();

        Cursor dataCursor = contentResolver.query(ContactsContract.Data.CONTENT_URI, PROJECTION.toArray(new String[PROJECTION.size()]),
                ContactsContract.Data.MIMETYPE + "=? OR " + ContactsContract.Data.MIMETYPE + "=? OR " + ContactsContract.Data.MIMETYPE + "=?",
                new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE}, null);

        Map<Long, ContactPerson> contactPersonMap = loadContactPerson(dataCursor);

        dataCursor.close();
        
        contactPersonList.addAll(contactPersonMap.values());


        return contactPersonList;
    }

    private Map<Long, ContactPerson> loadContactPerson(Cursor cursor) {
        Map<Long, ContactPerson> map = new LinkedHashMap<>();

        while (cursor.moveToNext()) {
            int idColumnIndex = cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID);
            long contactId = cursor.getLong(idColumnIndex);


            if (!map.containsKey(contactId)) {
                map.put(contactId, new ContactPerson());
            }

            ContactPerson contact = map.get(contactId);

            int typeColumnIndex = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);
            int nameColumnIndex = cursor.getColumnIndex(ContactsContract.Profile.DISPLAY_NAME);
            int photoColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.PHOTO_URI);
            String name = cursor.getString(nameColumnIndex);
            String photoUri = cursor.getString(photoColumnIndex);

            contact.set_id(String.valueOf(contactId));
            contact.setName(name);
            contact.setPhotoUrl(photoUri);

            String mimeType = cursor.getString(typeColumnIndex);

            if (mimeType.equals(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)) {
                String givenName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                String middleName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME));
                String familyName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
            } else if (mimeType.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                int type = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                if (!TextUtils.isEmpty(phoneNumber)) {
                    String label;
                    switch (type) {
                        case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                            label = "家庭电话";
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                            label = "工作电话";
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                            label = "手机号";
                            break;
                        default:
                            label = "其他";
                    }
                    contact.phones.add(new ContactPerson.Item(label, phoneNumber));
                }
            } else if (mimeType.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                String email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                int type = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                if (!TextUtils.isEmpty(email)) {
                    String label;
                    switch (type) {
                        case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
                            label = "家庭电话";
                            break;
                        case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
                            label = "工作电话";
                            break;
                        case ContactsContract.CommonDataKinds.Email.TYPE_MOBILE:
                            label = "手机号";
                            break;
                        case ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM:
                            if (cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL)) != null) {
                                label = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL)).toLowerCase();
                            } else {
                                label = "";
                            }
                            break;
                        default:
                            label = "其他";
                    }
                    contact.emails.add(new ContactPerson.Item(label, email));
                }
            }
        }
        return map;
    }

    @Override
    public synchronized void close() {
        mIsClosed = true;
        super.close();
    }

    @Override
    public boolean isClosed() {
        return mIsClosed;
    }

    private void assetNotClosed() {
        Preconditions.checkState(!mIsClosed, "Database is closed");
    }

    private List<ContactPerson> queryContactPersons(String selection, String... selectionArgs) {
        assetNotClosed();
        QueryResultIterable<ContactPerson> itr = null;

        try {
            itr = cupboard().withDatabase(getReadableDatabase()).query(ContactPerson.class)
                    .withSelection(selection, selectionArgs)
                    .query();
        } finally {
            if (itr != null) {
                itr.close();
                itr = null;
            }
        }

        return itr != null ? itr.list() : null;
    }

}
