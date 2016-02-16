package derpibooru.derpy.server.providers;

import android.content.Context;

import java.util.List;

import derpibooru.derpy.data.server.DerpibooruTagFull;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.parsers.ImageListParser;

public class ImageListProvider extends Provider {
    protected static final int IMAGES_PER_PAGE = 16;

    private int mCurrentPage = 1;

    public ImageListProvider(Context context, QueryHandler handler) {
        super(context, handler);
    }

    public ImageListProvider nextPage() {
        mCurrentPage++;
        return this;
    }

    public ImageListProvider resetPageNumber() {
        mCurrentPage = 1;
        return this;
    }

    protected int getCurrentPage() {
        return mCurrentPage;
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
        new SpoileredTagsProvider(mContext, new QueryHandler() {
            @Override
            public void onQueryExecuted(Object result) {
                fetchImages((List<DerpibooruTagFull>) result);
            }

            @Override
            public void onQueryFailed() {
                mHandler.onQueryFailed();
            }
        }).fetch();
    }

    private void fetchImages(List<DerpibooruTagFull> spoileredTags) {
        super.executeQuery(new ImageListParser(spoileredTags));
    }
}
