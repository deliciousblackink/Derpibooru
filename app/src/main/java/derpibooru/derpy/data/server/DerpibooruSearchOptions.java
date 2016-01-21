package derpibooru.derpy.data.server;

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

    public DerpibooruSearchOptions(SortBy sortBy,
                                   SortDirection sortDirection,
                                   UserPicksFilter favesFilter,
                                   UserPicksFilter upvotesFilter,
                                   UserPicksFilter uploadsFilter,
                                   UserPicksFilter watchedTagsFilter,
                                   Integer minScore, Integer maxScore) {
        mSortBy = sortBy;
        mSortDirection = sortDirection;
        mFavesFilter = favesFilter;
        mUpvotesFilter = upvotesFilter;
        mUploadsFilter = uploadsFilter;
        mWatchedTagsFilter = watchedTagsFilter;
        mMinScore = minScore;
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

    public Integer getMinScore() {
        return mMinScore;
    }

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
                    && (bothNullOrEqual(this.getMinScore(), comp.getMinScore()))
                    && (bothNullOrEqual(this.getMaxScore(), comp.getMaxScore()));
        }
        return super.equals(o);
    }

    private boolean bothNullOrEqual(Object o1, Object o2) {
        /* Java's native Objects.equals requires API 19 */
        return (o1 == null && o2 == null) ||
                (!(o1 == null || o2 == null)) && o1.equals(o2);
    }

    public enum SortBy {
        CreatedAt,
        Score,
        Relevance,
        Width,
        Height,
        Comments,
        Random
    }

    public enum SortDirection {
        Descending,
        Ascending
    }

    public enum UserPicksFilter {
        No,
        UserPicksOnly,
        NoUserPicks
    }
}
