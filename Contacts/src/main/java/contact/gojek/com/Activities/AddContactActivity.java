package contact.gojek.com.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import contact.gojek.com.AsynTasks.ReceiveFileFromBitmapTask;
import contact.gojek.com.Model.ContactProfile;
import contact.gojek.com.Model.Contacts;
import contact.gojek.com.R;
import contact.gojek.com.Rest.API.ContactAPI;
import contact.gojek.com.Utils.DialogUtils;
import contact.gojek.com.Utils.ImageUtil;
import contact.gojek.com.Utils.ToastUtil;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AddContactActivity extends AppCompatActivity implements View.OnClickListener,
        ReceiveFileFromBitmapTask.ReceiveFileListener{

    public static final String PARAM_FIRST_NAME = "first_name";
    public static final String PARAM_LAST_NAME = "last_name";
    public static final String PARAM_NUMBER = "phone_number";
    public static final String PARAM_EMAIL = "email";
    public static final String PARAM_FAVORIE = "favorite";
    public static final String PARAM_PROFILE_PIC_URL = "profile_pic";

    Button btnSave;
    EditText etFirstName, etLastName, etNumber, etEmail;
    ImageView ivProfilePic;

    ProgressDialog progressDialog;
    File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        initialiseView();
        setListeners();
    }

    private void initialiseView() {
        btnSave = (Button) findViewById(R.id.btn_save);
        etFirstName = (EditText) findViewById(R.id.et_first_name);
        etLastName = (EditText) findViewById(R.id.et_last_name);
        etNumber = (EditText) findViewById(R.id.et_number);
        etEmail = (EditText) findViewById(R.id.et_email);
        ivProfilePic = (ImageView) findViewById(R.id.iv_profle_pic);
    }

    private void setListeners() {
        btnSave.setOnClickListener(this);
        ivProfilePic.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                saveContactInfo();
                break;
            case R.id.iv_profle_pic:
                showAttachDialog();
                break;
        }
    }

    private void saveContactInfo() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.saving_info));
        progressDialog.show();
        HashMap<String, Object> map = new HashMap<>();
        map.put(PARAM_FIRST_NAME, etFirstName.getText().toString());
        map.put(PARAM_LAST_NAME, etLastName.getText().toString());
        map.put(PARAM_NUMBER, etNumber.getText().toString());
        map.put(PARAM_EMAIL, etEmail.getText().toString());
        map.put(PARAM_FAVORIE, false);
        map.put(PARAM_PROFILE_PIC_URL, "/images/missing.png");
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8")
                ,(new JSONObject(map)).toString());
        Observable<ContactProfile> call = ContactAPI.getInstance().saveContactInfo(body);
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ContactProfile>() {
                    @Override
                    public void onCompleted() {
                        if(progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        ToastUtil.show(AddContactActivity.this, R.string.cannot_save_some_error_ocur);
                    }

                    @Override
                    public void onNext(ContactProfile o) {
                        ToastUtil.show(AddContactActivity.this, getString(R.string.contact_save_successfully));
                        finish();
                    }
                });
    }

    private void showAttachDialog() {
        CharSequence[] itemsArray = getResources().getStringArray(R.array.dlg_attach_types_array);
        Dialog dialog = DialogUtils.createSingleChoiceItemsDialog(this, getResources().getString(
                R.string.dlg_select_attach_type),
                itemsArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                String imageFileName = ImageUtil.getJPGFileName();
                                imageFile = new File(getExternalFilesDir(null), imageFileName);
                                Uri outputFileUri = Uri.fromFile(imageFile);
                                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                                startActivityForResult(intent, ImageUtil.CAPTURE_CALLED);
                                break;
                            case 1:
                                ImageUtil.getImage(AddContactActivity.this);
                                break;
                        }
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private boolean isGalleryCalled(int requestCode) {
        return ImageUtil.GALLERY_INTENT_CALLED == requestCode;
    }

    private boolean isCaptureCalled(int requestCode) {
        return ImageUtil.CAPTURE_CALLED == requestCode;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isFinishing())
            return;
        if ((isGalleryCalled(requestCode) || isCaptureCalled(requestCode)) && resultCode == RESULT_OK) {
            if (isCaptureCalled(requestCode)) {
                InputStream is = null;
                File file = imageFile;
                try {
                    is = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (is == null) {
                    try {
                        Uri u = data.getData();
                        is = getContentResolver().openInputStream(u);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                Bitmap img = BitmapFactory.decodeStream(is);
                if (file.exists()) {
                    onCachedImageFileReceived(img, file);
                } else {
                    onFileSelected(img);
                }
            } else {
                onFileSelected(data.getData());
            }
        }
    }

    protected void onFileSelected(Bitmap bitmap) {
        new ReceiveFileFromBitmapTask(this, this, bitmap, true).execute();
    }

    protected void onFileSelected(Uri originalUri) {
        Glide.with(this)
                .load(originalUri.toString())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        new ReceiveFileFromBitmapTask(AddContactActivity.this,
                                AddContactActivity.this, bitmap, true).execute();
                    }
                });
    }

    @Override
    public void onCachedImageFileReceived(Bitmap bitmap, File imageFile) {
        if(progressDialog == null)
            progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.uploading_image));
    }

    @Override
    public void onAbsolutePathExtFileReceived(String absolutePath) {

    }
}
