package derpibooru.derpy.server;

import android.content.Context;

import java.net.URL;

import derpibooru.derpy.data.types.DerpibooruImageInfo;
import derpibooru.derpy.data.types.DerpibooruImageThumb;
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
    private DerpibooruImageThumb mThumb;

    public ImageFetcher(Context context, QueryHandler handler) {
        super(context, handler);
    }

    /**
     * Returns an ImageFetcher for the particular image thumb.
     *
     * @param thumb an image thumb
     */
    public ImageFetcher imageByThumb(DerpibooruImageThumb thumb) {
        mThumb = thumb;
        return this;
    }

    /**
     * Requests Derpibooru server to fetch the image with an ID specified
     * via 'id' method of the class.
     */
    public void fetch() {
        URL url = UrlBuilder.generateImageUrl(mThumb.getId());
        if (url != null) {
            executeQuery(url);
        } else {
            mQueryHandler.queryFailed();
        }
    }

    @Override
    public void processResponse(String response) {
        HtmlParser html = new HtmlParser(response);
        DerpibooruImageInfo i = html.readImage();
        if (i != null) {
            i.setImageInfoFromThumb(mThumb.getId(),
                                    mThumb.getImageUrl(), mThumb.getSourceUrl(),
                                    mThumb.getUploader(), mThumb.getDescription());
            mQueryHandler.queryPerformed(i);
        } else {
            mQueryHandler.queryFailed();
        }
    }
}
