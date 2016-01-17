package derpibooru.derpy.data.types;

import android.os.Parcel;
import android.os.Parcelable;

/* created automatically via
 * http://www.parcelabler.com/ */

public class DerpibooruTag implements Parcelable {
    private int mId;
    private int mImageCount;
    private String mName;
    private TagType mType;

    public DerpibooruTag(int id, int imageCount, String name, TagType type) {
        mId = id;
        mImageCount = imageCount;
        mName = name;
        mType = type;
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

    public TagType getType() {
        return mType;
    }

    protected DerpibooruTag(Parcel in) {
        mId = in.readInt();
        mImageCount = in.readInt();
        mName = in.readString();
        mType = TagType.getFromValue(in.readInt());
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
        dest.writeInt(mType.convertToValue());
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

    public enum TagType {
        Artist(1),
        ContentSafety(2),
        General(3);

        private int mValue;

        TagType(int value) {
            mValue = value;
        }

        public static TagType getFromValue(int value) {
            for (TagType type : values()) {
                if (type.mValue == value) {
                    return type;
                }
            }
            return General;
        }

        public int convertToValue() {
            return mValue;
        }
    }
}