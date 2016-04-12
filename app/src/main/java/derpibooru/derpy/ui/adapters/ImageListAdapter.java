package derpibooru.derpy.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
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

import java.util.EnumSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import derpibooru.derpy.R;
import derpibooru.derpy.data.server.DerpibooruImageInteraction;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.ui.animators.ImageListItemAnimator;
import derpibooru.derpy.ui.presenters.ImageInteractionPresenter;
import derpibooru.derpy.ui.views.AccentColorIconButton;

public abstract class ImageListAdapter extends RecyclerViewPaginationAdapter<DerpibooruImageThumb, ImageListAdapter.ViewHolder> {
    private ImageListItemAnimator mAnimator;
    private boolean mUserLoggedIn;

    protected ImageListAdapter(Context context, List<DerpibooruImageThumb> items, boolean isUserLoggedIn) {
        super(context, items);
        mAnimator = new ImageListItemAnimator();
        mUserLoggedIn = isUserLoggedIn;
    }

    public abstract void startImageActivity(int imageId);

    @Override
    public ImageListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_image_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        initializeImageView(holder, position);
        initializeImageInteractions(holder, position);
        setInteractionListeners(holder, position);
        holder.buttonComments.setText(String.format("%d", getItems().get(position).getCommentCount()));
        holder.buttonComments.setEnabled(false);
    }

    /**
     * Resets items and configures image interactions according to the user authentication state.
     * @param newItems new items
     * @param isUserLoggedIn new user interaction state
     */
    public void resetItems(List<DerpibooruImageThumb> newItems, boolean isUserLoggedIn) {
        mUserLoggedIn = isUserLoggedIn;
        super.resetItems(newItems);
    }

    public void replaceItem(final DerpibooruImageThumb target) {
        int targetIndex = Iterables.indexOf(getItems(), new Predicate<DerpibooruImageThumb>() {
            @Override
            public boolean apply(DerpibooruImageThumb it) {
                return it.getId() == target.getId();
            }
        });
        getItems().set(targetIndex, target);
        notifyItemChanged(targetIndex);
    }

    private void initializeImageView(final ViewHolder target, final int position) {
        if (getItems().get(position).isSpoilered()) {
            displaySpoiler(target, position);
        } else {
            displayImage(target, position);
        }
        target.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.get(getContext()).clearMemory();
                startImageActivity(getItems().get(position).getId());
            }
        });
    }

    private void displaySpoiler(ViewHolder target, int position) {
        loadWithGlide(getItems().get(position).getSpoilerImageUrl(),
                      Priority.NORMAL, target.imageView);
    }

    private void displayImage(ViewHolder target, int position) {
        Priority loadingPriority =
                getItems().get(position).getThumbUrl().endsWith(".gif") ? Priority.LOW : Priority.NORMAL;
        loadWithGlide(getItems().get(position).getThumbUrl(),
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

    private void setInteractionListeners(final ViewHolder target, final int position) {
        mAnimator.clearView(target.layoutUnspoiler);
        mAnimator.clearView(target.layoutImageInteractions);
        target.layoutImageInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* the target height of layoutUnspoiler and layoutImageInteractions is equal to that of layoutImageInfo */
                final int panelHeight = target.layoutImageInfo.getMeasuredHeight();
                if (getItems().get(position).isSpoilered()) {
                    toggleView(target.layoutUnspoiler, panelHeight);
                } else {
                    toggleView(target.layoutImageInteractions, panelHeight);
                }
            }
        });
        target.buttonUnspoiler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItems().get(position).unspoiler();
                mAnimator.collapseView(target.layoutUnspoiler);
                displayImage(target, position);
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.imageView) ImageView imageView;
        @Bind(R.id.layoutImageInfo) View layoutImageInfo;
        @Bind(R.id.buttonScore) AccentColorIconButton buttonScore;
        @Bind(R.id.buttonComments) AccentColorIconButton buttonComments;

        @Bind(R.id.layoutUnspoiler) View layoutUnspoiler;
        @Bind(R.id.buttonUnspoiler) AccentColorIconButton buttonUnspoiler;

        @Bind(R.id.layoutImageInteractions) View layoutImageInteractions;
        @Bind(R.id.buttonFave) AccentColorIconButton buttonFave;
        @Bind(R.id.buttonUpvote) AccentColorIconButton buttonUpvote;

        public ImageInteractionPresenter interactions;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    private void initializeImageInteractions(final ViewHolder target, final int position) {
        target.interactions = new ImageInteractionPresenter(
                getItems().get(position).getIdForImageInteractions(), target.buttonScore, target.buttonFave, target.buttonUpvote, null) {
            @NonNull
            @Override
            protected EnumSet<DerpibooruImageInteraction.InteractionType> getInteractionsSet() {
                return getItems().get(position).getImageInteractions();
            }

            @Override
            protected void onInteractionCompleted(DerpibooruImageInteraction result) {
                getItems().get(position).setFaves(result.getFavorites());
                getItems().get(position).setUpvotes(result.getUpvotes());
                getItems().get(position).setDownvotes(result.getDownvotes());
                super.onInteractionCompleted(result);
            }

            @Override
            protected void onInteractionFailed() { }
        };
        if (mUserLoggedIn) {
            target.interactions.enableInteractions(getContext());
        }
        target.interactions.refreshInfo(
                getItems().get(position).getFaves(), getItems().get(position).getUpvotes(), getItems().get(position).getDownvotes());
    }
}