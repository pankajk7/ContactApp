package contact.gojek.com.Rest.Interface;

import java.util.HashMap;
import java.util.List;

import contact.gojek.com.Model.ContactProfile;
import contact.gojek.com.Model.Contacts;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Pankaj on 11/02/17.
 */

public interface ContactsAPIService {

    @GET("contacts.json")
    Observable<List<Contacts>> getAllContactList();

    @GET("contacts/{contact_id}.json")
    Observable<ContactProfile> getContactByContactId(@Path("contact_id") int contactId);

    @POST("contacts.json")
    Observable<ContactProfile> saveContactInfo(@Body RequestBody body);
}
