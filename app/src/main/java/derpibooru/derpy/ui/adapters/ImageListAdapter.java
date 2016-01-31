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
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.common.base.Joiner;

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
         * the previous set of images has been fully downloaded.
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
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.data = mImages.get(position);

        if (holder.data.isSpoilered()) {
            String spoilers = Joiner.on(", ").skipNulls()
                    .join(holder.data.getSpoileredTagNames());
            holder.textSpoiler.setText(spoilers);
            displayImageWithGlide(holder.data.getSpoilerImageUrl(),
                                  Priority.NORMAL, holder.imageView);
        } else {
            holder.textSpoiler.setVisibility(View.GONE);
            Priority loadingPriority =
                    holder.data.getThumbUrl().endsWith(".gif") ? Priority.LOW : Priority.NORMAL;
            displayImageWithGlide(holder.data.getThumbUrl(),
                                  loadingPriority, holder.imageView);
        }
        holder.textInfo.setText(Integer.toString(holder.data.getScore()));
        return view;
    }

    private void displayImageWithGlide(String url, Priority priority, ImageView target) {
        Glide.with(mContext)
                .load(url)
                .centerCrop()
                .crossFade()
                .priority(priority)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(target);
    }

    public static class ViewHolder {
        public TextView textInfo;
        public TextView textSpoiler;
        public ImageView imageView;
        public DerpibooruImageThumb data;

        public ViewHolder(View v) {
            textInfo = (TextView) v.findViewById(R.id.textInfo);
            textSpoiler = (TextView) v.findViewById(R.id.textSpoiler);
            imageView = (ImageView) v.findViewById(R.id.imageView);
        }
    }
}