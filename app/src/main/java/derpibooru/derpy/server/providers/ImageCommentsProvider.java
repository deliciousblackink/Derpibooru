package derpibooru.derpy.server.providers;

import android.content.Context;

import java.util.List;

import derpibooru.derpy.data.server.DerpibooruImageComment;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.parsers.ImageCommentsParser;

public class ImageCommentsProvider extends Provider<List<DerpibooruImageComment>> {
    private int mCurrentPage = 1;
    private int mImageId;

    public ImageCommentsProvider(Context context, QueryHandler<List<DerpibooruImageComment>> handler) {
        super(context, handler);
    }

    /**
     * Returns an ImageCommentsProvider for the particular image ID.
     *
     * @param id image ID
     */
    public ImageCommentsProvider id(int id) {
        mImageId = id;
        return this;
    }

    public ImageCommentsProvider nextPage() {
        mCurrentPage++;
        return this;
    }

    public ImageCommentsProvider resetPageNumber() {
        mCurrentPage = 1;
        return this;
    }

    protected int getCurrentPage() {
        return mCurrentPage;
    }

    @Override
    protected String generateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(DERPIBOORU_DOMAIN);
        sb.append("images/");
        sb.append(mImageId);
        sb.append("/comments");
        sb.append("?page=");
        sb.append(getCurrentPage());
        return sb.toString();
    }

    @Override
    public void fetch() {
        super.executeQuery(new ImageCommentsParser());
    }
}
