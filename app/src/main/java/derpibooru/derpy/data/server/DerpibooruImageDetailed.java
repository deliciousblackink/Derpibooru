package derpibooru.derpy.data.server;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class DerpibooruImageDetailed implements Parcelable {
    private DerpibooruImageThumb mThumb;

    private String mSourceUrl;
    private String mUploader;
    private String mDescription;
    private String mCreatedAt;
    private List<DerpibooruTag> mTags = new ArrayList<>();
    private List<String> mFavedBy = new ArrayList<>();

    public DerpibooruImageDetailed(DerpibooruImageThumb image, String sourceUrl,
                                   String uploader, String description, String createdAt,
                                   List<DerpibooruTag> tags, List<String> favedBy) {
        mThumb = image;
        mSourceUrl = sourceUrl;
        mUploader = uploader;
        mDescription = description;
        mCreatedAt = createdAt;
        mTags = tags;
        mFavedBy = favedBy;
    }

    public DerpibooruImageThumb getThumb() {
        return mThumb;
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

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public List<DerpibooruTag> getTags() {
        return mTags;
    }

    public List<String> getFavedBy() {
        return mFavedBy;
    }

    protected DerpibooruImageDetailed(Parcel in) {
        mThumb = in.readParcelable(DerpibooruImageThumb.class.getClassLoader());
        mSourceUrl = in.readString();
        mUploader = in.readString();
        mDescription = in.readString();
        mCreatedAt = in.readString();
        in.readTypedList(mTags, DerpibooruTag.CREATOR);
        in.readStringList(mFavedBy);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mThumb, flags);
        dest.writeString(mSourceUrl);
        dest.writeString(mUploader);
        dest.writeString(mDescription);
        dest.writeString(mCreatedAt);
        dest.writeList(mTags);
        dest.writeStringList(mFavedBy);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DerpibooruImageDetailed> CREATOR = new Parcelable.Creator<DerpibooruImageDetailed>() {
        @Override
        public DerpibooruImageDetailed createFromParcel(Parcel in) {
            return new DerpibooruImageDetailed(in);
        }

        @Override
        public DerpibooruImageDetailed[] newArray(int size) {
            return new DerpibooruImageDetailed[size];
        }
    };
}