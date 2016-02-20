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
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.ArrayList;

import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageInteraction;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.requesters.ImageInteractionRequester;
import derpibooru.derpy.ui.ImageActivity;
import derpibooru.derpy.ui.animations.ImageListItemAnimator;
import derpibooru.derpy.ui.views.AccentColorIconButton;

public class ImageListAdapter extends RecyclerViewEndlessScrollAdapter<DerpibooruImageThumb, ImageListAdapter.ViewHolder> {
    private ImageListItemAnimator mAnimator;
    private ImageInteractionRequester mInteractions;

    public ImageListAdapter(Context context, ArrayList<DerpibooruImageThumb> items) {
        super(context, items);
        mAnimator = new ImageListItemAnimator();
        mInteractions = new ImageInteractionRequester(context, new ImageInteractionHandler());
    }

    @Override
    public ImageListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_image_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int oldImageId = (holder.data != null) ? holder.data.getId() : -1;
        holder.data = getItems().get(position);
        if (oldImageId != holder.data.getId()) {
            initializeViewHolder(holder);
        }
        holder.buttonUpvote.setText(String.format("%d", holder.data.getUpvotes()));
        holder.buttonUpvote.setActive(
                holder.data.getImageInteractions().contains(DerpibooruImageInteraction.InteractionType.Upvote));
        holder.buttonFave.setText(String.format("%d", holder.data.getFaves()));
        holder.buttonFave.setActive(
                holder.data.getImageInteractions().contains(DerpibooruImageInteraction.InteractionType.Fave));
        holder.buttonScore.setText(String.format("%d", holder.data.getScore()));
        holder.buttonScore.setActive(
                holder.data.getImageInteractions().size() > 0);
        holder.buttonScore.setEnabled(false);
        holder.buttonComments.setText(String.format("%d", holder.data.getCommentCount()));
        holder.buttonComments.setEnabled(false);
    }

    private void initializeViewHolder(final ViewHolder target) {
        if (target.data.isSpoilered()) {
            displaySpoiler(target);
        } else {
            displayImage(target);
        }
        target.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.get(getContext()).clearMemory();
                Intent intent = new Intent(getContext(), ImageActivity.class);
                intent.putExtra("derpibooru.derpy.ImageThumb", target.data);
                getContext().startActivity(intent);
            }
        });
        setInteractionListeners(target);
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
        Glide.with(getContext()).load(url)
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
                if (!target.data.getImageInteractions()
                        .contains(DerpibooruImageInteraction.InteractionType.Upvote)) {
                    mInteractions.interaction(DerpibooruImageInteraction.InteractionType.Upvote)
                            .onImage(target.data.getInternalId()).fetch();
                } else {
                    mInteractions.interaction(DerpibooruImageInteraction.InteractionType.ClearVote)
                            .onImage(target.data.getInternalId()).fetch();
                }
            }
        });
        target.buttonFave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!target.data.getImageInteractions()
                        .contains(DerpibooruImageInteraction.InteractionType.Fave)) {
                    mInteractions.interaction(DerpibooruImageInteraction.InteractionType.Fave)
                            .onImage(target.data.getInternalId()).fetch();
                } else {
                    mInteractions.interaction(DerpibooruImageInteraction.InteractionType.ClearFave)
                            .onImage(target.data.getInternalId()).fetch();
                }
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

    public class ImageInteractionHandler implements QueryHandler<DerpibooruImageInteraction> {
        @Override
        public void onQueryExecuted(final DerpibooruImageInteraction result) {
            int thumbPosition = getItems().indexOf(
                    Iterables.find(getItems(), new Predicate<DerpibooruImageThumb>() {
                        public boolean apply(DerpibooruImageThumb it) {
                            return it.getInternalId() == result.getInternalImageId();
                        }
                    }));
            getItems().get(thumbPosition).setScore(result.getScore());
            getItems().get(thumbPosition).setFaves(result.getFavorites());
            getItems().get(thumbPosition).setUpvotes(result.getUpvotes());
            getItems().get(thumbPosition).setDownvotes(result.getDownvotes());
            for (DerpibooruImageInteraction.InteractionType interaction : result.getInteractions()) {
                if (interaction == DerpibooruImageInteraction.InteractionType.ClearFave) {
                    getItems().get(thumbPosition).removeImageInteraction(DerpibooruImageInteraction.InteractionType.Fave);
                } else if (interaction == DerpibooruImageInteraction.InteractionType.ClearVote) {
                    getItems().get(thumbPosition).removeImageInteraction(
                            getItems().get(thumbPosition).getImageInteractions().contains(DerpibooruImageInteraction.InteractionType.Upvote)
                            ? DerpibooruImageInteraction.InteractionType.Upvote
                            : DerpibooruImageInteraction.InteractionType.Downvote);
                } else {
                    getItems().get(thumbPosition).addImageInteraction(interaction);
                }
            }
            notifyItemChanged(thumbPosition);
        }

        @Override
        public void onQueryFailed() {

        }
    }
}