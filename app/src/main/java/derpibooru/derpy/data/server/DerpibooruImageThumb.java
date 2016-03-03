package derpibooru.derpy.data.server;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.EnumSet;

public class DerpibooruImageThumb implements Parcelable {
    private int mId;
    private int mIdUsedForImageInteractions;
    private int mUpvotes;
    private int mDownvotes;
    private int mFaves;
    private int mCommentCount;
    private String mThumbUrl;
    private String mLargeUrl;
    private String mSpoilerImageUrl;

    private EnumSet<DerpibooruImageInteraction.InteractionType> mImageInteractions;

    public DerpibooruImageThumb(DerpibooruImageThumb from) {
        this(from.getId(), from.getIdForImageInteractions(), from.getUpvotes(), from.getDownvotes(),
             from.getFaves(), from.getCommentCount(), from.getThumbUrl(), from.getLargeImageUrl(),
             from.getSpoilerImageUrl(), from.getImageInteractions());
    }

    public DerpibooruImageThumb(int id, int interactionsId, int upvotes, int downvotes, int faves,
                                int comments, String thumbUrl, String largeUrl, String spoilerImageUrl,
                                EnumSet<DerpibooruImageInteraction.InteractionType> interactions) {
        mId = id;
        mIdUsedForImageInteractions = interactionsId;
        mUpvotes = upvotes;
        mDownvotes = downvotes;
        mFaves = faves;
        mCommentCount = comments;
        mThumbUrl = thumbUrl;
        mLargeUrl = largeUrl;
        mSpoilerImageUrl = spoilerImageUrl;
        mImageInteractions = interactions;
    }

    public int getId() {
        return mId;
    }

    public int getIdForImageInteractions() {
        return mIdUsedForImageInteractions;
    }

    public int getScore() {
        return (getUpvotes() - getDownvotes());
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

    public String getSpoilerImageUrl() {
        return mSpoilerImageUrl;
    }

    public EnumSet<DerpibooruImageInteraction.InteractionType> getImageInteractions() {
        return mImageInteractions;
    }

    public boolean isSpoilered() {
        return !mSpoilerImageUrl.isEmpty();
    }

    public void unspoiler() {
        mSpoilerImageUrl = "";
    }

    protected DerpibooruImageThumb(Parcel in) {
        mId = in.readInt();
        mIdUsedForImageInteractions = in.readInt();
        mUpvotes = in.readInt();
        mDownvotes = in.readInt();
        mFaves = in.readInt();
        mCommentCount = in.readInt();
        mThumbUrl = in.readString();
        mLargeUrl = in.readString();
        mSpoilerImageUrl = in.readString();

        int[] interactionValues = in.createIntArray();
        for (int value : interactionValues) {
            mImageInteractions.add(DerpibooruImageInteraction.InteractionType.fromValue(value));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeInt(mIdUsedForImageInteractions);
        dest.writeInt(mUpvotes);
        dest.writeInt(mDownvotes);
        dest.writeInt(mFaves);
        dest.writeInt(mCommentCount);
        dest.writeString(mThumbUrl);
        dest.writeString(mLargeUrl);
        dest.writeString(mSpoilerImageUrl);

        /* TODO: redo transition of Set<enum> to parcel */
        int[] interactionValues = new int[mImageInteractions.size()];
        int iterator = 0;
        for (DerpibooruImageInteraction.InteractionType interactionType : mImageInteractions) {
            interactionValues[iterator] = interactionType.toValue();
            iterator++;
        }
        dest.writeIntArray(interactionValues);
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