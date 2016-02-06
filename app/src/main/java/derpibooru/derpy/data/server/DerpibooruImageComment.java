package derpibooru.derpy.data.server;

import android.os.Parcel;
import android.os.Parcelable;

public class DerpibooruImageComment implements Parcelable {
    private String mAuthor;
    private String mAuthorAvatarUrl;
    private String mText;
    private String mPostedAt;

    public DerpibooruImageComment(String author, String avatarUrl, String text, String postedAt) {
        mAuthor = author;
        mAuthorAvatarUrl = "https:" + avatarUrl;
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

    protected DerpibooruImageComment(Parcel in) {
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
    public static final Parcelable.Creator<DerpibooruImageComment> CREATOR = new Parcelable.Creator<DerpibooruImageComment>() {
        @Override
        public DerpibooruImageComment createFromParcel(Parcel in) {
            return new DerpibooruImageComment(in);
        }

        @Override
        public DerpibooruImageComment[] newArray(int size) {
            return new DerpibooruImageComment[size];
        }
    };
}
