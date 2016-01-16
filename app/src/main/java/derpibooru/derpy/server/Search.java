package derpibooru.derpy.server;

import java.util.List;
import java.util.Map;

import derpibooru.derpy.data.types.DerpibooruImageThumb;

public class Search {
    Map<String, String> mQueryParams;

    public Search() {
    }

    public List<DerpibooruImageThumb> getThumbs() {
        throw new UnsupportedOperationException();
    }

    public Search sortBy(Sorting s) {
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

    public Search descending() {
        mQueryParams.put("sd", "desc");
        return this;
    }

    public Search ascending() {
        mQueryParams.put("sd", "asc");
        return this;
    }

    public Search itemsPerPage(int limit) {
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
