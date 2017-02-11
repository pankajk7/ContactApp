package contact.gojek.com.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.List;

import contact.gojek.com.Model.Contacts;
import contact.gojek.com.R;
import contact.gojek.com.Rest.API.ContactAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactListActivity extends AppCompatActivity {

    RecyclerView rvContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Call<List<Contacts>> call = ContactAPI.getInstance().getAllContacts();
        call.enqueue(new Callback<List<Contacts>>() {
            @Override
            public void onResponse(Call<List<Contacts>> call, Response<List<Contacts>> response) {
                if(isFinishing()) return;

                if(!response.isSuccessful()) return;
                List<Contacts> contactList = response.body();
                if(contactList == null || contactList.size() <= 0) return;

            }

            @Override
            public void onFailure(Call<List<Contacts>> call, Throwable t) {
                Log.d("Failure: ", t.getMessage()+"");
            }
        });
    }
}
