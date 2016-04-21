package derpibooru.derpy.server;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.Map;

import derpibooru.derpy.Derpibooru;
import derpibooru.derpy.server.parsers.ServerResponseParser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class AsynchronousRequest<T> implements Runnable {
    private OkHttpClient mHttpClient;
    private ServerResponseParser<T> mResponseParser;
    private Map<String, String> mHeaders;
    private int mSuccessCode;

    protected String mUrl;

    protected AsynchronousRequest(Context context, @Nullable ServerResponseParser<T> parser,
                                  String url, Map<String, String> headers) {
        mHttpClient = ((Derpibooru) context.getApplicationContext()).getHttpClient();
        mResponseParser = parser;
        mUrl = url;
        mHeaders = headers;
        mSuccessCode = 200;
    }

    AsynchronousRequest(Context context, @Nullable ServerResponseParser<T> parser, String url,
                        Map<String, String> headers, int successResponseCode) {
        mHttpClient = ((Derpibooru) context.getApplicationContext()).getHttpClient();
        mResponseParser = parser;
        mUrl = url;
        mHeaders = headers;
        mSuccessCode = successResponseCode;
    }

    protected abstract void onRequestCompleted(T parsedResponse);

    protected abstract void onRequestFailed();

    @Override
    public void run() {
        Request r = generateRequest();
        mHttpClient.newCall(r).enqueue(new Callback() {
            @Override
            public void onFailure(Call request, IOException e) {
                Log.e("AsynchronousRequest", request.toString(), e);
                onRequestFailed();
            }

            @Override
            public void onResponse(Call request, Response response) throws IOException {
                if (response.code() == mSuccessCode) {
                    onRequestCompleted(parseResponse(response));
                } else {
                    Log.e("AsynchronousRequest", String.format("run(): Response code doesn't match the required value (expected %d, got %d)", mSuccessCode, response.code()));
                    onRequestFailed();
                }
            }
        });
    }

    protected Request generateRequest() {
        Request.Builder builder =
                new Request.Builder().url(mUrl);
        for (Map.Entry<String, String> header : mHeaders.entrySet()) {
            builder.addHeader(header.getKey(), header.getValue());
        }
        return builder.build();
    }

    protected T parseResponse(Response response) {
        if (mResponseParser != null) {
            try {
                return mResponseParser.parseResponse(response.body().string());
            } catch (Exception e) {
                Log.e("AsynchronousRequest", "Error parsing response", e);
                onRequestFailed();
            }
        }
        return null;
    }
}
