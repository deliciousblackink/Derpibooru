package derpibooru.derpy.data.server;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class DerpibooruImageDetailed implements Parcelable {
    private final DerpibooruImageThumb mThumb;

    private final String mSourceUrl;
    private final String mDownloadUrl;
    private final String mUploader;
    private final String mDescription;
    private final String mCreatedAt;
    private final List<DerpibooruTag> mTags;
    private final List<String> mFavedBy;

    public DerpibooruImageDetailed(DerpibooruImageThumb image, String sourceUrl, String downloadUrl,
                                   String uploader, String description, String createdAt,
                                   List<DerpibooruTag> tags, List<String> favedBy) {
        mThumb = image;
        mSourceUrl = sourceUrl;
        mDownloadUrl = downloadUrl;
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

    public String getDownloadUrl() {
        return mDownloadUrl;
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
        mDownloadUrl = in.readString();
        mUploader = in.readString();
        mDescription = in.readString();
        mCreatedAt = in.readString();

        mTags = new ArrayList<>();
        in.readTypedList(mTags, DerpibooruTag.CREATOR);
        mFavedBy = new ArrayList<>();
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
        dest.writeString(mDownloadUrl);
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