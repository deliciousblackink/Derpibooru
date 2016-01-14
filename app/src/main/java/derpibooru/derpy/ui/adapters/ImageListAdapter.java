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
import derpibooru.derpy.data.types.ImageThumb;

public class ImageListAdapter extends ArrayAdapter {
    private Context mContext;
    private int mLayoutResourceId;
    private ArrayList<ImageThumb> mImages;

    public ImageListAdapter(Context context, int layoutResourceId, ArrayList<ImageThumb> images) {
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
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.info = (TextView) row.findViewById(R.id.text);
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Glide.with(mContext)
                .load(mImages.get(position).getThumbUrl())
                .centerCrop()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(holder.image);

        holder.info.setText(Integer.toString(mImages.get(position).getScore()));
        holder.info.setTag(mImages.get(position).getId());
        return row;
    }

    public static class ViewHolder {
        public TextView info;
        public ImageView image;
    }
}