package derpibooru.derpy.server;

import android.content.Context;

import java.net.URL;
import java.util.HashMap;

import derpibooru.derpy.data.server.DerpibooruSearchOptions;
import derpibooru.derpy.server.util.Query;
import derpibooru.derpy.server.util.QueryResultHandler;
import derpibooru.derpy.server.util.UrlBuilder;
import derpibooru.derpy.server.util.parsers.ImageListParser;

public class SearchResultProvider {
    private Query mQuery;

    public SearchResultProvider(Context context, QueryResultHandler handler) {
        mQuery = new Query(context, handler);
    }

    public void search(String query) {
        URL url = UrlBuilder.generateSearchUrl(new HashMap<String, String>(),
                                               query);
        mQuery.executeQuery(url, new ImageListParser());
    }

    public void search(String query, DerpibooruSearchOptions options) {
        URL url = UrlBuilder.generateSearchUrl(searchOptionsToUrlParams(options), query);
        mQuery.executeQuery(url, new ImageListParser());
    }

    /* TODO: merge this abomination with UrlBuilder to get rid of the boilerplate code */
    /* at this point i think it's better to make a single UrlProvider abstract class
     * and extend it to provide specific field conversions */

    private HashMap<String, String> searchOptionsToUrlParams(DerpibooruSearchOptions options) {
        HashMap<String, String> urlParams = new HashMap<>();

        String[] keyValuePair;

        keyValuePair = sortByParams(options.getSortBy());
        urlParams.put(keyValuePair[0], keyValuePair[1]);

        keyValuePair = sortDirectionParams(options.getSortDirection());
        urlParams.put(keyValuePair[0], keyValuePair[1]);

        keyValuePair = favesFilterParams(options.getFavesFilter());
        urlParams.put(keyValuePair[0], keyValuePair[1]);

        keyValuePair = upvotesFilterParams(options.getUpvotesFilter());
        urlParams.put(keyValuePair[0], keyValuePair[1]);

        keyValuePair = uploadsFilterParams(options.getUploadsFilter());
        urlParams.put(keyValuePair[0], keyValuePair[1]);

        keyValuePair = watchedTagsFilterParams(options.getWatchedTagsFilter());
        urlParams.put(keyValuePair[0], keyValuePair[1]);

        keyValuePair = minScoreParams(options.getMinScore());
        urlParams.put(keyValuePair[0], keyValuePair[1]);

        keyValuePair = maxScoreParams(options.getMaxScore());
        urlParams.put(keyValuePair[0], keyValuePair[1]);

        return urlParams;
    }

    private String[] sortByParams(DerpibooruSearchOptions.SortBy option) {
        switch (option) {
            case CreatedAt:
                return new String[] { "sf", "created_at"};
            case Relevance:
                return new String[] { "sf", "relevance"};
            case Score:
                return new String[] { "sf", "score"};
            case Width:
                return new String[] { "sf", "width"};
            case Height:
                return new String[] { "sf", "height"};
            case Comments:
                return new String[] { "sf", "comments"};
            case Random:
                return new String[] { "sf", "random"};
        }
        return new String[] { "", ""};
    }

    private String[] sortDirectionParams(DerpibooruSearchOptions.SortDirection option) {
        switch (option) {
            case Descending:
                return new String[] { "sd", "desc"};
            case Ascending:
                return new String[] { "sd", "asc"};
        }
        return new String[] { "", ""};
    }

    private String[] favesFilterParams(DerpibooruSearchOptions.UserPicksFilter option) {
        switch (option) {
            case No:
                return new String[] { "", ""};
            case UserPicksOnly:
                return new String[] { "faves", "only"};
            case NoUserPicks:
                return new String[] { "faves", "not"};
        }
        return new String[] { "", ""};
    }

    private String[] upvotesFilterParams(DerpibooruSearchOptions.UserPicksFilter option) {
        switch (option) {
            case No:
                return new String[] { "", ""};
            case UserPicksOnly:
                return new String[] { "upvotes", "only"};
            case NoUserPicks:
                return new String[] { "upvotes", "not"};
        }
        return new String[] { "", ""};
    }

    private String[] uploadsFilterParams(DerpibooruSearchOptions.UserPicksFilter option) {
        switch (option) {
            case No:
                return new String[] { "", ""};
            case UserPicksOnly:
                return new String[] { "uploads", "only"};
            case NoUserPicks:
                return new String[] { "uploads", "not"};
        }
        return new String[] { "", ""};
    }

    private String[] watchedTagsFilterParams(DerpibooruSearchOptions.UserPicksFilter option) {
        switch (option) {
            case No:
                return new String[] { "", ""};
            case UserPicksOnly:
                return new String[] { "watched", "only"};
            case NoUserPicks:
                return new String[] { "watched", "not"};
        }
        return new String[] { "", ""};
    }

    private String[] minScoreParams(Integer option) {
        if (option == null) {
            return new String[] { "", ""};
        }
        return new String[] { "min_score", Integer.toString(option)};
    }

    private String[] maxScoreParams(Integer option) {
        if (option == null) {
            return new String[] { "", ""};
        }
        return new String[] { "max_score", Integer.toString(option)};
    }
}
