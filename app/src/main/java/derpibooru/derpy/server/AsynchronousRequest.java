package derpibooru.derpy.server;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import derpibooru.derpy.Derpibooru;
import derpibooru.derpy.server.parsers.ServerResponseParser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class AsynchronousRequest implements Runnable {
    private OkHttpClient mHttpClient;

    protected ServerResponseParser mResponseParser;
    protected String mUrl;

    protected RequestHandler mRequestHandler;

    public AsynchronousRequest(Context context,
                               ServerResponseParser parser,
                               String url,
                               RequestHandler requestHandler) {
        mHttpClient = ((Derpibooru) context.getApplicationContext()).getHttpClient();
        mResponseParser = parser;
        mUrl = url;
        mRequestHandler = requestHandler;
    }

    public void run() {
        Request r = generateRequest();
        mHttpClient.newCall(r).enqueue(new Callback() {
            @Override
            public void onFailure(Call request, IOException e) {
                Log.e("AsynchronousRequest", request.toString(), e);
                mRequestHandler.onRequestFailed();
            }

            @Override
            public void onResponse(Call request, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("AsynchronousRequest", request.toString());
                    mRequestHandler.onRequestFailed();
                }
                mRequestHandler.onRequestCompleted(parseResponse(response));
            }
        });
    }

    protected Request generateRequest() {
        return new Request.Builder()
                .url(mUrl)
                .build();
    }

    protected Object parseResponse(Response response) {
        try {
            return mResponseParser.parseResponse(response.body().string());
        } catch (Exception e) {
            Log.e("AsynchronousRequest", "Error parsing response", e);
            mRequestHandler.onRequestFailed();
        }
        return null;
    }

    interface RequestHandler {
        void onRequestCompleted(Object parsedResponse);

        void onRequestFailed();
    }
}
