package derpibooru.derpy.data.types;

import android.os.Parcel;
import android.os.Parcelable;

/* http://www.parcelabler.com/ is a lifesaver */

public class ImageThumb implements Parcelable {
    private int mId;
    private int mScore;
    private int mUpvotes;
    private int mDownvotes;
    private int mFaves;
    private int mCommentCount;
    private String mThumbUrl;

    public ImageThumb(int id, int score, int upvotes, int downvotes, int faves,
                      int comments, String thumbUrl) {
        mId = id;
        mScore = score;
        mUpvotes = upvotes;
        mDownvotes = downvotes;
        mFaves = faves;
        mCommentCount = comments;
        mThumbUrl = "https:" + thumbUrl;
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

    protected ImageThumb(Parcel in) {
        mId = in.readInt();
        mScore = in.readInt();
        mUpvotes = in.readInt();
        mDownvotes = in.readInt();
        mFaves = in.readInt();
        mCommentCount = in.readInt();
        mThumbUrl = in.readString();
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
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ImageThumb> CREATOR = new Parcelable.Creator<ImageThumb>() {
        @Override
        public ImageThumb createFromParcel(Parcel in) {
            return new ImageThumb(in);
        }

        @Override
        public ImageThumb[] newArray(int size) {
            return new ImageThumb[size];
        }
    };
}