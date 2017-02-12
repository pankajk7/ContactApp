package contact.gojek.com.Adapters;

import android.content.Context;
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

import java.util.List;

import contact.gojek.com.Model.Contacts;
import contact.gojek.com.R;

/**
 * Created by Pankaj on 12/02/17.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    Context context;
    LayoutInflater layoutInflater;

    List<Contacts> contactList;

    public ContactListAdapter(Context context, List<Contacts> contactList) {
        this.context = context;
        this.contactList = contactList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_contact_pic);
            textView = (TextView) itemView.findViewById(R.id.tv_contact_name);
        }

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
                .override(72,72)
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
    }

    @Override
    public int getItemCount() {
        return contactList != null ? contactList.size() : 0;
    }
}
