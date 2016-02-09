package derpibooru.derpy.data.server;

import android.support.annotation.Nullable;

import com.google.common.base.Objects;

public class DerpibooruSearchOptions {
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
    public DerpibooruSearchOptions() {
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

    @Override
    public boolean equals(Object o) {
        if (o instanceof DerpibooruSearchOptions) {
            DerpibooruSearchOptions comp = (DerpibooruSearchOptions) o;
            return (this.getSortBy() == comp.getSortBy())
                    && (this.getSortDirection() == comp.getSortDirection())
                    && (this.getFavesFilter() == comp.getFavesFilter())
                    && (this.getUpvotesFilter() == comp.getUpvotesFilter())
                    && (this.getUploadsFilter() == comp.getUploadsFilter())
                    && (this.getWatchedTagsFilter() == comp.getWatchedTagsFilter())
                    && (Objects.equal(this.getMinScore(), comp.getMinScore()))
                    && (Objects.equal(this.getMaxScore(), comp.getMaxScore()));
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getSortBy(), getSortDirection(), getFavesFilter(), getUpvotesFilter(),
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
    }
}
