package derpibooru.derpy.server;

import android.content.Context;

import java.net.URL;
import java.util.HashMap;

import derpibooru.derpy.server.util.Query;
import derpibooru.derpy.server.util.QueryResultHandler;
import derpibooru.derpy.server.util.UrlBuilder;
import derpibooru.derpy.server.util.parsers.ImageListParser;

public class SearchResultProvider {
    private HashMap<String, String> mQueryParams;
    private Query mQuery;

    public SearchResultProvider(Context context, QueryResultHandler handler) {
        mQuery = new Query(context, handler);
        mQueryParams = new HashMap<>();
    }

    public void search(String query) {
        URL url = UrlBuilder.generateSearchUrl(mQueryParams, query);
        mQuery.executeQuery(url, new ImageListParser());
    }

    public SearchResultProvider sortBy(Sorting s) {
        switch (s) {
            case Score:
                mQueryParams.put("sf", "score");
                break;
            case Comments:
                mQueryParams.put("sf", "comments");
                break;
            case Relevance:
                mQueryParams.put("sf", "relevance");
                break;
            case Height:
                mQueryParams.put("sf", "height");
                break;
            case Width:
                mQueryParams.put("sf", "width");
                break;
            case Random:
                mQueryParams.put("sf", "random");
                break;
            case CreatedAt:
                mQueryParams.put("sf", "created_at");
                break;
        }
        return this;
    }

    public SearchResultProvider descending() {
        mQueryParams.put("sd", "desc");
        return this;
    }

    public SearchResultProvider ascending() {
        mQueryParams.put("sd", "asc");
        return this;
    }

    public SearchResultProvider itemsPerPage(int limit) {
        mQueryParams.put("perpage", Integer.toString(limit));
        return this;
    }

    public enum Sorting {
        Score,
        Comments,
        Relevance,
        Height,
        Width,
        Random,
        CreatedAt
    }
}
