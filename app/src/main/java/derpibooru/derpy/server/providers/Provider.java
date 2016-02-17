package derpibooru.derpy.server.providers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import derpibooru.derpy.server.AsynchronousRequest;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.parsers.ServerResponseParser;

/**
 * Asynchronous performer of safe (nullipotent) HTTP requests.
 */
public abstract class Provider {
    protected static final String DERPIBOORU_DOMAIN = "https://trixiebooru.org/";
    protected static final String DERPIBOORU_API_ENDPOINT = "api/v2/";

    protected QueryHandler mHandler;
    protected Context mContext;

    public Provider(Context context, QueryHandler handler) {
        mContext = context;
        mHandler = handler;
    }

    public abstract void fetch();

    protected abstract String generateUrl();

    protected void cacheResponse(Object parsedResponse) { }

    protected void executeQuery(ServerResponseParser parser) {
        Handler thread = new Handler();
        thread.post(new AsynchronousRequest(mContext, parser, generateUrl()) {
            Handler uiThread = new Handler(Looper.getMainLooper());

            @Override
            protected void onRequestCompleted(Object parsedResponse) {
                cacheResponse(parsedResponse);
                uiThread.post(new UiThreadMessageSender(parsedResponse, false));
            }

            @Override
            protected void onRequestFailed() {
                uiThread.post(new UiThreadMessageSender(null, true));
            }
        });
    }

    protected class UiThreadMessageSender implements Runnable {
        private Object mMessage;
        private boolean mIsError;

        public UiThreadMessageSender(Object message, boolean isError) {
            mMessage = message;
            mIsError = isError;
        }

        @Override
        public void run() {
            if (!mIsError) {
                mHandler.onQueryExecuted(mMessage);
            } else {
                mHandler.onQueryFailed();
            }
        }
    }
}
