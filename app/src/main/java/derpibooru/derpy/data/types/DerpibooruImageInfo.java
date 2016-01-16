package derpibooru.derpy.data.types;

import java.util.ArrayList;

public class DerpibooruImageInfo {
    private String mImgUrl;
    private ArrayList<DerpibooruTag> mTags;
    private ArrayList<String> mFavedBy;

    public DerpibooruImageInfo(String imgUrl, ArrayList<DerpibooruTag> tags,
                               ArrayList<String> favedBy) {
        mImgUrl = "https:" + imgUrl;
        mTags = tags;
        mFavedBy = favedBy;
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
}
