package derpibooru.derpy.server.providers;

import android.content.Context;

import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.parsers.UserDataParser;

public class UserDataProvider extends Provider<DerpibooruUser> {
    public UserDataProvider(Context context, QueryHandler<DerpibooruUser> handler) {
        super(context, handler);
    }

    public void refreshUserData() {
        fetch();
    }

    @Override
    protected String generateUrl() {
        /* TODO: there is probably a better url to find user data at */
        StringBuilder sb = new StringBuilder();
        sb.append(DERPIBOORU_DOMAIN);
        sb.append("about"); /* @/about@ is a static page — slightly better than querying an image list, but the point still stands */
        return sb.toString();
    }

    @Override
    public void fetch() {
        super.executeQuery(new UserDataParser());
    }
}
