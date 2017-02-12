package contact.gojek.com.AsynTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;

import contact.gojek.com.Utils.ImageUtil;

public class ReceiveFileFromBitmapTask extends AsyncTask<Object, Void, Object> {

    Context context;

    private ReceiveFileListener receiveFileListener;
    Bitmap bitmap;
    boolean isGettingFile;

    public ReceiveFileFromBitmapTask(Context context, ReceiveFileListener receiveFileListener, Bitmap bitmap,
                                     boolean isGettingFile) {
        this.context = context;
        this.receiveFileListener = receiveFileListener;
        this.bitmap = bitmap;
        this.isGettingFile = isGettingFile;
    }

    @Override
    protected Object doInBackground(Object... params) {
        File imageFile;
        String absolutePath;
        try {
            if (isGettingFile) {
                imageFile = ImageUtil.getOriginalImageFileFromBitmap(context, bitmap);
                return imageFile;
            } else {
                absolutePath = ImageUtil.getAbsolutePathByBitmap(context, bitmap, null);
                return absolutePath;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object object) {
        super.onPostExecute(object);
        final Object finalObject = object;
        new Runnable() {
            @Override
            public void run() {
                if (finalObject instanceof File) {
                    receiveFileListener.onCachedImageFileReceived(bitmap, (File) finalObject);
                } else if (finalObject instanceof String) {
                    receiveFileListener.onAbsolutePathExtFileReceived((String) finalObject);
                }
            }
        }.run();
    }

    public interface ReceiveFileListener {

        void onCachedImageFileReceived(Bitmap bitmap, File imageFile);

        void onAbsolutePathExtFileReceived(String absolutePath);
    }
}