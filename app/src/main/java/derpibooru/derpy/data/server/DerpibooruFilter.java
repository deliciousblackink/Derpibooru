package derpibooru.derpy.data.server;

import java.util.List;

public class DerpibooruFilter {
    private int mId;
    private String mName;
    private List<Integer> mSpoileredTags;

    private String mDescription;
    private List<String> mHiddenTagNames;
    private List<String> mSpoileredTagNames;
    private boolean mSystemFilter;
    private int mUserCount;

    public DerpibooruFilter(int filterId, String filterName, List<Integer> spoileredTags) {
        mId = filterId;
        mName = filterName;
        mSpoileredTags = spoileredTags;
    }

    public DerpibooruFilter setAdditionalInfo(String description, List<String> hiddenTagNames,
                                              List<String> spoileredTagNames, boolean isSystemFilter,
                                              int userCount) {
        mDescription = description;
        mHiddenTagNames = hiddenTagNames;
        mSpoileredTagNames = spoileredTagNames;
        mSystemFilter = isSystemFilter;
        mUserCount = userCount;
        return this;
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

    public String getDescription() {
        return mDescription;
    }

    public List<String> getHiddenTagNames() {
        return mHiddenTagNames;
    }

    public List<String> getSpoileredTagNames() {
        return mSpoileredTagNames;
    }

    public boolean isSystemFilter() {
        return mSystemFilter;
    }

    public int getUserCount() {
        return mUserCount;
    }
}
