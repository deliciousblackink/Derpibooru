package derpibooru.derpy.server;

import android.content.Context;

import derpibooru.derpy.server.parsers.UserDataParser;

public class UserDataProvider extends DataProvider {
    public UserDataProvider(Context context, DataProviderRequestHandler handler) {
        super(context, handler);
    }

    @Override
    protected String generateUrl() {
        /* TODO: optimize the request url
         * there doesn't seem to be a dedicated page/an API endpoint
         * that would return only the current filter and the basic
         * info about the user (avatar, username, etc.)
         *
         * such info is only available at the image list
         * (search result) pages.
         * therefore, "/images?perpage=1" is used as it generates
         * the least server load possible by requesting the minimum
         * allowed amount of images. */
        StringBuilder sb = new StringBuilder();
        sb.append(DERPIBOORU_DOMAIN);
        sb.append("images?perpage=1");
        return sb.toString();
    }

    @Override
    public void fetch() {
        super.executeQuery(generateUrl(), new UserDataParser());
    }
}
