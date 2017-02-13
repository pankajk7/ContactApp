package contact.gojek.com.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import contact.gojek.com.DbResources.ProfileInfoTable;
import contact.gojek.com.Model.ContactProfile;

/**
 * Created by Pankaj on 13/02/17.
 */

public class ProfileInfoDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "contact_info.db";
    private static final int VERSION_NUMBER = 1;

    public ProfileInfoDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ProfileInfoTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long insertProfileInfo(ContactProfile contactProfile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProfileInfoTable.COLUMN_PROFILE_ID, contactProfile.getProfileId());
        contentValues.put(ProfileInfoTable.COLUMN_FIRST_NAME, contactProfile.getFirstName());
        contentValues.put(ProfileInfoTable.COLUMN_LAST_NAME, contactProfile.getLastName());
        contentValues.put(ProfileInfoTable.COLUMN_EMAIL, contactProfile.getEmail());
        contentValues.put(ProfileInfoTable.COLUMN_PROFILE_PIC, contactProfile.getProfilePic());
        contentValues.put(ProfileInfoTable.COLUMN_FAVORITE, contactProfile.isFavorite()? 1 : 0);
        contentValues.put(ProfileInfoTable.COLUMN_CREATED_AT, contactProfile.getCreatedAt());
        contentValues.put(ProfileInfoTable.COLUMN_UPDATED_AT, contactProfile.getUpdatedAt());
        return db.insert(ProfileInfoTable.TABLE_NAME, null, contentValues);
    }

    public long updateProfileInfo(ContactProfile contactProfile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProfileInfoTable.COLUMN_FIRST_NAME, contactProfile.getFirstName());
        contentValues.put(ProfileInfoTable.COLUMN_LAST_NAME, contactProfile.getLastName());
        contentValues.put(ProfileInfoTable.COLUMN_EMAIL, contactProfile.getEmail());
        contentValues.put(ProfileInfoTable.COLUMN_PROFILE_PIC, contactProfile.getProfilePic());
        contentValues.put(ProfileInfoTable.COLUMN_FAVORITE, contactProfile.isFavorite()? 1 : 0);
        contentValues.put(ProfileInfoTable.COLUMN_CREATED_AT, contactProfile.getCreatedAt());
        contentValues.put(ProfileInfoTable.COLUMN_UPDATED_AT, contactProfile.getUpdatedAt());
        String whereCaluse = ProfileInfoTable.COLUMN_PROFILE_ID + "=?";
        String[] args = new String[]{contactProfile.getProfileId() + ""};
        return db.update(ProfileInfoTable.TABLE_NAME, contentValues, whereCaluse, args);
    }

    public ContactProfile getContactInfo(int profileId){
        SQLiteDatabase db = this.getReadableDatabase();
        ContactProfile contactProfile = new ContactProfile();
        String selection = ProfileInfoTable.COLUMN_PROFILE_ID + "=" + profileId;
        Cursor cursor = db.rawQuery("select * from "+ ProfileInfoTable.TABLE_NAME
                + " where " + selection, null);
        try {
            if ((cursor != null && cursor.getCount() > 0)) {
                cursor.moveToFirst();
                contactProfile.setProfileId(cursor.getInt(cursor.
                        getColumnIndex(ProfileInfoTable.COLUMN_PROFILE_ID)));
                contactProfile.setFirstName(cursor.getString(cursor.
                        getColumnIndex(ProfileInfoTable.COLUMN_FIRST_NAME)));
                contactProfile.setLastName(cursor.getString(cursor.
                        getColumnIndex(ProfileInfoTable.COLUMN_LAST_NAME)));
                contactProfile.setPhoneNumber(cursor.getString(cursor.
                        getColumnIndex(ProfileInfoTable.COLUMN_PHONE_NUMBER)));
                contactProfile.setEmail(cursor.getString(cursor.
                        getColumnIndex(ProfileInfoTable.COLUMN_EMAIL)));
                contactProfile.setFavorite(cursor.getInt(cursor.
                        getColumnIndex(ProfileInfoTable.COLUMN_FAVORITE)) == 1 ? true : false);
                contactProfile.setProfilePic(cursor.getString(cursor.
                        getColumnIndex(ProfileInfoTable.COLUMN_PROFILE_PIC)));
                contactProfile.setCreatedAt(cursor.getString(cursor.
                        getColumnIndex(ProfileInfoTable.COLUMN_CREATED_AT)));
                contactProfile.setUpdatedAt(cursor.getString(cursor.
                        getColumnIndex(ProfileInfoTable.COLUMN_UPDATED_AT)));
                return contactProfile;
            }
        }finally {
            if (cursor != null) {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }
        }
        return null;
    }
}
