package derpibooru.derpy.ui.presenters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.EnumSet;

import derpibooru.derpy.data.server.DerpibooruImageInteraction;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.requesters.ImageInteractionRequester;
import derpibooru.derpy.ui.views.AccentColorIconButton;

/**
 * Presents  image interaction requests. Handles both server requests and UI changes.
 */
public abstract class ImageInteractionPresenter {
    private ImageInteractionRequester mInteractionRequester;
    private int mId;

    private AccentColorIconButton mScoreButton;
    private AccentColorIconButton mFaveButton;
    private AccentColorIconButton mUpvoteButton;
    private AccentColorIconButton mDownvoteButton;

    protected ImageInteractionPresenter(int imageIdForInteractions,
                                        @Nullable AccentColorIconButton scoreButton,
                                        @Nullable AccentColorIconButton faveButton,
                                        @Nullable AccentColorIconButton upvoteButton,
                                        @Nullable AccentColorIconButton downvoteButton) {
        mId = imageIdForInteractions;
        mScoreButton = scoreButton;
        mFaveButton = faveButton;
        mUpvoteButton = upvoteButton;
        mDownvoteButton = downvoteButton;
        toggleInteractionButtons(false);
        if (mScoreButton != null) mScoreButton.setEnabled(false); /* the score button is not a touchable view */
    }

    public final void enableInteractions(Context context) {
        mInteractionRequester = new ImageInteractionRequester(context, new InteractionRequestHandler());
        initializeInteractionListeners();
        toggleInteractionButtons(true);
    }

    private void toggleInteractionButtons(boolean enabled) {
        if (mFaveButton != null) mFaveButton.setEnabled(enabled);
        if (mUpvoteButton != null) mUpvoteButton.setEnabled(enabled);
        if (mDownvoteButton != null) mDownvoteButton.setEnabled(enabled);
    }

    protected abstract void onInteractionFailed();

    @NonNull
    protected abstract EnumSet<DerpibooruImageInteraction.InteractionType> getInteractionsSet();

    protected void onInteractionCompleted(DerpibooruImageInteraction result) {
        refreshInfo(result.getFavorites(), result.getUpvotes(), result.getDownvotes());
    }

    public void refreshInfo(int faves, int upvotes, int downvotes) {
        if (mFaveButton != null) {
            mFaveButton.setText(String.format("%d", faves));
            mFaveButton.setActive(getInteractionsSet().contains(DerpibooruImageInteraction.InteractionType.Fave));
        }
        if (mUpvoteButton != null) {
            mUpvoteButton.setText(String.format("%d", upvotes));
            mUpvoteButton.setActive(getInteractionsSet().contains(DerpibooruImageInteraction.InteractionType.Upvote));
        }
        if (mDownvoteButton != null) {
            mDownvoteButton.setText(String.format("%d", downvotes));
            mDownvoteButton.setActive(getInteractionsSet().contains(DerpibooruImageInteraction.InteractionType.Downvote));
        }
        if (mScoreButton != null) {
            mScoreButton.setText(String.format("%d", (upvotes - downvotes)));
            mScoreButton.setActive(!getInteractionsSet().isEmpty());
        }
    }

    private void initializeInteractionListeners() {
        if (mFaveButton != null) mFaveButton
                .setOnClickListener(new OnButtonClickListener(DerpibooruImageInteraction.InteractionType.Fave));
        if (mUpvoteButton != null) mUpvoteButton
                .setOnClickListener(new OnButtonClickListener(DerpibooruImageInteraction.InteractionType.Upvote));
        if (mDownvoteButton != null) mDownvoteButton
                .setOnClickListener(new OnButtonClickListener(DerpibooruImageInteraction.InteractionType.Downvote));
    }

    private class OnButtonClickListener implements View.OnClickListener {
        private final DerpibooruImageInteraction.InteractionType mType;

        OnButtonClickListener(final DerpibooruImageInteraction.InteractionType type) {
            mType = type;
        }

        @Override
        public void onClick(View v) {
            mInteractionRequester.interaction(getInteractionsSet().contains(mType)
                                              ? ((mType == DerpibooruImageInteraction.InteractionType.Fave)
                                                 ? DerpibooruImageInteraction.InteractionType.ClearFave
                                                 : DerpibooruImageInteraction.InteractionType.ClearVote)
                                              : mType)
                    .onImage(mId).fetch();
        }
    }

    private class InteractionRequestHandler implements QueryHandler<DerpibooruImageInteraction> {
        @Override
        public void onQueryExecuted(DerpibooruImageInteraction result) {
            switch (result.getInteractionType()) {
                case ClearFave:
                    getInteractionsSet().remove(DerpibooruImageInteraction.InteractionType.Fave);
                    break;
                case ClearVote:
                    getInteractionsSet().remove(getInteractionsSet().contains(DerpibooruImageInteraction.InteractionType.Upvote)
                                                ? DerpibooruImageInteraction.InteractionType.Upvote
                                                : DerpibooruImageInteraction.InteractionType.Downvote);
                    break;
                case Downvote:
                    if (getInteractionsSet().contains(DerpibooruImageInteraction.InteractionType.Upvote)) {
                        getInteractionsSet().remove(DerpibooruImageInteraction.InteractionType.Upvote);
                    }
                    getInteractionsSet().add(DerpibooruImageInteraction.InteractionType.Downvote);
                    break;
                case Fave:
                    getInteractionsSet().add(DerpibooruImageInteraction.InteractionType.Fave);
                    /* intentional fall-through (fave = add to favorites _and_ upvote) */
                case Upvote:
                    if (getInteractionsSet().contains(DerpibooruImageInteraction.InteractionType.Downvote)) {
                        getInteractionsSet().remove(DerpibooruImageInteraction.InteractionType.Downvote);
                    }
                    getInteractionsSet().add(DerpibooruImageInteraction.InteractionType.Upvote);
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
