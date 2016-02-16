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
        new UserDataProvider(mContext, new QueryHandler() {
            @Override
            public void onQueryExecuted(Object result) {
                SpoileredTagsProvider.super.tags(((DerpibooruUser) result).getCurrentFilter().getSpoileredTags());
                SpoileredTagsProvider.super.fetch();
            }

            @Override
            public void onQueryFailed() {

            }
        }).fetch();
    }
}
