package derpibooru.derpy.data.server;

import java.util.List;

public class DerpibooruImageInteraction {
    private int mScore;
    private int mFavorites;
    private int mUpvotes;
    private int mDownvotes;
    private int mInternalImageId;
    private List<InteractionType> mInteractions;

    public DerpibooruImageInteraction(int score, int faves, int upvotes, int downvotes,
                                      int internalImageId, List<InteractionType> interactionType) {
        mScore = score;
        mFavorites = faves;
        mUpvotes = upvotes;
        mDownvotes = downvotes;
        mInternalImageId = internalImageId;
        mInteractions = interactionType;
    }

    public int getScore() {
        return mScore;
    }

    public int getFavorites() {
        return mFavorites;
    }

    public int getUpvotes() {
        return mUpvotes;
    }

    public int getDownvotes() {
        return mDownvotes;
    }

    public List<InteractionType> getInteractions() {
        return mInteractions;
    }

    public int getInternalImageId() {
        return mInternalImageId;
    }

    public enum InteractionType {
        Fave,
        Upvote,
        Downvote,
        ClearVote,
        ClearFave,
        None
    }
}
