package derpibooru.derpy.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.common.base.Joiner;

import java.util.ArrayList;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.ui.ImageActivity;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<DerpibooruImageThumb> mImages;

    public ImageListAdapter(Context context, ArrayList<DerpibooruImageThumb> images) {
        mContext = context;
        mImages = images;
    }

    @Override
    public ImageListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_image_list_item, parent, false);
        return new ViewHolder(v);
    }

    public void resetImageThumbs(ArrayList<DerpibooruImageThumb> newImages) {
        super.notifyItemRangeRemoved(0, mImages.size());
        mImages = newImages;
        super.notifyItemRangeInserted(0, mImages.size() - 1);
    }

    public void appendImageThumbs(ArrayList<DerpibooruImageThumb> newImages) {
        int oldImageCount = mImages.size();
        mImages.addAll(newImages);
        int newImageCount = mImages.size() - 1;
        super.notifyItemRangeInserted(oldImageCount, newImageCount);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.data = mImages.get(position);
        if (holder.data.isSpoilered()) {
            String spoilers = Joiner.on(", ").skipNulls()
                    .join(holder.data.getSpoileredTagNames());
            holder.textSpoiler.setVisibility(View.VISIBLE);
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
        holder.textScore.setText(String.format("%d", holder.data.getScore()));
        holder.textComments.setText(String.format("%d", holder.data.getCommentCount()));
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImageActivity.class);
                intent.putExtra("image_thumb", holder.data);
                mContext.startActivity(intent);
            }
        });
    }

    private void displayImageWithGlide(String url, Priority priority, ImageView target) {
        Glide.with(mContext).load(url)
                .centerCrop().crossFade()
                .priority(priority)
                /* the image is going to be resized due to orientation changes */
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(target);
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textScore;
        public TextView textComments;
        public TextView textSpoiler;
        public ImageView imageView;
        public DerpibooruImageThumb data;

        public ViewHolder(View v) {
            super(v);
            textScore = (TextView) v.findViewById(R.id.textScore);
            textComments = (TextView) v.findViewById(R.id.textComments);
            textSpoiler = (TextView) v.findViewById(R.id.textSpoiler);
            imageView = (ImageView) v.findViewById(R.id.imageView);
        }
    }
}