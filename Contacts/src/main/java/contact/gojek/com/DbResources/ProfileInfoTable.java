package contact.gojek.com.DbResources;

/**
 * Created by Pankaj on 13/02/17.
 */

public class ProfileInfoTable {

    public static final String TABLE_NAME = "profile";

    public static final String COLUMN_PROFILE_ID = "id";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_PROFILE_PIC = "profile_pic";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_FAVORITE = "favorite";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";


    public static final String CREATE_TABLE = String.format(
            "CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s, %s, %s, %s, %s INTEGER," +
                    "%s, %s)",
            TABLE_NAME, COLUMN_PROFILE_ID, COLUMN_FIRST_NAME,
            COLUMN_LAST_NAME, COLUMN_PROFILE_PIC, COLUMN_PHONE_NUMBER,
            COLUMN_FAVORITE, COLUMN_CREATED_AT, COLUMN_UPDATED_AT);
}
