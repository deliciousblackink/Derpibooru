package derpibooru.derpy.server;

import android.content.Context;

import java.net.URL;

import derpibooru.derpy.server.util.Query;
import derpibooru.derpy.server.util.QueryResultHandler;
import derpibooru.derpy.server.util.UrlBuilder;
import derpibooru.derpy.server.util.parsers.ImageInfoParser;

/**
 * Asynchronous full image info (tags, faved by) provider. The receiving object has to implement the
 * 'QueryResultHandler' interface. Server response is passed via the 'onQueryExecuted' method as a
 * 'DerpibooruImageInfo' object.
 */
public class ImageInfoProvider {
    private int mId;
    private Query mQuery;

    public ImageInfoProvider(Context context, QueryResultHandler handler) {
        mQuery = new Query(context, handler);
    }

    /**
     * Returns an ImageInfoProvider for the particular image ID.
     *
     * @param id image ID
     */
    public ImageInfoProvider id(int id) {
        mId = id;
        return this;
    }

    /**
     * Requests Derpibooru server to fetch the image with an ID specified
     * via 'id' method of the class.
     */
    public void fetch() {
        URL url = UrlBuilder.generateImageUrl(mId);
        mQuery.executeQuery(url, new ImageInfoParser());
    }
}
