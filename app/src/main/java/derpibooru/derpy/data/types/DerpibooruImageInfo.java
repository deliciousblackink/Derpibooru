package derpibooru.derpy.data.types;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class DerpibooruImageInfo implements Parcelable {
    private int mId;
    private String mSourceUrl;
    private String mUploader;
    private String mDescription;
    private ArrayList<DerpibooruTag> mTags;
    private ArrayList<String> mFavedBy;

    public DerpibooruImageInfo(int id, String sourceUrl, String uploader, String description,
                               ArrayList<DerpibooruTag> tags, ArrayList<String> favedBy) {
        mId = id;
        mSourceUrl = sourceUrl;
        mUploader = uploader;
        mDescription = description;
        mTags = tags;
        mFavedBy = favedBy;
    }

    public int getId() {
        return mId;
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

    public ArrayList<DerpibooruTag> getTags() {
        return mTags;
    }

    public ArrayList<String> getFavedBy() {
        return mFavedBy;
    }

    protected DerpibooruImageInfo(Parcel in) {
        mId = in.readInt();
        mSourceUrl = in.readString();
        mUploader = in.readString();
        mDescription = in.readString();
        in.readTypedList(mTags, DerpibooruTag.CREATOR);
        in.readStringList(mFavedBy);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mSourceUrl);
        dest.writeString(mUploader);
        dest.writeString(mDescription);
        dest.writeList(mTags);
        dest.writeStringList(mFavedBy);
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