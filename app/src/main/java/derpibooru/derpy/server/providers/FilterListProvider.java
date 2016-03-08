package derpibooru.derpy.server.providers;

import android.content.Context;

import java.util.List;

import derpibooru.derpy.data.server.DerpibooruFilter;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.parsers.FilterListParser;

public class FilterListProvider extends Provider<List<DerpibooruFilter>> {
    public FilterListProvider(Context context, QueryHandler<List<DerpibooruFilter>> handler) {
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
