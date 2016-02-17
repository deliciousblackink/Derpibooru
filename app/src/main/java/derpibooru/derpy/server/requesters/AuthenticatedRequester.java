package derpibooru.derpy.server.requesters;

import android.content.Context;

import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.parsers.AuthenticityTokenParser;
import derpibooru.derpy.server.providers.Provider;

/**
 * A subclass of Requester that fetches a Rails authentication token before performing the request.
 */
public abstract class AuthenticatedRequester extends Requester {
    public AuthenticatedRequester(Context context, QueryHandler handler) {
        super(context, handler);
    }

    protected abstract void onTokenFetched(String token);

    protected void fetchToken() {
        new AuthenticityTokenProvider(mContext, new QueryHandler() {
            @Override
            public void onQueryExecuted(Object result) {
                onTokenFetched((String) result);
            }

            @Override
            public void onQueryFailed() {
                mHandler.onQueryFailed();
            }
        }).fetch();
    }

    private static class AuthenticityTokenProvider extends Provider {
        public AuthenticityTokenProvider(Context context, QueryHandler handler) {
            super(context, handler);
        }

        @Override
        protected String generateUrl() {
            StringBuilder sb = new StringBuilder();
            sb.append(DERPIBOORU_DOMAIN);
            return sb.toString();
        }

        @Override
        public void fetch() {
            super.executeQuery(new AuthenticityTokenParser());
        }
    }
}
