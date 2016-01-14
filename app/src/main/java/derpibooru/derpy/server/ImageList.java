package derpibooru.derpy.server;

import android.content.Context;

import java.net.URL;
import java.util.ArrayList;

import derpibooru.derpy.data.types.ImageThumb;
import derpibooru.derpy.server.util.*;

/**
 * Asynchronous image list (an array of {id, score, etc.}) fetcher. The receiving object has to
 * implement the 'QueryHandler' interface. Server response is passed via the 'queryPerformed'
 * method as an 'ArrayList<ImageThumb>' object.
 */
public class ImageList extends Query {
    private static final String ALL_TIME = "520w";
    /* 520 weeks (10 years) effectively equals to 'All Time' */

    private Type mListType;
    private String mTime = ALL_TIME;

    public ImageList(Context context, QueryHandler handler) {
        super(context, handler);
    }

    /**
     * Sets image list type (either Top Scoring or Most commented).
     *
     * @param listType the type of the image list
     */
    public ImageList type(Type listType) {
        mListType = listType;
        return this;
    }

    /**
     * Sets time limit for an image list in days
     * (the default value is All Time).
     *
     * @param hours the time limit in hours
     */
    public ImageList inHours(int hours)
    {
        mTime = Integer.toString(hours) + "h";
        return this;
    }

    /**
     * Sets time limit for an image list in days
     * (the default value is All Time).
     *
     * @param days the time limit in days
     */
    public ImageList inDays(int days)
    {
        mTime = Integer.toString(days) + "d";
        return this;
    }

    /**
     * Sets time limit for an image list in weeks
     * (the default value is All Time).
     *
     * @param weeks the time limit in weeks
     */
    public ImageList inWeeks(int weeks)
    {
        mTime = Integer.toString(weeks) + "w";
        return this;
    }

    /**
     * Requests Derpibooru server to fetch an image list with the parameters
     * specified via public methods of the class.
     */
    public void load()
    {
        URL url = UrlBuilder.generateListUrl(mListType, mTime);
        if (url != null) {
            executeQuery(url);
        } else {
            mQueryHandler.queryFailed();
        }
    }

    @Override
    public void processResponse(String response) {
        Json json = new Json(response);
        ArrayList<ImageThumb> img = json.readImageThumbs();
        mQueryHandler.queryPerformed(img);
    }

    public enum Type {
        TopScoring,
        MostCommented
    }
}
