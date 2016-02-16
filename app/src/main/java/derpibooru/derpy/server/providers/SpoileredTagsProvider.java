package derpibooru.derpy.server.providers;

import android.content.Context;

import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.QueryHandler;

public class SpoileredTagsProvider extends TagProvider {
    public SpoileredTagsProvider(Context context, QueryHandler handler) {
        super(context, handler);
    }

    @Override
    public void fetch() {
        new User(mContext, new User.UserRequestHandler() {
            @Override
            public void onUserDataObtained(DerpibooruUser userData) {
                SpoileredTagsProvider.super
                        .tags(userData.getCurrentFilter().getSpoileredTags()).fetch();
            }

            @Override
            public void onNetworkError() {
                mHandler.onQueryFailed();
            }
        }).fetchUserData();
    }
}
