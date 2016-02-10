package derpibooru.derpy.data.server;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class DerpibooruImageThumb implements Parcelable {
    private int mId;
    private int mScore;
    private int mUpvotes;
    private int mDownvotes;
    private int mFaves;
    private int mCommentCount;
    private String mThumbUrl;
    private String mLargeUrl;

    private List<String> mSpoileredTagNames = new ArrayList<>();
    private String mSpoilerImageUrl;

    /* a non-persistent variable used by image lists to indicate if a user has unspoilered an image */
    private boolean isSpoilered = true;

    public DerpibooruImageThumb(int id, int score, int upvotes, int downvotes, int faves,
                                int comments, String thumbUrl, String largeUrl,
                                List<String> spoileredTagNames, String spoilerImageUrl) {
        mId = id;
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

    public int getScore() {
        return mScore;
    }

    public int getUpvotes() {
        return mUpvotes;
    }

    public int getDownvotes() {
        return mDownvotes;
    }

    public int getFaves() {
        return mFaves;
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

    public boolean isSpoilered() {
        return isSpoilered;
    }

    public void unspoiler() {
        isSpoilered = false;
    }

    public List<String> getSpoileredTagNames() {
        return mSpoileredTagNames;
    }

    public String getSpoilerImageUrl() {
        return mSpoilerImageUrl;
    }

    protected DerpibooruImageThumb(Parcel in) {
        mId = in.readInt();
        mScore = in.readInt();
        mUpvotes = in.readInt();
        mDownvotes = in.readInt();
        mFaves = in.readInt();
        mCommentCount = in.readInt();
        mThumbUrl = in.readString();
        mLargeUrl = in.readString();
        in.readStringList(mSpoileredTagNames);
        mSpoilerImageUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeInt(mScore);
        dest.writeInt(mUpvotes);
        dest.writeInt(mDownvotes);
        dest.writeInt(mFaves);
        dest.writeInt(mCommentCount);
        dest.writeString(mThumbUrl);
        dest.writeString(mLargeUrl);
        dest.writeStringList(mSpoileredTagNames);
        dest.writeString(mSpoilerImageUrl);
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