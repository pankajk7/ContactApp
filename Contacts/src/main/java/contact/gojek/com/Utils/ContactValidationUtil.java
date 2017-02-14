package contact.gojek.com.Utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import contact.gojek.com.R;

/**
 * Created by Pankaj on 13/02/17.
 */

public class ContactValidationUtil {

    public static boolean isValidData(Context context, EditText etFirstName, EditText etNumber,
                                      EditText etLastName, EditText etEmail) {
        String firstName = etFirstName.getText().toString().trim();
        String number = etNumber.getText().toString().trim();
        if (TextUtils.isEmpty(firstName) && firstName.length() <= 3) {
            ToastUtil.show(context, R.string.first_name_validation_message);
            return false;
        }
        String mobilePattern = "^\\+{0,1}[0-9]{10,15}";
        Pattern pattern = Pattern.compile(mobilePattern);
        Matcher matcher = pattern.matcher(number);
        if (TextUtils.isEmpty(number) || !matcher.matches()) {
            ToastUtil.show(context, R.string.mobile_number_not_valid);
            return false;
        }

        if(TextUtils.isEmpty(etLastName.getText().toString().trim())){
            ToastUtil.show(context, R.string.please_enter_last_name);
            return false;
        }

        if(TextUtils.isEmpty(etEmail.getText().toString().trim())){
            ToastUtil.show(context, R.string.please_enter_email);
            return false;
        }
        return true;
    }
}
