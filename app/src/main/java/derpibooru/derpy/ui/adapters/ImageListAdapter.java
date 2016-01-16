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
import derpibooru.derpy.data.types.DerpibooruImageThumb;

public class ImageListAdapter extends ArrayAdapter {
    private Context mContext;
    private int mLayoutResourceId;
    private ArrayList<DerpibooruImageThumb> mImages;

    public ImageListAdapter(Context context, int layoutResourceId, ArrayList<DerpibooruImageThumb> images) {
        super(context, layoutResourceId);
        this.mLayoutResourceId = layoutResourceId;
        this.mContext = context;
        this.mImages = images;
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