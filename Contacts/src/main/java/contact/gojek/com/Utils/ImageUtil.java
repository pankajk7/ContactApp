package contact.gojek.com.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Pankaj on 12/02/17.
 */

public class ImageUtil {

    public static final int GALLERY_INTENT_CALLED = 1;
    public static final int CAPTURE_CALLED = 3;
    private static final String JPG_EXTENSION = ".jpg";
    private static final String PNG_EXTENSION = ".png";

    public static String fileName(String extension) {
        return System.currentTimeMillis() + "Go-Jek" + extension;
    }

    public static String fileName() {
        return fileName(PNG_EXTENSION);
    }

    public static String getJPGFileName() {
        return fileName(JPG_EXTENSION);
    }

    public static void getImage(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            activity.startActivityForResult(intent, GALLERY_INTENT_CALLED);
        } else {
            showKitKatGallery(activity);
        }
    }

    private static void showKitKatGallery(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        activity.startActivityForResult(intent, GALLERY_INTENT_CALLED);
    }

    public static File getOriginalImageFileFromBitmap(Context context, Bitmap origBitmap) throws IOException {
        File tempFile = new File(context.getExternalFilesDir(null), fileName());
        tempFile.createNewFile();
        OutputStream os = new BufferedOutputStream(new FileOutputStream(tempFile));
        // Not compressing images now. We might want to start compressing high res images.
        origBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        os.close();
        return tempFile;
    }

    public static String getAbsolutePathByBitmap(Context context, Bitmap origBitmap, File file) {
        if (file == null)
            file = new File(context.getExternalFilesDir(null), fileName());
        ByteArrayOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            bos = new ByteArrayOutputStream();
            origBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bitmapData = bos.toByteArray();
            fos = new FileOutputStream(file);
            fos.write(bitmapData);
            closeOutputStream(fos);
            closeOutputStream(bos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeOutputStream(fos);
            closeOutputStream(bos);
        }
        return file.getAbsolutePath();
    }

    public static void closeOutputStream(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
