package derpibooru.derpy.server;

import android.content.Context;

import java.util.List;

import derpibooru.derpy.data.server.DerpibooruTagFull;
import derpibooru.derpy.server.parsers.ImageListParser;

public abstract class ImageListProvider extends Provider {
    protected static final int IMAGES_PER_PAGE = 16;

    private int mCurrentPage = 1;

    public ImageListProvider(Context context, ProviderRequestHandler handler) {
        super(context, handler);
    }

    public ImageListProvider nextPage() {
        mCurrentPage++;
        return this;
    }

    protected int getCurrentPage() {
        return mCurrentPage;
    }

    protected void resetPageNumber() {
        mCurrentPage = 1;
    }

    @Override
    public void fetch() {
        Tags t = new Tags(mContext, new Tags.TagRequestHandler() {
            @Override
            public void onTagsFetched(List<DerpibooruTagFull> tagList) {
                fetchImages(tagList);
            }
        });
        t.fetchSpoileredTagsForCurrentFilter();
    }

    private void fetchImages(List<DerpibooruTagFull> spoileredTags) {
        super.executeQuery(generateUrl(), new ImageListParser(spoileredTags));
    }
}
