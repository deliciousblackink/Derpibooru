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
public abstract class Provider<T> {
    protected static final String DERPIBOORU_DOMAIN = "https://derpibooru.org/";
    protected static final String DERPIBOORU_API_ENDPOINT = "api/v2/";

    protected final QueryHandler<T> mHandler;
    protected final Context mContext;

    protected Provider(Context context, QueryHandler<T> handler) {
        mContext = context;
        mHandler = handler;
    }

    public abstract void fetch();

    protected abstract String generateUrl();

    protected void cacheResponse(T parsedResponse) { }

    protected void executeQuery(ServerResponseParser<T> parser) {
        Handler thread = new Handler();
        thread.post(new AsynchronousRequest<T>(mContext, parser, generateUrl()) {
            Handler uiThread = new Handler(Looper.getMainLooper());

            @Override
            protected void onRequestCompleted(T parsedResponse) {
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
        private T mMessage;
        private boolean mIsError;

        public UiThreadMessageSender(T message, boolean isError) {
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
