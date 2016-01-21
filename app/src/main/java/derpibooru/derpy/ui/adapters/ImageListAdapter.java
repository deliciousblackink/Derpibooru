package derpibooru.derpy.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageThumb;

/**
 * Asynchronously downloads images/animated GIFs and places them into a
 * custom ViewHolder defined by the 'view_image_list_item.xml' layout.
 */
public class ImageListAdapter extends ArrayAdapter {
    private Context mContext;
    private int mLayoutResourceId;
    private ArrayList<DerpibooruImageThumb> mImages;

    public ImageListAdapter(Context context, ArrayList<DerpibooruImageThumb> images) {
        super(context, R.layout.view_image_list_item);
        mLayoutResourceId = R.layout.view_image_list_item;

        this.mContext = context;
        this.mImages = images;
    }

    /**
     * Stops any active downloads.
     */
    public void stop() {
        /* FIXME: a bug without an obvious solution */
        /* (related only to the search functionality)
         *
         * afaik right now Glide does not provide a direct way of
         * stopping the active requests. unfortunately, those remain
         * in case the user has changed the search options before
         * the previous set of images has fully downloaded.
         *
         * it should not leave a noticeable impact on the memory usage
         * since the unused objects get GC'ed quickly & Glide cleans up
         * after itself when the activity finishes.
         *
         * however, it does produce a lot of unnecessary traffic, bad
         * both for the server & the client.
         */
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = inflater.inflate(mLayoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.info = (TextView) view.findViewById(R.id.text);
            holder.image = (ImageView) view.findViewById(R.id.image);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.data = mImages.get(position);

        /* TODO: load animated GIFs _after_ static images */
        /* right now, if there's a huge GIF in the beginning of the list,
         * the user will have to wait ages before everything loads properly.
          */
        Glide.with(mContext)
                .load(holder.data.getThumbUrl())
                .centerCrop()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(holder.image);

        holder.info.setText(Integer.toString(holder.data.getScore()));
        return view;
    }

    public static class ViewHolder {
        public TextView info;
        public ImageView image;
        public DerpibooruImageThumb data; /* to be passed to ImageActivity upon opening the image */
    }
}