package derpibooru.derpy.server;


import android.content.Context;

import java.net.URL;

import derpibooru.derpy.data.types.Image;
import derpibooru.derpy.server.util.Json;
import derpibooru.derpy.server.util.Query;
import derpibooru.derpy.server.util.QueryHandler;
import derpibooru.derpy.server.util.UrlBuilder;

/**
 * Asynchronous image data (id, score, etc.) fetcher. The receiving object has to implement the
 * 'QueryHandler' interface. Server response is passed via the 'queryPerformed' method as an
 * 'Image' object.
 */
public class ImageFetcher extends Query {
    private Integer mId;

    public ImageFetcher(Context context, QueryHandler handler) {
        super(context, handler);
    }

    /**
     * Sets the image ID.
     *
     * @param id image ID in the Derpibooru database
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
        Json json = new Json(response);
        Image i = json.readImage();
        if (i != null) {
            mQueryHandler.queryPerformed(json.readImage());
        } else {
            mQueryHandler.queryFailed();
        }
    }
}
