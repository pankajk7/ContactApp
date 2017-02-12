package contact.gojek.com.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import contact.gojek.com.DbResources.ContactTable;
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
        contentValues.put(ProfileInfoTable.COLUMN_PROFILE_PIC, contactProfile.getProfilePic());
        contentValues.put(ProfileInfoTable.COLUMN_FAVORITE, contactProfile.isFavorite()? 1 : 0);
        contentValues.put(ProfileInfoTable.COLUMN_CREATED_AT, contactProfile.getCreatedAt());
        contentValues.put(ProfileInfoTable.COLUMN_UPDATED_AT, contactProfile.getUpdatedAt());
        String whereCaluse = ProfileInfoTable.COLUMN_PROFILE_ID + "=?";
        String[] args = new String[]{contactProfile.getProfileId() + ""};
        return db.update(ProfileInfoTable.TABLE_NAME, contentValues, whereCaluse, args);
    }
}
