package derpibooru.derpy.data.server;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.regex.Pattern;

public class DerpibooruTag implements Parcelable {
    private final int mId;
    private final int mImageCount;
    private final String mName;
    private final TagType mType;

    public DerpibooruTag(int id, int imageCount, String name) {
        mId = id;
        mImageCount = imageCount;
        mName = name;
        mType = setTagType();
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
        mType = TagType.fromValue(in.readInt());
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
        dest.writeInt(mType.toValue());
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
        Artist(0),
        SpoilerWarning(1),
        ContentSafety(2),
        OC(3),
        General(4);

        private int mValue;

        TagType(int value) {
            mValue = value;
        }

        public static TagType fromValue(int value) {
            for (TagType type : values()) {
                if (type.mValue == value) {
                    return type;
                }
            }
            return General;
        }

        public int toValue() {
            return mValue;
        }
    }

    private TagType setTagType() {
        /* TODO: add spoiler and OC tags */
        if (Pattern.compile("^(artist:)").matcher(mName).find()) {
            return DerpibooruTag.TagType.Artist;
        }
        if (Pattern.compile("^(spoiler:)").matcher(mName).find()) {
            return TagType.SpoilerWarning;
        }
        if (Pattern.compile("^(oc:)").matcher(mName).find()) {
            return TagType.OC;
        }
        for (Map.Entry<String, TagType> tag : SYSTEM_TAGS.entrySet()) {
            if (mName.equals(tag.getKey())) {
                return tag.getValue();
            }
        }
        return DerpibooruTag.TagType.General;
    }

    static final Map<String, TagType> SYSTEM_TAGS = ImmutableMap.<String, TagType>builder()
            /* artist tags */
            .put("anonymous artist", TagType.Artist)
            .put("artist needed", TagType.Artist)
            .put("edit", TagType.Artist)
            /* spoiler warning tags */
            .put("spoilers for another series", TagType.SpoilerWarning)
            .put("spoiler", TagType.SpoilerWarning)
             /* content safety tags */
            .put("explicit", TagType.ContentSafety)
            .put("grimdark", TagType.ContentSafety)
            .put("grotesque", TagType.ContentSafety)
            .put("questionable", TagType.ContentSafety)
            .put("safe", TagType.ContentSafety)
            .put("semi-grimdark", TagType.ContentSafety)
            .put("suggestive", TagType.ContentSafety)
            /* oc tags */
            .put("oc", TagType.OC)
            .put("oc only", TagType.OC)
            .build();
}