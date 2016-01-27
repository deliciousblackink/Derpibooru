package derpibooru.derpy.data.server;

import java.util.List;

public class DerpibooruFilter {
    private int mId;
    private String mName;
    private List<Integer> mSpoileredTags;

    public DerpibooruFilter(int filterId, String filterName, List<Integer> spoileredTags) {
        mId = filterId;
        mName = filterName;
        mSpoileredTags = spoileredTags;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public List<Integer> getSpoileredTags() {
        return mSpoileredTags;
    }

    public boolean isTagSpoilered(int tagId) {
        return mSpoileredTags.contains(tagId);
    }
}
