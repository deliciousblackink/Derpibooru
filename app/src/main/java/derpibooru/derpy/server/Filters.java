package derpibooru.derpy.server;

import android.content.Context;

import java.util.ArrayList;

import derpibooru.derpy.data.server.DerpibooruFilter;
import derpibooru.derpy.server.parsers.FilterListParser;

public class Filters {
    private FiltersHandler mHandler;
    private Context mContext;
    private User mUser;

    public Filters(Context context, FiltersHandler handler) {
        mContext = context;
        mHandler = handler;
    }

    public void fetchAvailableFilters() {
        FilterListProvider flp = new FilterListProvider(mContext, new ProviderRequestHandler() {
            @Override
            public void onDataFetched(Object result) {
                mHandler.onAvailableFiltersFetched((ArrayList<DerpibooruFilter>) result);
            }

            @Override
            public void onDataRequestFailed() { }
        });
        flp.fetch();
    }

    public interface FiltersHandler {
        void onAvailableFiltersFetched(ArrayList<DerpibooruFilter> filters);
    }

    private class FilterListProvider extends Provider {
        public FilterListProvider(Context context, ProviderRequestHandler handler) {
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
            super.executeQuery(generateUrl(), new FilterListParser());
        }
    }
}
