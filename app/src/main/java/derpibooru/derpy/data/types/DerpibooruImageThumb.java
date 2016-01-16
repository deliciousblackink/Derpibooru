package derpibooru.derpy.data.types;

import android.os.Parcel;
import android.os.Parcelable;

/* http://www.parcelabler.com/ is a lifesaver */

public class DerpibooruImageThumb implements Parcelable {
    private int mId;
    private int mScore;
    private int mUpvotes;
    private int mDownvotes;
    private int mFaves;
    private int mCommentCount;

    /* These values are not used in image lists, but they are
     * more easily acquired via JSON request than via HTML parsing,
     * hence they are stored there and passed to the ImageActivity. */
    private String mThumbUrl;
    private String mImageUrl;
    private String mSourceUrl;
    private String mUploader;
    private String mDescription;

    public DerpibooruImageThumb(int id, int score, int upvotes, int downvotes, int faves,
                                int comments, String thumbUrl, String imageUrl, String sourceUrl,
                                String uploader, String description) {
        mId = id;
        mScore = score;
        mUpvotes = upvotes;
        mDownvotes = downvotes;
        mFaves = faves;
        mCommentCount = comments;
        mThumbUrl = "https:" + thumbUrl;
        mImageUrl = "https:" + imageUrl;
        mSourceUrl = sourceUrl;
        mUploader = uploader;
        mDescription = description;
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

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getSourceUrl() {
        return mSourceUrl;
    }

    public String getUploader() {
        return mUploader;
    }

    public String getDescription() {
        return mDescription;
    }

    protected DerpibooruImageThumb(Parcel in) {
        mId = in.readInt();
        mScore = in.readInt();
        mUpvotes = in.readInt();
        mDownvotes = in.readInt();
        mFaves = in.readInt();
        mCommentCount = in.readInt();
        mThumbUrl = in.readString();
        mImageUrl = in.readString();
        mSourceUrl = in.readString();
        mUploader = in.readString();
        mDescription = in.readString();
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
        dest.writeString(mImageUrl);
        dest.writeString(mSourceUrl);
        dest.writeString(mUploader);
        dest.writeString(mDescription);
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