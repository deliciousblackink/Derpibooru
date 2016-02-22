package derpibooru.derpy.data.server;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class DerpibooruImageThumb implements Parcelable {
    private int mId;
    private int mInternalId;
    private int mScore;
    private int mUpvotes;
    private int mDownvotes;
    private int mFaves;
    private int mCommentCount;
    private String mThumbUrl;
    private String mLargeUrl;

    private List<String> mSpoileredTagNames = new ArrayList<>();
    private String mSpoilerImageUrl;

    private List<DerpibooruImageInteraction.InteractionType> mImageInteractions = new ArrayList<>();

    /* a non-persistent variable used by image lists to indicate if a user has unspoilered an image */
    private boolean isSpoilered = false;

    public DerpibooruImageThumb(int id, int internalId, int score, int upvotes, int downvotes, int faves,
                                int comments, String thumbUrl, String largeUrl,
                                List<String> spoileredTagNames, String spoilerImageUrl) {
        mId = id;
        mInternalId = internalId;
        mScore = score;
        mUpvotes = upvotes;
        mDownvotes = downvotes;
        mFaves = faves;
        mCommentCount = comments;
        mThumbUrl = "https:" + thumbUrl;
        mLargeUrl = "https:" + largeUrl;

        mSpoileredTagNames = spoileredTagNames;
        mSpoilerImageUrl = spoilerImageUrl;

        isSpoilered = (mSpoileredTagNames.size() > 0);
    }

    public int getId() {
        return mId;
    }

    public int getInternalId() {
        return mInternalId;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int score) {
        mScore = score;
    }
    public int getUpvotes() {
        return mUpvotes;
    }

    public void setUpvotes(int upvotes) {
        mUpvotes = upvotes;
    }

    public int getDownvotes() {
        return mDownvotes;
    }

    public void setDownvotes(int downvotes) {
        mDownvotes = downvotes;
    }

    public int getFaves() {
        return mFaves;
    }

    public void setFaves(int faves) {
        mFaves = faves;
    }

    public int getCommentCount() {
        return mCommentCount;
    }

    public String getThumbUrl() {
        return mThumbUrl;
    }

    public String getLargeImageUrl() {
        return mLargeUrl;
    }

    public List<String> getSpoileredTagNames() {
        return mSpoileredTagNames;
    }

    public String getSpoilerImageUrl() {
        return mSpoilerImageUrl;
    }

    public List<DerpibooruImageInteraction.InteractionType> getImageInteractions() {
        return mImageInteractions;
    }

    public void addImageInteraction(DerpibooruImageInteraction.InteractionType interaction) {
        mImageInteractions.add(interaction);
    }

    public void removeImageInteraction(DerpibooruImageInteraction.InteractionType interaction) {
        mImageInteractions.remove(interaction);
    }

    public boolean isSpoilered() {
        return isSpoilered;
    }

    public void unspoiler() {
        isSpoilered = false;
    }

    protected DerpibooruImageThumb(Parcel in) {
        mId = in.readInt();
        mInternalId = in.readInt();
        mScore = in.readInt();
        mUpvotes = in.readInt();
        mDownvotes = in.readInt();
        mFaves = in.readInt();
        mCommentCount = in.readInt();
        mThumbUrl = in.readString();
        mLargeUrl = in.readString();
        in.readStringList(mSpoileredTagNames);
        mSpoilerImageUrl = in.readString();
        in.readList(mImageInteractions, List.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeInt(mInternalId);
        dest.writeInt(mScore);
        dest.writeInt(mUpvotes);
        dest.writeInt(mDownvotes);
        dest.writeInt(mFaves);
        dest.writeInt(mCommentCount);
        dest.writeString(mThumbUrl);
        dest.writeString(mLargeUrl);
        dest.writeStringList(mSpoileredTagNames);
        dest.writeString(mSpoilerImageUrl);
        dest.writeList(mImageInteractions);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DerpibooruImageThumb> CREATOR = new Parcelable.Creator<DerpibooruImageThumb>() {
        @Override
        public DerpibooruImageThumb createFromParcel(Parcel in) {
            return new DerpibooruImageThumb(in);
        }

        @Override
        public DerpibooruImageThumb[] newArray(int size) {
            return new DerpibooruImageThumb[size];
        }
    };
}