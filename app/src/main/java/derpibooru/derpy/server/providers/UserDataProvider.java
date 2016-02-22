package derpibooru.derpy.server.providers;

import android.content.Context;

import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.parsers.UserDataParser;
import derpibooru.derpy.storage.UserDataStorage;

public class UserDataProvider extends Provider<DerpibooruUser> {
    private UserDataStorage mUserDataStorage;

    public UserDataProvider(Context context, QueryHandler<DerpibooruUser> handler) {
        super(context, handler);
        mUserDataStorage = new UserDataStorage(context);
    }

    public void refreshUserData() {
        mUserDataStorage.clearUserData();
        fetch();
    }

    public boolean isLoggedIn() {
        return mUserDataStorage.getUserData() != null
                && mUserDataStorage.getUserData().isLoggedIn();
    }

    @Override
    protected String generateUrl() {
        /* TODO: there is probably a better url to find user data at */
        StringBuilder sb = new StringBuilder();
        sb.append(DERPIBOORU_DOMAIN);
        sb.append("images?perpage=1");
        return sb.toString();
    }

    @Override
    public void fetch() {
        if (!isUserDataCached()) {
            super.executeQuery(new UserDataParser());
        }
    }

    @Override
    protected void cacheResponse(DerpibooruUser parsedResponse) {
        mUserDataStorage.setUserData(parsedResponse);
    }

    private boolean isUserDataCached() {
        DerpibooruUser cached = mUserDataStorage.getUserData();
        if (cached != null) {
            mHandler.onQueryExecuted(cached);
            return true;
        }
        return false;
    }
}
