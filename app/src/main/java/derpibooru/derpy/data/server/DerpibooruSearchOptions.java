package derpibooru.derpy.data.server;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;

import static derpibooru.derpy.data.server.DerpibooruSearchOptions.UserPicksFilter.No;
import static derpibooru.derpy.data.server.DerpibooruSearchOptions.UserPicksFilter.UserPicksOnly;

public class DerpibooruSearchOptions implements Parcelable {
    private String mSearchQuery = "*";

    private SortBy mSortBy = SortBy.CreatedAt;
    private SortDirection mSortDirection = SortDirection.Descending;
    private UserPicksFilter mFavesFilter = No;
    private UserPicksFilter mUpvotesFilter = No;
    private UserPicksFilter mUploadsFilter = No;
    private UserPicksFilter mWatchedTagsFilter = No;
    private Integer mMinScore;
    private Integer mMaxScore;

    /**
     * Returns default search options.
     */
    public DerpibooruSearchOptions() { }

    public static DerpibooruSearchOptions copyFrom(DerpibooruSearchOptions from) {
        Parcel parcel = Parcel.obtain();
        from.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        DerpibooruSearchOptions c = DerpibooruSearchOptions.CREATOR.createFromParcel(parcel);
        parcel.recycle();
        return c;
    }

    public void setSearchQuery(String query) {
        mSearchQuery = query;
    }

    public void setSortBy(SortBy sortBy) {
        mSortBy = sortBy;
    }

    public void setSortDirection(SortDirection sortDirection) {
        mSortDirection = sortDirection;
    }

    public void setFavesFilter(UserPicksFilter favesFilter) {
        mFavesFilter = favesFilter;
    }

    public void setUpvotesFilter(UserPicksFilter upvotesFilter) {
        mUpvotesFilter = upvotesFilter;
    }

    public void setUploadsFilter(UserPicksFilter uploadsFilter) {
        mUploadsFilter = uploadsFilter;
    }

    public void setWatchedTagsFilter(UserPicksFilter watchedTagsFilter) {
        mWatchedTagsFilter = watchedTagsFilter;
    }

    public void setMinScore(Integer minScore) {
        mMinScore = minScore;
    }

    public void setMaxScore(Integer maxScore) {
        mMaxScore = maxScore;
    }

    public String getSearchQuery() {
        return mSearchQuery;
    }

    public SortBy getSortBy() {
        return mSortBy;
    }

    public SortDirection getSortDirection() {
        return mSortDirection;
    }

    public UserPicksFilter getFavesFilter() { return mFavesFilter; }

    public UserPicksFilter getUpvotesFilter() {
        return mUpvotesFilter;
    }

    public UserPicksFilter getUploadsFilter() {
        return mUploadsFilter;
    }

    public UserPicksFilter getWatchedTagsFilter() {
        return mWatchedTagsFilter;
    }

    /**
     * Return the search text for any of the user list filters.
     * Important: the individual filter state must be set first. This assumes only one filter is set.
     * @return A string that is the search text for the particular filter
     */
    public String getFilterParams() {
        switch (getFavesFilter()) {
            case No: break;
            case UserPicksOnly: return UserPicks.Faves.getFilterParams();
            case NoUserPicks: return "-" + UserPicks.Faves.getFilterParams();
        }
        switch (getUpvotesFilter()) {
            case No: break;
            case UserPicksOnly: return UserPicks.Upvotes.getFilterParams();
            case NoUserPicks: return "-" + UserPicks.Upvotes.getFilterParams();
        }
        switch (getUploadsFilter()) {
            case No: break;
            case UserPicksOnly: return UserPicks.Uploads.getFilterParams();
            case NoUserPicks: return "-" + UserPicks.Uploads.getFilterParams();
        }
        switch (getWatchedTagsFilter()) {
            case No: break;
            case UserPicksOnly: return UserPicks.WatchedTags.getFilterParams();
            case NoUserPicks: return "-" + UserPicks.WatchedTags.getFilterParams();
        }
        return null;
    }

    @Nullable
    public Integer getMinScore() {
        return mMinScore;
    }

    @Nullable
    public Integer getMaxScore() {
        return mMaxScore;
    }

    /* i'm not proud of these */

    public boolean isDisplayingWatchedTagsOnly() {
        return ((mWatchedTagsFilter == UserPicksOnly)
                && (mFavesFilter != UserPicksOnly)
                && (mUpvotesFilter != UserPicksOnly)
                && (mUploadsFilter != UserPicksOnly));
    }

    public boolean isDisplayingFavesOnly() {
        return ((mFavesFilter == UserPicksOnly)
                && (mWatchedTagsFilter != UserPicksOnly)
                && (mUpvotesFilter != UserPicksOnly)
                && (mUploadsFilter != UserPicksOnly));
    }

    public boolean isDisplayingUpvotesOnly() {
        return ((mUpvotesFilter == UserPicksOnly)
                && (mWatchedTagsFilter != UserPicksOnly)
                && (mFavesFilter != UserPicksOnly)
                && (mUploadsFilter != UserPicksOnly));
    }

