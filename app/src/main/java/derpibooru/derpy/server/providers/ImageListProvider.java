package derpibooru.derpy.server.providers;

import android.content.Context;

import java.util.List;

import derpibooru.derpy.data.server.DerpibooruFilter;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.data.server.DerpibooruTagDetailed;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.parsers.ImageListParser;

public class ImageListProvider extends PaginatedListProvider<DerpibooruImageThumb> {
    protected static final int IMAGES_PER_PAGE = 16;

    private final DerpibooruFilter mFilter;

    public ImageListProvider(Context context, QueryHandler<List<DerpibooruImageThumb>> handler,
                             DerpibooruFilter imageListFilter) {
        super(context, handler);
        mFilter = imageListFilter;
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
        new TagProvider(mContext, new QueryHandler<List<DerpibooruTagDetailed>>() {
            @Override
            public void onQueryExecuted(List<DerpibooruTagDetailed> spoileredTags) {
                fetchImages(spoileredTags);
            }

            @Override
            public void onQueryFailed() {
                mHandler.onQueryFailed();
            }
        }).tags(mFilter.getSpoileredTags()).fetch();
    }

    private void fetchImages(List<DerpibooruTagDetailed> spoileredTags) {
        super.executeQuery(new ImageListParser(spoileredTags));
    }
}
