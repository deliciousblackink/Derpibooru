package derpibooru.derpy.data.server;

import android.os.Parcel;
import android.os.Parcelable;

public class DerpibooruTagFull extends DerpibooruTag {
    private String mDescription;
    private String mSpoilerUrl;

    public DerpibooruTagFull(int id, int imageCount, String name,
                             String description, String spoilerUrl) {
        super(id, imageCount, name);
        mDescription = description;
        mSpoilerUrl = "https:" + spoilerUrl;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getSpoilerUrl() {
        return mSpoilerUrl;
    }

    protected DerpibooruTagFull(Parcel in) {
        super(in);
        mDescription = in.readString();
        mSpoilerUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mDescription);
        dest.writeString(mSpoilerUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DerpibooruTagFull> CREATOR = new Parcelable.Creator<DerpibooruTagFull>() {
        @Override
        public DerpibooruTagFull createFromParcel(Parcel in) {
            return new DerpibooruTagFull(in);
        }

        @Override
        public DerpibooruTagFull[] newArray(int size) {
            return new DerpibooruTagFull[size];
        }
    };
}
