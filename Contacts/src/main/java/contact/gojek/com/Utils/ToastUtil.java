package contact.gojek.com.Utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Pankaj on 12/02/17.
 */

public class ToastUtil {

    public static void show(Context context, int resourceId){
        Toast.makeText(context, resourceId, Toast.LENGTH_LONG).show();
    }
}
