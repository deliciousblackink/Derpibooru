package derpibooru.derpy.server;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import derpibooru.derpy.server.parsers.ServerResponseParser;

abstract class Provider {
    protected static final String DERPIBOORU_DOMAIN = "https://trixiebooru.org/";

    private ProviderRequestHandler mHandler;
    private Context mContext;

    public Provider(Context context, ProviderRequestHandler handler) {
        mContext = context;
        mHandler = handler;
    }

    public abstract void fetch();

    protected abstract String generateUrl();

    protected void executeQuery(String url, ServerResponseParser parser) {
        Handler threadHandler = new Handler();
        AsynchronousRequest requestThread =
                new AsynchronousRequest(mContext, parser, url,
                                        new AsynchronousRequest.RequestHandler() {
                                            Handler uiThread = new Handler(Looper.getMainLooper());

                                            @Override
                                            public void onRequestCompleted(Object parsedResponse) {
                                                uiThread.post(new UiThreadMessageSender(parsedResponse, false));
                                            }

                                            @Override
                                            public void onRequestFailed() {
                                                uiThread.post(new UiThreadMessageSender(null, true));
                                            }
                                        });
        threadHandler.post(requestThread);
    }

    protected class UiThreadMessageSender implements Runnable {
        private Object mMessage;

        public UiThreadMessageSender(Object message, boolean isError) {
            if (!isError) {
                mMessage = message;
            } else {
                mMessage = null;
            }
        }

        @Override
        public void run() {
            if (mMessage != null) {
                mHandler.onDataFetched(mMessage);
            } else {
                mHandler.onDataRequestFailed();
            }
        }
    }
}
