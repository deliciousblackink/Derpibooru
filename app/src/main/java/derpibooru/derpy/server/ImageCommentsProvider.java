package derpibooru.derpy.server;

import android.content.Context;

import derpibooru.derpy.server.parsers.ImageCommentsParser;

public class ImageCommentsProvider extends Provider {
    private int mCurrentPage = 1;
    private int mImageId;

    public ImageCommentsProvider(Context context, ProviderRequestHandler handler) {
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
        super.executeQuery(generateUrl(), new ImageCommentsParser());
    }
}
