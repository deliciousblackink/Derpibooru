package derpibooru.derpy.server;

import android.content.Context;

import java.net.URL;

import derpibooru.derpy.data.types.DerpibooruImageInfo;
import derpibooru.derpy.server.util.HtmlParser;
import derpibooru.derpy.server.util.Query;
import derpibooru.derpy.server.util.QueryHandler;
import derpibooru.derpy.server.util.UrlBuilder;

/**
 * Asynchronous full image info (tags, faved by) fetcher. The receiving object has to implement the
 * 'QueryHandler' interface. Server response is passed via the 'queryPerformed' method as an
 * 'DerpibooruImageInfo' object.
 */
public class ImageFetcher extends Query {
    private int mId;

    public ImageFetcher(Context context, QueryHandler handler) {
        super(context, handler);
    }

    /**
     * Returns an ImageFetcher for the particular image ID.
     *
     * @param id image ID
     */
    public ImageFetcher id(int id) {
        mId = id;
        return this;
    }

    /**
     * Requests Derpibooru server to fetch the image with an ID specified
     * via 'id' method of the class.
     */
    public void fetch() {
        URL url = UrlBuilder.generateImageUrl(mId);
        if (url != null) {
            executeQuery(url);
        } else {
            mQueryHandler.queryFailed();
        }
    }

    @Override
    public void processResponse(String response) {
        HtmlParser html = new HtmlParser(response);
        DerpibooruImageInfo i = html.readImage(mId);
        if (i != null) {
            mQueryHandler.queryPerformed(i);
        } else {
            mQueryHandler.queryFailed();
        }
    }
}
