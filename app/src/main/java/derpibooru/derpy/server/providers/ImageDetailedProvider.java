package derpibooru.derpy.server.providers;

import android.content.Context;

import java.util.List;

import derpibooru.derpy.data.server.DerpibooruImageDetailed;
import derpibooru.derpy.data.server.DerpibooruTagDetailed;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.parsers.ImageDetailedParser;

public class ImageDetailedProvider extends Provider<DerpibooruImageDetailed> {
    private int mId;

    public ImageDetailedProvider(Context context, QueryHandler<DerpibooruImageDetailed> handler) {
        super(context, handler);
    }

    public ImageDetailedProvider id(int id) {
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
        new SpoileredTagsProvider(mContext, new QueryHandler<List<DerpibooruTagDetailed>>() {
            @Override
            public void onQueryExecuted(List<DerpibooruTagDetailed> spoileredTags) {
                fetchDetailed(spoileredTags);
            }

            @Override
            public void onQueryFailed() {
                mHandler.onQueryFailed();
            }
        }).fetch();
    }

    private void fetchDetailed(List<DerpibooruTagDetailed> spoileredTags) {
        super.executeQuery(new ImageDetailedParser(spoileredTags));
    }
}
