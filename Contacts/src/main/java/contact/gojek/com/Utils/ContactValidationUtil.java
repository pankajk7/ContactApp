package contact.gojek.com.Utils;

import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import contact.gojek.com.R;

/**
 * Created by Pankaj on 13/02/17.
 */

public class ContactValidationUtil {

    public static boolean isValidData(Context context, EditText etFirstName, EditText etNumber) {
        String firstName = etFirstName.getText().toString().trim();
        String number = etNumber.getText().toString().trim();
        if (TextUtils.isEmpty(firstName) && firstName.length() <= 3) {
            etFirstName.setError(context.getString(R.string.first_name_validation_message));
            return false;
        }
        String mobilePattern = "^\\+{0,1}[0-9]{10,15}";
        Pattern pattern = Pattern.compile(mobilePattern);
        Matcher matcher = pattern.matcher(number);
        if (TextUtils.isEmpty(number) || !matcher.matches()) {
            etNumber.setError(context.getString(R.string.mobile_number_not_valid));
            return false;
        }
        return true;
    }
}
