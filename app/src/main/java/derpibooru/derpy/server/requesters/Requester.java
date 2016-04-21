package derpibooru.derpy.server.requesters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import java.util.Map;

import derpibooru.derpy.server.AsynchronousFormRequest;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.parsers.ServerResponseParser;
import derpibooru.derpy.server.providers.Provider;

/**
 * Asynchronous performer of unsafe (both idempotent and non-idempotent) HTTP requests with form submission.
 */
abstract class Requester<T> extends Provider<T> {
    Requester(Context context, QueryHandler<T> handler) {
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
    protected void executeQuery(@Nullable ServerResponseParser<T> parser) {
        Handler thread = new Handler();
        thread.post(new AsynchronousFormRequest<T>(mContext, parser, generateUrl(), generateForm(),
                                                   getHeaders(), getSuccessResponseCode(), getHttpMethod()) {
            Handler uiThread = new Handler(Looper.getMainLooper());

            @Override
            public void onRequestCompleted(T parsedResponse) {
                uiThread.post(new UiThreadMessageSender(parsedResponse, false));
            }

            @Override
            public void onRequestFailed() {
                uiThread.post(new UiThreadMessageSender(null, true));
            }
        });
    }
}
