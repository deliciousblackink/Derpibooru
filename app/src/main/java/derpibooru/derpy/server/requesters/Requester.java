package derpibooru.derpy.server.requesters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.Map;

import derpibooru.derpy.server.AsynchronousFormRequest;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.parsers.ServerResponseParser;
import derpibooru.derpy.server.providers.Provider;

/**
 * Asynchronous performer of unsafe (both idempotent and non-idempotent) HTTP requests with form submission.
 */
public abstract class Requester extends Provider {
    public Requester(Context context, QueryHandler handler) {
        super(context, handler);
    }

    protected abstract Map<String, String> generateForm();

    public int getSuccessResponseCode() {
        return 200;
    }

    protected String getHttpMethod() {
        return "POST";
    }

    @Override
    protected void executeQuery(ServerResponseParser parser) {
        Handler thread = new Handler();
        thread.post(new AsynchronousFormRequest(mContext, generateUrl(), generateForm(), getSuccessResponseCode(), getHttpMethod()) {
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
    }
}
