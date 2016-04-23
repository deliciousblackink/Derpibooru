package derpibooru.derpy.server.providers;

import android.content.Context;

import java.util.List;

import derpibooru.derpy.data.server.DerpibooruComment;
import derpibooru.derpy.data.server.DerpibooruFilter;
import derpibooru.derpy.data.server.DerpibooruTagDetailed;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.parsers.CommentParser;

public class CommentProvider extends Provider<DerpibooruComment> {
    private final DerpibooruFilter mFilter;

    private int mCommentId;

    public CommentProvider(Context context, QueryHandler<DerpibooruComment> handler,
                           DerpibooruFilter userFilter) {
        super(context, handler);
        mFilter = userFilter;
    }

    /**
     * Returns a CommentProvider for the particular <strong>comment</strong> ID.
     *
     * @param id comment ID
     */
    public CommentProvider id(int id) {
        mCommentId = id;
        return this;
    }

    @Override
    protected String generateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(DERPIBOORU_DOMAIN);
        sb.append("comment/");
        sb.append(mCommentId);
        return sb.toString();
    }

    @Override
    public void fetch() {
        /* TODO: shorten & clean this up */
        /* FIXME: code dupe see CommentListProvider */
        new TagProvider(mContext, new QueryHandler<List<DerpibooruTagDetailed>>() {
            @Override
            public void onQueryExecuted(final List<DerpibooruTagDetailed> spoileredTags) {
                new TagProvider(mContext, new QueryHandler<List<DerpibooruTagDetailed>>() {
                    @Override
                    public void onQueryExecuted(List<DerpibooruTagDetailed> hiddenTags) {
                        fetchWithTags(spoileredTags, hiddenTags);
                    }

                    @Override
                    public void onQueryFailed() {
                        mHandler.onQueryFailed();
                    }
                }).tags(mFilter.getHiddenTags()).fetch();
            }

            @Override
            public void onQueryFailed() {
                mHandler.onQueryFailed();
            }
        }).tags(mFilter.getSpoileredTags()).fetch();
    }

    private void fetchWithTags(List<DerpibooruTagDetailed> spoileredTags, List<DerpibooruTagDetailed> hiddenTags) {
        super.executeQuery(new CommentParser(spoileredTags, hiddenTags));
    }
}

