package derpibooru.derpy.data.server;

public class DerpibooruImageInteraction {
    private final int mScore;
    private final int mFavorites;
    private final int mUpvotes;
    private final int mDownvotes;
    private final InteractionType mInteractionType;

    public DerpibooruImageInteraction(int score, int faves, int upvotes, int downvotes,
                                      InteractionType interactionType) {
        mScore = score;
        mFavorites = faves;
        mUpvotes = upvotes;
        mDownvotes = downvotes;
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
