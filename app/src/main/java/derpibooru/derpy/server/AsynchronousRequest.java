package derpibooru.derpy.server;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import derpibooru.derpy.Derpibooru;
import derpibooru.derpy.storage.CookieStrorage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class AsynchronousRequest implements Runnable {
    private OkHttpClient mHttpClient;

    private ServerResponseParser mResponseParser;
    private CookieStrorage mCookieStorage;
    private String mUrl;

    private RequestHandler mRequestHandler;

    public AsynchronousRequest(Context context,
                               ServerResponseParser parser,
                               String url,
                               RequestHandler requestHandler) {
        mHttpClient = ((Derpibooru) context.getApplicationContext()).getHttpClient();
        mCookieStorage = new CookieStrorage(context);
        mResponseParser = parser;
        mUrl = url;
        mRequestHandler = requestHandler;
    }

    public void run() {
        Request request = new Request.Builder()
                .url(mUrl)
                .addHeader("cookie", mCookieStorage.getCookie())
                .build();

        mHttpClient.newCall(request).enqueue(new Callback() {
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
                mCookieStorage.setCookie(response.header("set-cookie"));
                mRequestHandler.onRequestCompleted
                        (parseResponse(response.body().string()));
            }
        });
    }

    private Object parseResponse(String response) {
        try {
            return mResponseParser.parseResponse(response);
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
