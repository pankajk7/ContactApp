package contact.gojek.com.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import contact.gojek.com.Model.ContactProfile;
import contact.gojek.com.Model.Contacts;
import contact.gojek.com.R;
import contact.gojek.com.Rest.API.ContactAPI;
import contact.gojek.com.Utils.ToastUtil;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ContactInfoActivity extends AppCompatActivity {

    private static final String ARGS_CONTACT = "contact";

    TextView tvName, tvNumber,tvEmail;
    ImageView ivProfilePic;

    Contacts contact;
    ContactProfile contactProfile;
    ProgressDialog progress;

    public static void startActivity(Context context, Contacts contact){
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
        fetchProfileInfo();
    }

    private void extractArguments(){
        Intent intent = getIntent();
        if(intent != null) {
            contact = (Contacts)intent.getSerializableExtra(ARGS_CONTACT);
        }
    }

    private void initialiseView() {
        tvName = (TextView) findViewById(R.id.tv_name);
        tvNumber = (TextView) findViewById(R.id.tv_number);
        tvEmail = (TextView) findViewById(R.id.tv_email);
        ivProfilePic = (ImageView) findViewById(R.id.iv_profle_pic);
    }

    private void fetchProfileInfo() {
        if(contact == null) return;

        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.fetching_profile_info));
        progress.show();
        Observable<ContactProfile> call = ContactAPI.getInstance()
                .getContactByContactId(contact.getContactsId());
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private void setViews() {
        tvName.setText(contactProfile.getFirstName() + " " + contactProfile.getLastName());
        tvNumber.setText(contactProfile.getPhoneNumber());
        tvEmail.setText(contactProfile.getEmail());
        Glide.with(this).load(contact.getProfilePic()).asBitmap().centerCrop()
                .placeholder(R.drawable.ic_profile_pic)
                .into(new BitmapImageViewTarget(ivProfilePic) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        ivProfilePic.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    Observer<ContactProfile> observer = new Observer<ContactProfile>() {
        @Override
        public void onCompleted() {
            if(progress!= null)
                progress.dismiss();
        }

        @Override
        public void onError(Throwable e) {
            if(progress!= null)
                progress.dismiss();
        }

        @Override
        public void onNext(ContactProfile object) {
            contactProfile = object;
            if(contactProfile == null) {
                ToastUtil.show(ContactInfoActivity.this, R.string.not_able_fetch_info);
                return;
            }
            setViews();
        }
    };
}
