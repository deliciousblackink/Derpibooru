package derpibooru.derpy.server.providers;

import android.content.Context;

import java.util.List;

import derpibooru.derpy.data.server.DerpibooruComment;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.parsers.CommentListParser;

public class CommentListProvider extends PaginatedListProvider<DerpibooruComment> {
    private int mImageId;

    public CommentListProvider(Context context, QueryHandler<List<DerpibooruComment>> handler) {
        super(context, handler);
    }

    /**
     * Returns a CommentListProvider for the particular image ID.
     *
     * @param id image ID
     */
    public CommentListProvider id(int id) {
        mImageId = id;
        return this;
    }

    @Override
    protected String generateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(DERPIBOORU_DOMAIN);
        sb.append("images/");
        sb.append(mImageId);
        sb.append("/comments");
        sb.append("?page=");
        sb.append(getCurrentPage());
        return sb.toString();
    }

    @Override
    public void fetch() {
        super.executeQuery(new CommentListParser());
    }
}
