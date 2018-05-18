package info.mik.mru.contacts;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import info.mik.mru.R;

/**
 * Created by mik on 2018-05-09.
 */

public class AdapterContacts extends BaseAdapter implements View.OnClickListener {
//public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private Cursor cursor;
    private Context mContext;
    private LayoutInflater inflater;

    public AdapterContacts(Context context, Cursor cursor) {
        mContext = context;
        this.cursor = cursor;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Holder holder;
        cursor.moveToPosition(position);
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_contacts, parent,
                    false);
            holder = new Holder();
            holder.tvContactName =
                    view.findViewById(R.id.tvContactName);
            holder.tvContactNumber =
                    view.findViewById(R.id.tvContactNumber);
            holder.ivContactImage =
                    view.findViewById(R.id.ivContactImage);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        holder.tvContactName.setText(cursor.getString(cursor
                .getColumnIndex(Phone.DISPLAY_NAME)));
        holder.tvContactNumber.setText(cursor.getString(cursor
                .getColumnIndex(Phone.NUMBER)));
        String imageUri = cursor.getString(cursor
                .getColumnIndex(Phone.PHOTO_URI));
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                    mContext.getContentResolver(), Uri.parse(imageUri));
            holder.ivContactImage.setImageBitmap(bitmap);
            scaleImage(holder.ivContactImage);
        } catch (Exception e) {
            holder.ivContactImage.setImageResource(R.drawable.contact);
            scaleImage(holder.ivContactImage);
        }
        // TODO: Nemanja helped me with this, finally click on contact calls dialer
        // TODO: but, it always selects contact from the bottom of the screen, and refreshes view back to top
        // TODO: Have to check this out furthermore
        // CHECKED: Can't fix it with BaseAdapter, should have used RecyclerViewAdapter but MRU time is out
        view.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        // while testing, just make a Toast with a Contact name
        Toast.makeText(mContext, "Clicked: " + cursor.getString(cursor
                .getColumnIndex(Phone.DISPLAY_NAME)), Toast.LENGTH_LONG).show();

        // Initialize an intent to open a Dialer app with contact's phone number
        // and allow user to call the number manually
        Intent intentOpenDialer = new Intent(Intent.ACTION_DIAL);

        // Send phone number to intent as data
        intentOpenDialer.setData(Uri.parse("tel:" + cursor.getString(cursor
                .getColumnIndex(Phone.NUMBER))));

        // and send intent in context of the adapter
        mContext.startActivity(intentOpenDialer);
    }


    private class Holder {
        TextView tvContactName, tvContactNumber;
        ImageView ivContactImage;
    }

    //helper method for profile image
    private void scaleImage(ImageView imageView) {

        Drawable drawing = imageView.getDrawable();
        if (drawing == null) { }
        Bitmap bitmap = ((BitmapDrawable) drawing).getBitmap();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int bounding = dpToPx(50);

        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);

        imageView.setImageDrawable(result);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView
                .getLayoutParams();
        params.width = width;
        params.height = height;
        imageView.setLayoutParams(params);
    }

    //helper method for conversion dp to pixels
    private int dpToPx(int dp) {
        float density = mContext.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}