    public boolean isDisplayingUploadsOnly() {
        return ((mUploadsFilter == UserPicksOnly)
                && (mWatchedTagsFilter != UserPicksOnly)
                && (mFavesFilter != UserPicksOnly)
                && (mUpvotesFilter != UserPicksOnly));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DerpibooruSearchOptions) {
            DerpibooruSearchOptions comp = (DerpibooruSearchOptions) o;
            return ((this.getSearchQuery().equals(comp.getSearchQuery()))
                    && (this.getSortBy() == comp.getSortBy())
                    && (this.getSortDirection() == comp.getSortDirection())
                    && (this.getFavesFilter() == comp.getFavesFilter())
                    && (this.getUpvotesFilter() == comp.getUpvotesFilter())
                    && (this.getUploadsFilter() == comp.getUploadsFilter())
                    && (this.getWatchedTagsFilter() == comp.getWatchedTagsFilter())
                    && (Objects.equal(this.getMinScore(), comp.getMinScore()))
                    && (Objects.equal(this.getMaxScore(), comp.getMaxScore())));
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getSearchQuery(), getSortBy(), getSortDirection(), getFavesFilter(), getUpvotesFilter(),
                                getUploadsFilter(), getWatchedTagsFilter(), getMinScore(), getMaxScore());
    }

    public enum SortBy {
        CreatedAt(0),
        Score(1),
        Relevance(2),
        Width(3),
        Height(4),
        Comments(5),
        Random(6);

        private int mValue;

        SortBy(int value) {
            mValue = value;
        }

        public static SortBy fromValue(int value) {
            for (SortBy type : values()) {
                if (type.mValue == value) {
                    return type;
                }
            }
            return CreatedAt;
        }

        public int toValue() {
            return mValue;
        }
    }

    public enum SortDirection {
        Descending(0),
        Ascending(1);

        private int mValue;

        SortDirection(int value) {
            mValue = value;
        }

        public static SortDirection fromValue(int value) {
            for (SortDirection type : values()) {
                if (type.mValue == value) {
                    return type;
                }
            }
            return Descending;
        }

        public int toValue() {
            return mValue;
        }
    }

    public enum UserPicks {
        Faves(0),
        Upvotes(1),
        Uploads(2),
        WatchedTags(3);

        private int mValue;

        UserPicks(int value) { mValue = value; }

        public static UserPicks fromValue(int value) {
            for (UserPicks type : values()) {
                if (type.mValue == value) {
                    return type;
                }
            }
            return Faves;
        }

        public int toValue() { return mValue; }

        public String getFilterParams() {
            switch (this) {
                case Faves:
                    return "my:faves";
                case Upvotes:
                    return "my:upvotes";
                case Uploads:
                    return "my:uploads";
                case WatchedTags:
                    return "my:watched";
            }
            return null;
        }
    }

    public enum UserPicksFilter {
        No(0),
        UserPicksOnly(1),
        NoUserPicks(2);

        private int mValue;

        UserPicksFilter(int value) {
            mValue = value;
        }

        public static UserPicksFilter fromValue(int value) {
            for (UserPicksFilter type : values()) {
                if (type.mValue == value) {
                    return type;
                }
            }
            return No;
        }

        public int toValue() {
            return mValue;
        }

    }

    protected DerpibooruSearchOptions(Parcel in) {
        mSearchQuery = in.readString();
        mSortBy = SortBy.fromValue(in.readInt());
        mSortDirection = SortDirection.fromValue(in.readInt());
        mFavesFilter = UserPicksFilter.fromValue(in.readInt());
        mUpvotesFilter = UserPicksFilter.fromValue(in.readInt());
        mUploadsFilter = UserPicksFilter.fromValue(in.readInt());
        mWatchedTagsFilter = UserPicksFilter.fromValue(in.readInt());
        /* the score values can be null, use classes instead of primitives */
        mMinScore = (Integer) in.readValue(Integer.class.getClassLoader());
        mMaxScore = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mSearchQuery);
        dest.writeInt(getSortBy().toValue());
        dest.writeInt(getSortDirection().toValue());
        dest.writeInt(getFavesFilter().toValue());
        dest.writeInt(getUpvotesFilter().toValue());
        dest.writeInt(getUploadsFilter().toValue());
        dest.writeInt(getWatchedTagsFilter().toValue());
        dest.writeValue(getMinScore());
        dest.writeValue(getMaxScore());
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DerpibooruSearchOptions> CREATOR = new Parcelable.Creator<DerpibooruSearchOptions>() {
        @Override
        public DerpibooruSearchOptions createFromParcel(Parcel in) {
            return new DerpibooruSearchOptions(in);
        }

        @Override
        public DerpibooruSearchOptions[] newArray(int size) {
            return new DerpibooruSearchOptions[size];
        }
    };
}
