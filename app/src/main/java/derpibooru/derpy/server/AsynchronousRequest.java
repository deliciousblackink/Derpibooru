package derpibooru.derpy.server;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import derpibooru.derpy.Derpibooru;
import derpibooru.derpy.server.parsers.ServerResponseParser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class AsynchronousRequest implements Runnable {
    private OkHttpClient mHttpClient;

    protected ServerResponseParser mResponseParser;
    protected String mUrl;
    protected int mSuccessCode;

    public AsynchronousRequest(Context context, @Nullable ServerResponseParser parser, String url) {
        mHttpClient = ((Derpibooru) context.getApplicationContext()).getHttpClient();
        mResponseParser = parser;
        mUrl = url;
        mSuccessCode = 200;
    }

    public AsynchronousRequest(Context context, @Nullable ServerResponseParser parser,
                               String url, int successResponseCode) {
        mHttpClient = ((Derpibooru) context.getApplicationContext()).getHttpClient();
        mResponseParser = parser;
        mUrl = url;
        mSuccessCode = successResponseCode;
    }

    protected abstract void onRequestCompleted(Object parsedResponse);

    protected abstract void onRequestFailed();

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
                if (!response.isSuccessful() && response.code() != mSuccessCode) {
                    Log.e("AsynchronousRequest", "run(): Callback() onResponse");
                    onRequestFailed();
                }
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
        return new Request.Builder()
                .url(mUrl)
                .build();
    }

    protected Object parseResponse(Response response) {
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
