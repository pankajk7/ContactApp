package contact.gojek.com.DbResources;

/**
 * Created by Pankaj on 12/02/17.
 */

public class ContactTable {

    public static final String TABLE_NAME = "contact";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_PROFILE_PIC = "profile_pic";
    public static final String COLUMN_CONTACT_INFO_URL = "url";

    public static final String CREATE_TABLE = String.format(
            "CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s, %s, %s, %s)",
            TABLE_NAME, COLUMN_ID, COLUMN_FIRST_NAME,
            COLUMN_LAST_NAME, COLUMN_PROFILE_PIC, COLUMN_CONTACT_INFO_URL);
}
