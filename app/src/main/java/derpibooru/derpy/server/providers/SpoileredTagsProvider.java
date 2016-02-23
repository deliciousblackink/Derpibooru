package derpibooru.derpy.server.providers;

import android.content.Context;

import java.util.List;

import derpibooru.derpy.data.server.DerpibooruTagDetailed;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.QueryHandler;

public class SpoileredTagsProvider extends TagProvider {
    public SpoileredTagsProvider(Context context, QueryHandler<List<DerpibooruTagDetailed>> handler) {
        super(context, handler);
    }

    @Override
    public void fetch() {
        new UserDataProvider(mContext, new QueryHandler<DerpibooruUser>() {
            @Override
            public void onQueryExecuted(DerpibooruUser user) {
                SpoileredTagsProvider.super.tags(user.getCurrentFilter().getSpoileredTags());
                SpoileredTagsProvider.super.fetch();
            }

            @Override
            public void onQueryFailed() {

            }
        }).fetch();
    }
}
