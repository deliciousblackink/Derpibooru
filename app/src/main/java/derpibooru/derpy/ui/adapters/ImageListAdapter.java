package derpibooru.derpy.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageInteractionType;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.requesters.ImageInteractionRequester;
import derpibooru.derpy.ui.ImageActivity;
import derpibooru.derpy.ui.animations.ImageListItemAnimator;
import derpibooru.derpy.ui.views.AccentColorIconButton;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {
    private ImageListItemAnimator mAnimator;
    private Context mContext;

    private ArrayList<DerpibooruImageThumb> mImages;
    private ImageInteractionRequester mInteractions;

    public ImageListAdapter(Context context, ArrayList<DerpibooruImageThumb> images) {
        mAnimator = new ImageListItemAnimator();
        mContext = context;
        mImages = images;
        mInteractions = new ImageInteractionRequester(context, new ImageInteractionHandler());
    }

    @Override
    public int getItemCount() {
        return mImages.size();
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
            displaySpoiler(holder);
        } else {
            displayImage(holder);
        }
        holder.buttonUpvote.setText(String.format("%d", holder.data.getUpvotes()));
        holder.buttonUpvote.setActive(
                holder.data.getImageInteractions().contains(DerpibooruImageInteractionType.Upvote));
        holder.buttonFave.setText(String.format("%d", holder.data.getFaves()));
        holder.buttonFave.setActive(
                holder.data.getImageInteractions().contains(DerpibooruImageInteractionType.Fave));
        holder.buttonScore.setText(String.format("%d", holder.data.getScore()));
        holder.buttonScore.setActive(
                holder.data.getImageInteractions().size() > 0);
        holder.buttonScore.setEnabled(false);
        holder.buttonComments.setText(String.format("%d", holder.data.getCommentCount()));
        holder.buttonComments.setEnabled(false);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.get(mContext).clearMemory();
                Intent intent = new Intent(mContext, ImageActivity.class);
                intent.putExtra("derpibooru.derpy.ImageThumb", holder.data);
                mContext.startActivity(intent);
            }
        });
        setInteractionListeners(holder);
    }

    private void displaySpoiler(ViewHolder target) {
        loadWithGlide(target.data.getSpoilerImageUrl(),
                      Priority.NORMAL, target.imageView);
    }

    private void displayImage(ViewHolder target) {
        Priority loadingPriority =
                target.data.getThumbUrl().endsWith(".gif") ? Priority.LOW : Priority.NORMAL;
        loadWithGlide(target.data.getThumbUrl(),
                      loadingPriority, target.imageView);
    }

    private void loadWithGlide(String url, Priority priority, ImageView target) {
        Glide.with(mContext).load(url)
                .centerCrop().crossFade()
                .priority(priority)
                /* the image is going to be resized due to orientation changes */
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(target);
    }

    private void setInteractionListeners(final ViewHolder target) {
        mAnimator.clearView(target.layoutUnspoiler);
        mAnimator.clearView(target.layoutImageInteractions);
        target.layoutImageInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* the target height of layoutUnspoiler and layoutImageInteractions is equal to that of layoutImageInfo */
                final int panelHeight = target.layoutImageInfo.getMeasuredHeight();
                if (target.data.isSpoilered()) {
                    toggleView(target.layoutUnspoiler, panelHeight);
                } else {
                    toggleView(target.layoutImageInteractions, panelHeight);
                }
            }
        });
        target.buttonUnspoiler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                target.data.unspoiler();
                mAnimator.collapseView(target.layoutUnspoiler);
                displayImage(target);
            }
        });
        target.buttonUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInteractions.interaction(ImageInteractionRequester.InteractionType.Upvote)
                        .onImage(target.data.getInternalId()).fetch();
            }
        });
    }

    private void toggleView(View v, int maximumHeight) {
        if (v.getMeasuredHeight() == 0) {
            mAnimator.expandView(v, maximumHeight);
        } else {
            mAnimator.collapseView(v);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public View layoutImageInfo;
        public AccentColorIconButton buttonScore;
        public AccentColorIconButton buttonComments;

        public View layoutUnspoiler;
        public AccentColorIconButton buttonUnspoiler;

        public View layoutImageInteractions;
        public AccentColorIconButton buttonFave;
        public AccentColorIconButton buttonUpvote;

        public DerpibooruImageThumb data;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.imageView);
            layoutImageInfo = v.findViewById(R.id.layoutImageInfo);
            buttonScore = (AccentColorIconButton) v.findViewById(R.id.buttonScore);
            buttonComments = (AccentColorIconButton) v.findViewById(R.id.buttonComments);
            layoutUnspoiler = v.findViewById(R.id.layoutUnspoiler);
            buttonUnspoiler = (AccentColorIconButton) v.findViewById(R.id.buttonUnspoiler);
            layoutImageInteractions = v.findViewById(R.id.layoutImageInteractions);
            buttonFave = (AccentColorIconButton) v.findViewById(R.id.buttonFave);
            buttonUpvote = (AccentColorIconButton) v.findViewById(R.id.buttonUpvote);
        }
    }

    public class ImageInteractionHandler implements QueryHandler {
        @Override
        public void onQueryExecuted(Object result) {

        }

        @Override
        public void onQueryFailed() {

        }
    }
}