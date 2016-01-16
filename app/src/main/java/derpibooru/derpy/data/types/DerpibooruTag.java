package derpibooru.derpy.data.types;

public class DerpibooruTag {
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
}
