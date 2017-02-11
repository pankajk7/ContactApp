package contact.gojek.com.Rest.API;

import java.util.List;

import contact.gojek.com.Model.Contacts;
import contact.gojek.com.Rest.ApiClient;
import contact.gojek.com.Rest.Interface.ContactsAPIService;
import retrofit2.Call;

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

    public Call<List<Contacts>> getAllContacts(){
        return contactsAPIService.getAllContactList();
    }

    public Call<List<Contacts>> getContactByContactId(int contactId){
        return contactsAPIService.getContactByContactId(contactId);
    }
}
