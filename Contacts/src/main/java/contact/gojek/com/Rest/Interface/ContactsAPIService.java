package contact.gojek.com.Rest.Interface;

import java.util.List;

import contact.gojek.com.Model.Contacts;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Pankaj on 11/02/17.
 */

public interface ContactsAPIService {

    @GET("contacts.json")
    Observable<List<Contacts>> getAllContactList();

    @GET("contacts/{contact_id}.json")
    Observable<List<Contacts>> getContactByContactId(@Path("contact_id") int contactId);
}
