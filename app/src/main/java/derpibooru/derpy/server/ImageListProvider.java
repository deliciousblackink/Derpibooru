package derpibooru.derpy.server;

import android.content.Context;

import java.net.URL;

import derpibooru.derpy.server.util.Query;
import derpibooru.derpy.server.util.QueryResultHandler;
import derpibooru.derpy.server.util.UrlBuilder;
import derpibooru.derpy.server.util.parsers.ImageListParser;

/**
 * Asynchronous image list (an array of {id, score, etc.}) provider. The receiving object has to
 * implement the 'QueryResultHandler' interface. Server response is passed via the 'onQueryExecuted'
 * method as an 'ArrayList<DerpibooruImageThumb>' object.
 */
public class ImageListProvider {
    private static final String ALL_TIME = "520w";
    /* 520 weeks (10 years) effectively equals to 'All Time' */

    private Type mListType;
    private String mTime = ALL_TIME;

    private Query mQuery;

    public ImageListProvider(Context context, QueryResultHandler handler) {
        mQuery = new Query(context, handler);
    }

    /**
     * Sets image list type (either Top Scoring or Most commented).
     *
     * @param listType the type of the image list
     */
    public ImageListProvider type(Type listType) {
        mListType = listType;
        return this;
    }

    /**
     * Sets time limit for an image list in days
     * (the default value is All Time).
     *
     * @param hours the time limit in hours
     */
    public ImageListProvider inHours(int hours) {
        mTime = Integer.toString(hours) + "h";
        return this;
    }

    /**
     * Sets time limit for an image list in days
     * (the default value is All Time).
     *
     * @param days the time limit in days
     */
    public ImageListProvider inDays(int days) {
        mTime = Integer.toString(days) + "d";
        return this;
    }

    /**
     * Sets time limit for an image list in weeks
     * (the default value is All Time).
     *
     * @param weeks the time limit in weeks
     */
    public ImageListProvider inWeeks(int weeks) {
        mTime = Integer.toString(weeks) + "w";
        return this;
    }

    /**
     * Requests Derpibooru server to fetch an image list with the parameters
     * specified via public methods of the class.
     */
    public void load() {
        URL url = UrlBuilder.generateListUrl(mListType, mTime);
        mQuery.executeQuery(url, new ImageListParser());
    }

    public enum Type {
        TopScoring,
        MostCommented
    }
}
