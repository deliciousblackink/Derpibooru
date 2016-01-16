package derpibooru.derpy.data.types;

import android.os.Parcel;
import android.os.Parcelable;

/* created automatically via
 * http://www.parcelabler.com/ */

public class DerpibooruTag implements Parcelable {
    private int mId;
    private int mImageCount;
    private String mName;

    public DerpibooruTag(int id, int imageCount, String name) {
        mId = id;
        mImageCount = imageCount;
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public int getNumberOfImages() {
        return mImageCount;
    }

    public String getName() {
        return mName;
    }

    protected DerpibooruTag(Parcel in) {
        mId = in.readInt();
        mImageCount = in.readInt();
        mName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeInt(mImageCount);
        dest.writeString(mName);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DerpibooruTag> CREATOR = new Parcelable.Creator<DerpibooruTag>() {
        @Override
        public DerpibooruTag createFromParcel(Parcel in) {
            return new DerpibooruTag(in);
        }

        @Override
        public DerpibooruTag[] newArray(int size) {
            return new DerpibooruTag[size];
        }
    };
}