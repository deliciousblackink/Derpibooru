package derpibooru.derpy.data.server;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;

public class DerpibooruSearchOptions implements Parcelable {
    private String mSearchQuery = "*";

    private SortBy mSortBy = SortBy.CreatedAt;
    private SortDirection mSortDirection = SortDirection.Descending;
    private UserPicksFilter mFavesFilter = UserPicksFilter.No;
    private UserPicksFilter mUpvotesFilter = UserPicksFilter.No;
    private UserPicksFilter mUploadsFilter = UserPicksFilter.No;
    private UserPicksFilter mWatchedTagsFilter = UserPicksFilter.No;
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

    public UserPicksFilter getFavesFilter() {
        return mFavesFilter;
    }

    public UserPicksFilter getUpvotesFilter() {
        return mUpvotesFilter;
    }

    public UserPicksFilter getUploadsFilter() {
        return mUploadsFilter;
    }

    public UserPicksFilter getWatchedTagsFilter() {
        return mWatchedTagsFilter;
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
        return ((mWatchedTagsFilter == UserPicksFilter.UserPicksOnly)
                && (mFavesFilter != UserPicksFilter.UserPicksOnly)
                && (mUpvotesFilter != UserPicksFilter.UserPicksOnly)
                && (mUploadsFilter != UserPicksFilter.UserPicksOnly));
    }

    public boolean isDisplayingFavesOnly() {
        return ((mFavesFilter == UserPicksFilter.UserPicksOnly)
                && (mWatchedTagsFilter != UserPicksFilter.UserPicksOnly)
                && (mUpvotesFilter != UserPicksFilter.UserPicksOnly)
                && (mUploadsFilter != UserPicksFilter.UserPicksOnly));
    }

    public boolean isDisplayingUpvotesOnly() {
        return ((mUpvotesFilter == UserPicksFilter.UserPicksOnly)
                && (mWatchedTagsFilter != UserPicksFilter.UserPicksOnly)
                && (mFavesFilter != UserPicksFilter.UserPicksOnly)
                && (mUploadsFilter != UserPicksFilter.UserPicksOnly));
    }

    public boolean isDisplayingUploadsOnly() {
        return ((mUploadsFilter == UserPicksFilter.UserPicksOnly)
                && (mWatchedTagsFilter != UserPicksFilter.UserPicksOnly)
                && (mFavesFilter != UserPicksFilter.UserPicksOnly)
                && (mUpvotesFilter != UserPicksFilter.UserPicksOnly));
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
