package derpibooru.derpy.ui.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.List;

import derpibooru.derpy.data.server.DerpibooruImageInteraction;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.requesters.ImageInteractionRequester;
import derpibooru.derpy.ui.views.AccentColorIconButton;

/**
 * Provides a layer of abstraction for image interaction requests. Handles server queries and UI presentation.
 */
public abstract class ImageInteractionPresenter {
    private ImageInteractionRequester mInteractionRequester;

    public ImageInteractionPresenter(Context context) {
        mInteractionRequester = new ImageInteractionRequester(context, new InteractionRequestHandler());
        initializeInteractionListeners();
    }

    @Nullable
    protected abstract AccentColorIconButton getScoreButton();

    @Nullable
    protected abstract AccentColorIconButton getFaveButton();

    @Nullable
    protected abstract AccentColorIconButton getUpvoteButton();

    @Nullable
    protected abstract AccentColorIconButton getDownvoteButton();

    protected abstract int getInternalImageId();

    @NonNull
    protected abstract List<DerpibooruImageInteraction.InteractionType> getInteractions();

    protected abstract void addInteraction(DerpibooruImageInteraction.InteractionType interaction);

    protected abstract void removeInteraction(DerpibooruImageInteraction.InteractionType interaction);

    protected abstract void onInteractionFailed();

    protected void onInteractionCompleted(DerpibooruImageInteraction result) {
        refreshInfo(result.getFavorites(), result.getUpvotes(), result.getDownvotes());
    }

    public void refreshInfo(int faves, int upvotes, int downvotes) {
        refreshInteractionButton(getFaveButton(), faves,
                                 getInteractions().contains(DerpibooruImageInteraction.InteractionType.Fave));
        refreshInteractionButton(getUpvoteButton(), upvotes,
                                 getInteractions().contains(DerpibooruImageInteraction.InteractionType.Upvote));
        refreshInteractionButton(getDownvoteButton(), downvotes,
                                 getInteractions().contains(DerpibooruImageInteraction.InteractionType.Downvote));
        refreshInteractionButton(getScoreButton(), (upvotes - downvotes), getInteractions().size() > 0);
    }

    private void refreshInteractionButton(AccentColorIconButton button, int buttonValue, boolean active) {
        if (button != null) {
            button.setText(String.format("%d", buttonValue));
            button.setActive(active);
        }
    }

    private void initializeInteractionListeners() {
        if (getFaveButton() != null) getFaveButton()
                .setOnClickListener(new OnButtonClickListener(DerpibooruImageInteraction.InteractionType.Fave));
        if (getUpvoteButton() != null) getUpvoteButton()
                .setOnClickListener(new OnButtonClickListener(DerpibooruImageInteraction.InteractionType.Upvote));
        if (getDownvoteButton() != null) getDownvoteButton()
                .setOnClickListener(new OnButtonClickListener(DerpibooruImageInteraction.InteractionType.Downvote));
    }

    private class OnButtonClickListener implements View.OnClickListener {
        private final DerpibooruImageInteraction.InteractionType mType;

        public OnButtonClickListener(final DerpibooruImageInteraction.InteractionType type) {
            mType = type;
        }

        @Override
        public void onClick(View v) {
            if (!getInteractions().contains(mType)) {
                mInteractionRequester.interaction(mType)
                        .onImage(getInternalImageId())
                        .fetch();
            } else {
                mInteractionRequester.interaction(
                        mType == DerpibooruImageInteraction.InteractionType.Fave ? DerpibooruImageInteraction.InteractionType.ClearFave
                                                                                 : DerpibooruImageInteraction.InteractionType.ClearVote)
                        .onImage(getInternalImageId())
                        .fetch();
            }
        }
    }

    private class InteractionRequestHandler implements QueryHandler<DerpibooruImageInteraction> {
        @Override
        public void onQueryExecuted(DerpibooruImageInteraction result) {
            switch (result.getInteractionType()) {
                case ClearFave:
                    removeInteraction(DerpibooruImageInteraction.InteractionType.Fave);
                    break;
                case ClearVote:
                    removeInteraction(getInteractions().contains(DerpibooruImageInteraction.InteractionType.Upvote)
                                      ? DerpibooruImageInteraction.InteractionType.Upvote
                                      : DerpibooruImageInteraction.InteractionType.Downvote);
                    break;
                case Downvote:
                    if (getInteractions().contains(DerpibooruImageInteraction.InteractionType.Upvote)) {
                        removeInteraction(DerpibooruImageInteraction.InteractionType.Upvote);
                    }
                    addInteraction(DerpibooruImageInteraction.InteractionType.Downvote);
                    break;
                case Fave:
                    addInteraction(DerpibooruImageInteraction.InteractionType.Fave);
                    /* intentional fall-through (fave = add to favorites _and_ upvote) */
                case Upvote:
                    if (getInteractions().contains(DerpibooruImageInteraction.InteractionType.Downvote)) {
                        removeInteraction(DerpibooruImageInteraction.InteractionType.Downvote);
                    }
                    addInteraction(DerpibooruImageInteraction.InteractionType.Upvote);
                    break;
            }
            onInteractionCompleted(result);
        }

        @Override
        public void onQueryFailed() {
            onInteractionFailed();
        }
    }
}
