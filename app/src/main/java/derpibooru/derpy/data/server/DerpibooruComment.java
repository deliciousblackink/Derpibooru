package derpibooru.derpy.data.server;

import android.os.Parcel;
import android.os.Parcelable;

public class DerpibooruComment implements Parcelable {
    private String mAuthor;
    private String mAuthorAvatarUrl;
    private String mText;
    private String mPostedAt;

    public DerpibooruComment(String author, String avatarUrl, String text, String postedAt) {
        mAuthor = author;
        mAuthorAvatarUrl = avatarUrl;
        mText = text;
        mPostedAt = postedAt;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getAuthorAvatarUrl() {
        return mAuthorAvatarUrl;
    }

    public String getText() {
        return mText;
    }

    public String getPostedAt() {
        return mPostedAt;
    }

    protected DerpibooruComment(Parcel in) {
        mAuthor = in.readString();
        mAuthorAvatarUrl = in.readString();
        mText = in.readString();
        mPostedAt = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAuthor);
        dest.writeString(mAuthorAvatarUrl);
        dest.writeString(mText);
        dest.writeString(mPostedAt);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DerpibooruComment> CREATOR = new Parcelable.Creator<DerpibooruComment>() {
        @Override
        public DerpibooruComment createFromParcel(Parcel in) {
            return new DerpibooruComment(in);
        }

        @Override
        public DerpibooruComment[] newArray(int size) {
            return new DerpibooruComment[size];
        }
    };
}
