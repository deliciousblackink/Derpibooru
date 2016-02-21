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
    protected abstract AccentColorIconButton getButtonScore();

    @Nullable
    protected abstract AccentColorIconButton getButtonFave();

    @Nullable
    protected abstract AccentColorIconButton getButtonUpvote();

    @Nullable
    protected abstract AccentColorIconButton getButtonDownvote();

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
        refreshInteractionButton(getButtonFave(), faves,
                                 getInteractions().contains(DerpibooruImageInteraction.InteractionType.Fave));
        refreshInteractionButton(getButtonUpvote(), upvotes,
                                 getInteractions().contains(DerpibooruImageInteraction.InteractionType.Upvote));
        refreshInteractionButton(getButtonDownvote(), downvotes,
                                 getInteractions().contains(DerpibooruImageInteraction.InteractionType.Downvote));
        if (getButtonScore() != null) getButtonScore().setText(String.format("%d", (upvotes - downvotes)));
    }

    private void refreshInteractionButton(AccentColorIconButton button, int buttonValue, boolean active) {
        if (button != null) {
            button.setText(String.format("%d", buttonValue));
            button.setActive(active);
        }
    }

    private void initializeInteractionListeners() {
        if (getButtonFave() != null) getButtonFave()
                .setOnClickListener(new OnButtonClickListener(DerpibooruImageInteraction.InteractionType.Fave));
        if (getButtonUpvote() != null) getButtonUpvote()
                .setOnClickListener(new OnButtonClickListener(DerpibooruImageInteraction.InteractionType.Upvote));
        if (getButtonDownvote() != null) getButtonDownvote()
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
            if (result.getInteractions().get(0) == DerpibooruImageInteraction.InteractionType.ClearFave) {
                removeInteraction(DerpibooruImageInteraction.InteractionType.Fave);
            } else if (result.getInteractions().get(0) == DerpibooruImageInteraction.InteractionType.ClearVote) {
                removeInteraction(getInteractions().contains(DerpibooruImageInteraction.InteractionType.Upvote)
                                  ? DerpibooruImageInteraction.InteractionType.Upvote
                                  : DerpibooruImageInteraction.InteractionType.Downvote);
            } else {
                for (DerpibooruImageInteraction.InteractionType interaction : result.getInteractions()) {
                    addInteraction(interaction);
                }
            }
            onInteractionCompleted(result);
        }

        @Override
        public void onQueryFailed() {
            onInteractionFailed();
        }
    }
}
