package derpibooru.derpy.server;

import android.content.Context;

public class WatchedProvider extends ImageListProvider {
    public WatchedProvider(Context context, ProviderRequestHandler handler) {
        super(context, handler);
    }

    @Override
    protected String generateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(DERPIBOORU_DOMAIN);
        sb.append("images/watched.json");
        sb.append("?perpage=");
        sb.append(IMAGES_PER_PAGE);
        sb.append("&page=");
        sb.append(super.getCurrentPage());
        return sb.toString();
    }
}
