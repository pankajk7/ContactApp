package contact.gojek.com.Rest.API;

import java.util.HashMap;
import java.util.List;

import contact.gojek.com.Model.ContactProfile;
import contact.gojek.com.Model.Contacts;
import contact.gojek.com.Rest.ApiClient;
import contact.gojek.com.Rest.Interface.ContactsAPIService;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import rx.Observable;

/**
 * Created by Pankaj on 11/02/17.
 */

public class ContactAPI {

    ContactsAPIService contactsAPIService;
    public static ContactAPI contactAPI;

    private ContactAPI() {
        contactsAPIService = ApiClient.getClient().create(ContactsAPIService.class);
    }

    public static ContactAPI getInstance() {
        if (contactAPI == null) {
            contactAPI = new ContactAPI();
            return contactAPI;
        }
        return contactAPI;
    }

    public Observable<List<Contacts>> getAllContacts(){
        return contactsAPIService.getAllContactList();
    }

    public Observable<ContactProfile> getContactByContactId(int contactId){
        return contactsAPIService.getContactByContactId(contactId);
    }

    public Observable<ContactProfile> saveContactInfo(RequestBody map){
        return contactsAPIService.saveContactInfo(map);
    }
}
