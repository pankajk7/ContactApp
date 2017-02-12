package contact.gojek.com.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import contact.gojek.com.Activities.ContactInfoActivity;
import contact.gojek.com.Model.Contacts;
import contact.gojek.com.R;

/**
 * Created by Pankaj on 12/02/17.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    Context context;
    LayoutInflater layoutInflater;

    int dimenRes;

    List<Contacts> contactList;

    public ContactListAdapter(Context context, List<Contacts> contactList) {
        this.context = context;
        this.contactList = contactList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resources resources = context.getResources();
        dimenRes = (int) (resources.getDimension(R.dimen.iv_row_height) /
                resources.getDisplayMetrics().density);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.row_contact_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Contacts contact = contactList.get(position);

        Glide.with(context).load(contact.getProfilePic()).asBitmap().centerCrop()
                .placeholder(R.drawable.ic_profile_pic)
                .into(new BitmapImageViewTarget(holder.imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        holder.imageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
        holder.textView.setText(contact.getFirstName() + " " + contact.getLastName());
        holder.textView.setTag(R.id.iv_tag, position);
        holder.imageView.setTag(R.id.iv_tag, position);
    }

    public void addContactList(List<Contacts> contactList){
        this.contactList.clear();
        this.contactList = contactList;
        notifyDataSetChanged();
    }

    public void addSingleContact(Contacts contact){
        contactList.add(contact);
        Collections.sort(contactList, new Comparator<Contacts>() {
            @Override
            public int compare(Contacts contact1, Contacts contact2) {
                return contact1.getFirstName().compareToIgnoreCase(contact2.getFirstName());
            }
        });
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return contactList != null ? contactList.size() : 0;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_contact_pic);
            textView = (TextView) itemView.findViewById(R.id.tv_contact_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    ContactInfoActivity.startActivity(context, contactList.get(position));
                }
            });
        }

    }
}
