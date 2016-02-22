package derpibooru.derpy.data.server;

public class DerpibooruImageInteraction {
    private int mScore;
    private int mFavorites;
    private int mUpvotes;
    private int mDownvotes;
    private int mInternalImageId;
    private InteractionType mInteractionType;

    public DerpibooruImageInteraction(int score, int faves, int upvotes, int downvotes,
                                      int internalImageId, InteractionType interactionType) {
        mScore = score;
        mFavorites = faves;
        mUpvotes = upvotes;
        mDownvotes = downvotes;
        mInternalImageId = internalImageId;
        mInteractionType = interactionType;
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

    public InteractionType getInteractionType() {
        return mInteractionType;
    }

    public int getInternalImageId() {
        return mInternalImageId;
    }

    public enum InteractionType {
        Fave(0),
        Upvote(1),
        Downvote(2),
        ClearFave(3),
        ClearVote(4);

        private int mValue;

        InteractionType(int value) {
            mValue = value;
        }

        public static InteractionType fromValue(int value) {
            for (InteractionType type : values()) {
                if (type.mValue == value) {
                    return type;
                }
            }
            return ClearVote;
        }

        public int toValue() {
            return mValue;
        }
    }
}
