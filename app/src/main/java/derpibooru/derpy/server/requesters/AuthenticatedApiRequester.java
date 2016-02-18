package derpibooru.derpy.server.requesters;

import android.content.Context;

import derpibooru.derpy.Derpibooru;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.parsers.ApiKeyParser;
import derpibooru.derpy.server.providers.Provider;

/**
 * A subclass of Requester that fetches an API key before performing the request.
 */
public abstract class AuthenticatedApiRequester<T> extends Requester<T> {
    public AuthenticatedApiRequester(Context context, QueryHandler<T> handler) {
        super(context, handler);
    }

    protected abstract void onApiKeyFetched(String apiKey);

    protected void fetchApiKey() {
        new ApiKeyProvider(mContext, new QueryHandler<String>() {
            @Override
            public void onQueryExecuted(String key) {
                onApiKeyFetched(key);
            }

            @Override
            public void onQueryFailed() {
                mHandler.onQueryFailed();
            }
        }).fetch();
    }

    private static class ApiKeyProvider extends Provider<String> {
        public ApiKeyProvider(Context context, QueryHandler<String> handler) {
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
            if (cached != null) {
                mHandler.onQueryExecuted(cached);
            } else {
                super.executeQuery(new ApiKeyParser());
            }
        }

        @Override
        protected void cacheResponse(String parsedResponse) {
            ((Derpibooru) mContext.getApplicationContext()).setApiKey(parsedResponse);
        }
    }
}
