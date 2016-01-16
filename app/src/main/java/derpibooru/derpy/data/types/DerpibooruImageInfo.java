package derpibooru.derpy.data.types;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class DerpibooruImageInfo implements Parcelable {
    /* parsed from HTML */
    private String mImgUrl;
    private ArrayList<DerpibooruTag> mTags;
    private ArrayList<String> mFavedBy;

    /* copied from DerpibooruImageThumb */
    private int mId;
    private String mImageUrl;
    private String mSourceUrl;
    private String mUploader;
    private String mDescription;

    public DerpibooruImageInfo(String imgUrl, ArrayList<DerpibooruTag> tags,
                               ArrayList<String> favedBy) {
        mImgUrl = "https:" + imgUrl;
        mTags = tags;
        mFavedBy = favedBy;
    }

    public void setImageInfoFromThumb(int id, String imageUrl, String sourceUrl,
                                      String uploader, String description) {
        mId = id;
        mImageUrl = imageUrl;
        mSourceUrl = sourceUrl;
        mUploader = uploader;
        mDescription = description;
    }

    public String getImgUrl() {
        return mImgUrl;
    }

    public ArrayList<DerpibooruTag> getTags() {
        return mTags;
    }

    public ArrayList<String> getFavedBy() {
        return mFavedBy;
    }

    public int getId() {
        return mId;
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

    protected DerpibooruImageInfo(Parcel in) {
        mImgUrl = in.readString();
        in.readTypedList(mTags, DerpibooruTag.CREATOR);
        in.readStringList(mFavedBy);
        mId = in.readInt();
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
        dest.writeString(mImgUrl);
        dest.writeList(mTags);
        dest.writeStringList(mFavedBy);
        dest.writeInt(mId);
        dest.writeString(mImageUrl);
        dest.writeString(mSourceUrl);
        dest.writeString(mUploader);
        dest.writeString(mDescription);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DerpibooruImageInfo> CREATOR = new Parcelable.Creator<DerpibooruImageInfo>() {
        @Override
        public DerpibooruImageInfo createFromParcel(Parcel in) {
            return new DerpibooruImageInfo(in);
        }

        @Override
        public DerpibooruImageInfo[] newArray(int size) {
            return new DerpibooruImageInfo[size];
        }
    };
}