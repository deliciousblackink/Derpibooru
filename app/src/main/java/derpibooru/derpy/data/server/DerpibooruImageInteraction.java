package derpibooru.derpy.data.server;

public class DerpibooruImageInteraction {
    private int mScore;
    private int mFavorites;
    private int mUpvotes;
    private int mDownvotes;

    public DerpibooruImageInteraction(int score, int faves, int upvotes, int downvotes) {
        mScore = score;
        mFavorites = faves;
        mUpvotes = upvotes;
        mDownvotes = downvotes;
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
}
