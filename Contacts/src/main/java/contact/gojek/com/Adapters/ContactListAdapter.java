package contact.gojek.com.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import contact.gojek.com.Model.Contacts;

/**
 * Created by Pankaj on 12/02/17.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    Context context;

    List<Contacts> contactList;

    public ContactListAdapter(Context context, List<Contacts> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return contactList != null ? contactList.size() : 0;
    }
}
