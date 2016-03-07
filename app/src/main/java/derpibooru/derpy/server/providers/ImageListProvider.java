package derpibooru.derpy.server.providers;

import android.content.Context;

import java.util.List;

import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.data.server.DerpibooruTagDetailed;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.parsers.ImageListParser;

public class ImageListProvider extends PaginatedListProvider<DerpibooruImageThumb> {
    protected static final int IMAGES_PER_PAGE = 16;

    public ImageListProvider(Context context, QueryHandler<List<DerpibooruImageThumb>> handler) {
        super(context, handler);
    }

    @Override
    protected String generateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(DERPIBOORU_DOMAIN);
        sb.append("images.json");
        sb.append("?perpage=");
        sb.append(IMAGES_PER_PAGE);
        sb.append("&page=");
        sb.append(getCurrentPage());
        return sb.toString();
    }

    @Override
    public void fetch() {
        new SpoileredTagsProvider(mContext, new QueryHandler<List<DerpibooruTagDetailed>>() {
            @Override
            public void onQueryExecuted(List<DerpibooruTagDetailed> spoileredTags) {
                fetchImages(spoileredTags);
            }

            @Override
            public void onQueryFailed() {
                mHandler.onQueryFailed();
            }
        }).fetch();
    }

    private void fetchImages(List<DerpibooruTagDetailed> spoileredTags) {
        super.executeQuery(new ImageListParser(spoileredTags));
    }
}
