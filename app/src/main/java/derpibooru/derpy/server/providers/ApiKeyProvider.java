package derpibooru.derpy.server.providers;

import android.content.Context;

import derpibooru.derpy.Derpibooru;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.parsers.ApiKeyParser;

public class ApiKeyProvider extends Provider {
    public ApiKeyProvider(Context context, QueryHandler handler) {
        super(context, handler);
    }

    @Override
    protected String generateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(DERPIBOORU_DOMAIN);
        sb.append("users/edit");
        return sb.toString();
    }

    @Override
    public void fetch() {
        String cached = ((Derpibooru) mContext.getApplicationContext()).getApiKey();
        if (!cached.equals("")) {
            mHandler.onQueryExecuted(cached);
        } else {
            super.executeQuery(new ApiKeyParser());
        }
    }

    @Override
    protected void cacheResponse(Object parsedResponse) {
        ((Derpibooru) mContext.getApplicationContext()).setApiKey((String) parsedResponse);
    }
}
