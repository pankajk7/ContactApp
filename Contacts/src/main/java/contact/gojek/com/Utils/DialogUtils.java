package contact.gojek.com.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class DialogUtils {
    public static Dialog createSingleChoiceItemsDialog(Context context, String title,
                                                       CharSequence[] itemsArray,
                                                       DialogInterface.OnClickListener singleChoiceOnClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setItems(itemsArray, singleChoiceOnClickListener);
        return builder.create();
    }
}