package derpibooru.derpy.server.providers;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import derpibooru.derpy.data.server.DerpibooruComment;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.parsers.CommentParser;

public class CommentProvider extends Provider<DerpibooruComment> {
    private int mCommentId;

    public CommentProvider(Context context, QueryHandler<DerpibooruComment> handler) {
        super(context, handler);
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
    protected Map<String, String> getHeaders() {
        /* ! important: without the header, the server returns 422. could be a security feature or something, hence
         * TODO: use a public API method instead of this obscure URI */
        Map<String, String> headers = new HashMap<>(1);
        headers.put("x-requested-with", "XMLHttpRequest");
        return headers;
    }

    @Override
    protected String generateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(DERPIBOORU_DOMAIN);
        sb.append("images/18/comments/");
        /* the URI is taken from the actual request the site's script makes; I'm not sure myself why it always
         * requests the 18th image; apparently, it can be any other one — I've tried — so the number is random */
        sb.append(mCommentId);
        sb.append(".js");
        return sb.toString();
    }

    @Override
    public void fetch() {
        super.executeQuery(new CommentParser());
    }
}
