package contact.gojek.com.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import contact.gojek.com.DbResources.ContactTable;
import contact.gojek.com.Model.Contacts;

/**
 * Created by Pankaj on 12/02/17.
 */

public class ContactDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "contact.db";
    private static final int VERSION_NUMBER = 1;

    public ContactDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ContactTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertContacts(List<Contacts> contactList) {
        for (Contacts contact : contactList) {
            insertContact(contact.getContactsId(), contact.getFirstName(), contact.getLastName(),
                    contact.getProfilePic(), contact.getProfileUrl());
        }
    }

    public long insertContact(int contactId, String firstName, String lastName, String profilePicUrl,
                              String contactInfoUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactTable.COLUMN_ID, contactId);
        contentValues.put(ContactTable.COLUMN_FIRST_NAME, firstName);
        contentValues.put(ContactTable.COLUMN_LAST_NAME, lastName);
        contentValues.put(ContactTable.COLUMN_PROFILE_PIC, profilePicUrl);
        contentValues.put(ContactTable.COLUMN_CONTACT_INFO_URL, contactInfoUrl);
        return db.insert(ContactTable.TABLE_NAME, null, contentValues);
    }

    public long updateContact(int contactId, String firstName, String lastName, String profilePicUrl,
                              String contactInfoUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactTable.COLUMN_FIRST_NAME, firstName);
        contentValues.put(ContactTable.COLUMN_LAST_NAME, lastName);
        contentValues.put(ContactTable.COLUMN_PROFILE_PIC, profilePicUrl);
        contentValues.put(ContactTable.COLUMN_CONTACT_INFO_URL, contactInfoUrl);
        String whereClause = ContactTable.COLUMN_ID + "=?";
        String[] args = new String[]{contactId + ""};
        return db.update(ContactTable.TABLE_NAME, contentValues, whereClause, args);
    }

    public List<Contacts> getAllContacts() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Contacts> contactList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + ContactTable.TABLE_NAME, null);
        try {
            if ((cursor != null && cursor.getCount() > 0)) {
                while (cursor.moveToNext()) {
                    Contacts contact = new Contacts();
                    contact.setContactsId(cursor.getInt(cursor.getColumnIndex(ContactTable.COLUMN_ID)));
                    contact.setFirstName(cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_FIRST_NAME)));
                    contact.setLastName(cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_LAST_NAME)));
                    contact.setProfilePic(cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_PROFILE_PIC)));
                    contact.setProfileUrl(cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_CONTACT_INFO_URL)));
                    contactList.add(contact);
                }
            }
        } finally {
            if (cursor != null) {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }
        }
        return contactList;
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ContactTable.TABLE_NAME, null, null);
    }
}
