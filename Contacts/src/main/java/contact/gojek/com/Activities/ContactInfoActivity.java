package contact.gojek.com.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.json.JSONObject;

import java.util.HashMap;

import contact.gojek.com.Database.ContactDatabaseHelper;
import contact.gojek.com.Database.ProfileInfoDatabaseHelper;
import contact.gojek.com.DbResources.ProfileInfoTable;
import contact.gojek.com.Model.ContactProfile;
import contact.gojek.com.Model.Contacts;
import contact.gojek.com.R;
import contact.gojek.com.Rest.API.ContactAPI;
import contact.gojek.com.Rest.NetworkObserver;
import contact.gojek.com.Utils.NetworkAvailable;
import contact.gojek.com.Utils.ToastUtil;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class ContactInfoActivity extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private static final String ARGS_CONTACT = "contact";

    Button btnRetry;
    CheckBox cbName;
    ImageView ivProfilePic;
    LinearLayout llMainView, llFailureView;
    TextView tvNumber, tvEmail;

    Contacts contact;
    ContactProfile contactProfile;
    ProgressDialog progress;
    private Subscription subscription;

    public static void startActivity(Context context, Contacts contact) {
        Intent intent = new Intent(context, ContactInfoActivity.class);
        intent.putExtra(ARGS_CONTACT, contact);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);
        extractArguments();
        initialiseView();
        setListeners();
        checkDbAndGetInfo();
    }

    private void extractArguments() {
        Intent intent = getIntent();
        if (intent != null) {
            contact = (Contacts) intent.getSerializableExtra(ARGS_CONTACT);
        }
    }

    private void initialiseView() {
        btnRetry = (Button) findViewById(R.id.btn_retry);
        cbName = (CheckBox) findViewById(R.id.cb_name);
        llMainView = (LinearLayout) findViewById(R.id.ll_main_content);
        llFailureView = (LinearLayout) findViewById(R.id.ll_failure);
        ivProfilePic = (ImageView) findViewById(R.id.iv_profle_pic);
        tvNumber = (TextView) findViewById(R.id.tv_number);
        tvEmail = (TextView) findViewById(R.id.tv_email);
    }

    private void setListeners() {
        btnRetry.setOnClickListener(this);
        cbName.setOnCheckedChangeListener(this);
        tvNumber.setOnClickListener(this);
        tvEmail.setOnClickListener(this);
    }

    private void checkDbAndGetInfo() {
        if (contact == null) return;

        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
        if (progress == null)
            progress = new ProgressDialog(ContactInfoActivity.this);
        progress.setMessage(getString(R.string.fetching_profile_info));
        progress.show();

        // Starting Background Thread using RxJava for db operation
        subscription = Observable.defer(new Func0<Observable<ContactProfile>>() {
            @Override
            public Observable<ContactProfile> call() {
                return Observable.just(new ProfileInfoDatabaseHelper(ContactInfoActivity.this)
                        .getContactInfo(contact.getContactsId()));
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkObserver<ContactProfile>() {
                    @Override
                    public void onNext(ContactProfile object) {
                        if (object == null) {
                            if (NetworkAvailable.isNetworkAvailable(ContactInfoActivity.this)) {
                                fetchProfileInfo();
                            } else {
                                llMainView.setVisibility(View.GONE);
                                llFailureView.setVisibility(View.VISIBLE);
                            }
                            return;
                        }
                        contactProfile = object;
                        setViews();
                        if (NetworkAvailable.isNetworkAvailable(ContactInfoActivity.this))
                            fetchProfileInfo();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (progress != null && progress.isShowing()) {
                            progress.dismiss();
                            progress = null;
                        }
                        fetchProfileInfo();
                    }

                    @Override
                    public void onCompleted() {
                        if (progress != null && progress.isShowing()) {
                            progress.dismiss();
                            progress = null;
                        }
                    }
                });
    }

    private void fetchProfileInfo() {
        if (contact == null) return;

        if (progress == null)
            progress = new ProgressDialog(ContactInfoActivity.this);
        progress.setMessage(getString(R.string.fetching_profile_info));
        progress.show();
        Observable<ContactProfile> call = ContactAPI.getInstance()
                .getContactByContactId(contact.getContactsId());
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private void setViews() {
        llMainView.setVisibility(View.VISIBLE);
        llFailureView.setVisibility(View.GONE);
        cbName.setText(contactProfile.getFirstName() + " " + contactProfile.getLastName());
        // For not to auto call OnCheckedListener when calling setChecked()
        cbName.setOnCheckedChangeListener(null);
        cbName.setChecked(contactProfile.isFavorite());
        // Setting listener again after auto setChecked()
        cbName.setOnCheckedChangeListener(this);
        tvNumber.setText(contactProfile.getPhoneNumber());
        tvEmail.setText(contactProfile.getEmail());
        Glide.with(ContactInfoActivity.this).load(contact.getProfilePic()).asBitmap().centerCrop()
                .placeholder(R.drawable.ic_profile_pic)
                .into(new BitmapImageViewTarget(ivProfilePic) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        ivProfilePic.setImageDrawable(circularBitmapDrawable);
                        if (progress != null && progress.isShowing()) {
                            progress.dismiss();
                            progress = null;
                        }
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        if (progress != null && progress.isShowing()) {
                            progress.dismiss();
                            progress = null;
                        }
                    }
                });
    }

    Observer<ContactProfile> observer = new Observer<ContactProfile>() {
        @Override
        public void onCompleted() {
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
                progress = null;
            }
        }

        @Override
        public void onError(Throwable e) {
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
                progress = null;
            }
            llMainView.setVisibility(View.GONE);
            llFailureView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onNext(ContactProfile object) {
            contactProfile = object;
            if (contactProfile == null) {
                llMainView.setVisibility(View.GONE);
                llFailureView.setVisibility(View.VISIBLE);
                return;
            }
            ProfileInfoDatabaseHelper db = new ProfileInfoDatabaseHelper(ContactInfoActivity.this);
            ContactProfile objContactProfile = db.getContactInfo(object.getProfileId());
            if (objContactProfile != null)
                db.updateProfileInfo(contactProfile);
            else
                db.insertProfileInfo(contactProfile);
            setViews();
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
                progress = null;
            }
        }
    };

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_retry:
                checkDbAndGetInfo();
                break;
            case R.id.tv_number:
                intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                        "tel", tvNumber.getText().toString(), null));
                startActivity(intent);
                break;
            case R.id.tv_email:
                intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", tvEmail.getText().toString(), null));
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "subject text");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, "some body text");
                startActivity(Intent.createChooser(intent, "Send email"));
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        saveFavorite(isChecked);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
            progress = null;
        }
    }

    private void saveFavorite(boolean isFavorite) {
        progress = new ProgressDialog(ContactInfoActivity.this);
        progress.setMessage(getString(R.string.fetching_profile_info));
        progress.show();
        HashMap<String, Object> map = new HashMap<>(1);
        map.put(AddContactActivity.PARAM_FAVORIE, isFavorite);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8")
                , (new JSONObject(map)).toString());
        Observable<ContactProfile> call = ContactAPI.getInstance()
                .saveFavorite(contact.getContactsId(), body);
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
