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
import contact.gojek.com.Database.ContactDatabaseHelper;
import contact.gojek.com.Model.Contacts;
import contact.gojek.com.R;
import contact.gojek.com.Rest.API.ContactAPI;
import contact.gojek.com.Rest.NetworkObserver;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class ContactListActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 1;
    public static final String ARGS_CONTACT = "contact";

    FloatingActionButton fab;
    RecyclerView rvContacts;

    ContactListAdapter contactListAdapter;
    ProgressDialog progress;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        initialiseView();
        setListeners();
        checkForDbAndGetData();
    }

    // Fetching contact data from db if there is any else calling API to fetch from server
    private void checkForDbAndGetData() {
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
        if (progress == null)
            progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.fetching_profile_info));
        progress.show();

        // Starting Background Thread using RxJava for db operation
        subscription = Observable.defer(new Func0<Observable<List<Contacts>>>() {

            @Override
            public Observable<List<Contacts>> call() {
                return Observable.just(new ContactDatabaseHelper(ContactListActivity.this).getAllContacts());
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Contacts>>() {
                    @Override
                    public void onCompleted() {
                        if (progress != null && progress.isShowing())
                            progress.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (progress != null && progress.isShowing())
                            progress.dismiss();
                        getAllContacts();
                    }

                    @Override
                    public void onNext(List<Contacts> contactList) {
                        // If there is no data then fetching from server
                        if (contactList == null && contactList.size() <= 0) {
                            getAllContacts();
                            return;
                        }
                        Collections.sort(contactList, new Comparator<Contacts>() {
                            @Override
                            public int compare(Contacts contact1, Contacts contact2) {
                                return contact1.getFirstName().compareToIgnoreCase(contact2.getFirstName());
                            }
                        });
                        setAdapter(contactList);
                        // After getting data from db, calling api to fetch data from server to
                        getAllContacts();
                    }
                });
    }

    // For getting contact data from server
    private void getAllContacts() {
        if (progress == null)
            progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.fetching_profile_info));

        if (contactListAdapter == null)
            progress.show();
        Observable<List<Contacts>> call = ContactAPI.getInstance().getAllContacts();
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fetchDataFromServerObserver);
    }

    Observer<List<Contacts>> fetchDataFromServerObserver = new Observer<List<Contacts>>() {

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

            saveToDb(contactList);
        }

        @Override
        public void onCompleted() {
            if (progress != null && progress.isShowing())
                progress.dismiss();
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            if (progress != null && progress.isShowing())
                progress.dismiss();
        }
    };

    private void saveToDb(final List<Contacts> contactList) {
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
        if (progress == null)
            progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.fetching_profile_info));
        progress.show();

        // Starting Background Thread using RxJava for db operation
        subscription = Observable.defer(new Func0<Observable<Void>>() {

            @Override
            public Observable<Void> call() {
                ContactDatabaseHelper db = new ContactDatabaseHelper(ContactListActivity.this);
                db.deleteAllData();
                db.insertContacts(contactList);
                return Observable.just(null);
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkObserver<Void>() {
                });
    }

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
        if (contactListAdapter == null) {
            contactListAdapter = new ContactListAdapter(this, contactList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            rvContacts.setLayoutManager(mLayoutManager);
            rvContacts.setAdapter(contactListAdapter);
        } else {
            contactListAdapter.addContactList(contactList);
        }
    }

    @Override
    public void onClick(View view) {
        startActivityForResult(new Intent(this, AddContactActivity.class), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.hasExtra(ARGS_CONTACT)) {
                    Contacts contact = (Contacts) data.getSerializableExtra(ARGS_CONTACT);
                    if (contactListAdapter != null) {
                        contactListAdapter.addSingleContact(contact);
                    }
                }
            }
        }
    }
}
