package contact.gojek.com.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import contact.gojek.com.Adapters.ContactListAdapter;
import contact.gojek.com.Model.Contacts;
import contact.gojek.com.R;
import contact.gojek.com.Rest.API.ContactAPI;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ContactListActivity extends AppCompatActivity implements View.OnClickListener{

    FloatingActionButton fab;
    RecyclerView rvContacts;

    ContactListAdapter contactListAdapter;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        initialiseView();
        setListeners();
        getAllContacts();
    }

    private void getAllContacts() {
        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.fetching_profile_info));
        progress.show();
        Observable<List<Contacts>> call = ContactAPI.getInstance().getAllContacts();
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    Observer<List<Contacts>> observer = new Observer<List<Contacts>>() {

        @Override
        public void onNext(List<Contacts> contactList) {
            if (contactList == null) return;
            Collections.sort(contactList, new Comparator<Contacts>() {
                @Override
                public int compare(Contacts contact1, Contacts contact2) {
                    return contact1.getFirstName().compareToIgnoreCase(contact2.getFirstName());
                }
            });
            setAdapter(contactList);
        }

        @Override
        public void onCompleted() {
            if(progress != null && progress.isShowing())
                progress.dismiss();
        }

        @Override
        public void onError(Throwable e) {
            if(progress != null && progress.isShowing())
                progress.dismiss();
        }
    };

    private void initialiseView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rvContacts = (RecyclerView) findViewById(R.id.rv_contact_list);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void setListeners() {
        fab.setOnClickListener(this);
    }

    private void setAdapter(List<Contacts> contactList) {
        contactListAdapter = new ContactListAdapter(this, contactList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvContacts.setLayoutManager(mLayoutManager);
        rvContacts.setAdapter(contactListAdapter);
    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(this, AddContactActivity.class));
    }
}
