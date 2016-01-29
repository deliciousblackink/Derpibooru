package derpibooru.derpy.server;

import android.content.Context;

import derpibooru.derpy.server.parsers.ImageInfoParser;

public class ImageInfoProvider extends Provider {
    private int mId;

    public ImageInfoProvider(Context context, ProviderRequestHandler handler) {
        super(context, handler);
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

    @Override
    protected String generateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(DERPIBOORU_DOMAIN);
        sb.append("images/");
        sb.append(mId);
        return sb.toString();
    }

    @Override
    public void fetch() {
        super.executeQuery(generateUrl(), new ImageInfoParser());
    }
}
