package derpibooru.derpy.server.providers;

import android.content.Context;
import android.util.Log;

import java.net.URLEncoder;
import java.util.List;

import derpibooru.derpy.data.server.DerpibooruFilter;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.data.server.DerpibooruSearchOptions;
import derpibooru.derpy.server.QueryHandler;

public class SearchProvider extends ImageListProvider {
    private DerpibooruSearchOptions mSearchOptions;

    public SearchProvider(Context context, QueryHandler<List<DerpibooruImageThumb>> handler,
                          DerpibooruFilter imageListFilter) {
        super(context, handler, imageListFilter);
    }

    public SearchProvider searching(DerpibooruSearchOptions options) {
        mSearchOptions = options;
        return this;
    }

    @Override
    protected String generateUrl() {
        try {
            StringBuilder sb = new StringBuilder();
            String searchString = URLEncoder.encode(mSearchOptions.getSearchQuery(), "UTF-8");
            if (mSearchOptions.getFilterParams() != null) {
                // Derpibooru use the search field to indicate the specific user lists
                searchString += "," + mSearchOptions.getFilterParams();
            }

            sb.append(DERPIBOORU_DOMAIN)
                    .append("search.json?utf8=✓")
                    .append("&sbq=").append(searchString)
                    .append("&sf=").append(sortByParams())
                    .append("&sd=").append(sortDirectionParams())
                    //.append("&faves=").append(filterParams(mSearchOptions.getFavesFilter())) // TODO: remove this non-existent API
                    //.append("&upvotes=").append(filterParams(mSearchOptions.getUpvotesFilter())) // TODO: remove this non-existent API
                    //.append("&uploads=").append(filterParams(mSearchOptions.getUploadsFilter())) // TODO: remove this non-existent API
                    //.append("&watched=").append(filterParams(mSearchOptions.getWatchedTagsFilter())) // TODO: remove this non-existent API
                    .append("&min_score=").append(scoreFilterParams(mSearchOptions.getMinScore())) // TODO: replace this API with sth newer
                    .append("&max_score=").append(scoreFilterParams(mSearchOptions.getMaxScore())); // TODO: replace this API with sth newer
            sb.append("&perpage=");
            sb.append(IMAGES_PER_PAGE);
            sb.append("&page=");
            sb.append(super.getCurrentPage());
            Log.d("auzbuzzard", "url: " + sb.toString());
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private String sortByParams() {
        switch (mSearchOptions.getSortBy()) {
            case CreatedAt:
                return "created_at";
            case Relevance:
                return "relevance";
            case Score:
                return "score";
            case Width:
                return "width";
            case Height:
                return "height";
            case Comments:
                return "comments";
            case Random:
                return "random";
        }
        return "";
    }

    private String sortDirectionParams() {
        switch (mSearchOptions.getSortDirection()) {
            case Descending:
                return "desc";
            case Ascending:
                return "asc";
        }
        return "";
    }

//    private String filterParams(DerpibooruSearchOptions.UserPicksFilter filter) {
//        switch (filter) {
//            case No:
//                return "";
//            case UserPicksOnly:
//                return "only";
//            case NoUserPicks:
//                return "not";
//        }
//        return "";
//    }

    private String scoreFilterParams(Integer option) {
        return (option != null) ? Integer.toString(option) : "";
    }
}
