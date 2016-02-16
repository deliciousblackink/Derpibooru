package derpibooru.derpy.server.providers;

import android.content.Context;

import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.parsers.FilterListParser;

public class FilterListProvider extends Provider {
    public FilterListProvider(Context context, QueryHandler handler) {
        super(context, handler);
    }

    @Override
    protected String generateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(DERPIBOORU_DOMAIN);
        sb.append("filters.json");
        return sb.toString();
    }

    @Override
    public void fetch() {
        super.executeQuery(new FilterListParser());
    }
}
