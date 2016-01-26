package derpibooru.derpy.server;

import android.content.Context;

import java.net.URLEncoder;

import derpibooru.derpy.data.server.DerpibooruSearchOptions;
import derpibooru.derpy.server.parsers.ImageListParser;

public class SearchProvider extends DataProvider {
    private String mSearchQuery;
    private DerpibooruSearchOptions mSearchOptions;

    public SearchProvider(Context context, DataProviderRequestHandler handler) {
        super(context, handler);
    }

    public SearchProvider searching(String query) {
        mSearchQuery = query;
        return this;
    }

    public SearchProvider with(DerpibooruSearchOptions options) {
        mSearchOptions = options;
        return this;
    }

    @Override
    protected String generateUrl() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(DERPIBOORU_DOMAIN)
                    .append("search.json?utf8=✓")
                    .append("&sbq=").append(URLEncoder.encode(mSearchQuery, "UTF-8"))
                    .append("&sf=").append(sortByParams())
                    .append("&sd=").append(sortDirectionParams())
                    .append("&faves=").append(filterParams(mSearchOptions.getFavesFilter()))
                    .append("&upvotes=").append(filterParams(mSearchOptions.getUpvotesFilter()))
                    .append("&uploads=").append(filterParams(mSearchOptions.getUploadsFilter()))
                    .append("&watched=").append(filterParams(mSearchOptions.getWatchedTagsFilter()))
                    .append("&min_score=").append(scoreFilterParams(mSearchOptions.getMinScore()))
                    .append("&max_score=").append(scoreFilterParams(mSearchOptions.getMaxScore()));
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void fetch() {
        /* a search query yields a JSON reponse that has the
         * same structure as that of an image list, hence
         * ImageListParser is used. */
        super.executeQuery(generateUrl(), new ImageListParser());
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

    private String filterParams(DerpibooruSearchOptions.UserPicksFilter filter) {
        switch (filter) {
            case No:
                return "";
            case UserPicksOnly:
                return "only";
            case NoUserPicks:
                return "not";
        }
        return "";
    }

    private String scoreFilterParams(Integer option) {
        if (option == null) {
            return "";
        }
        return Integer.toString(option);
    }
}
