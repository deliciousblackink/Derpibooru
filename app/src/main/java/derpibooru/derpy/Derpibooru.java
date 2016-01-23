package derpibooru.derpy;

import android.app.Application;

import okhttp3.OkHttpClient;

public class Derpibooru extends Application {
    /* https://plus.google.com/118239425803358296962/posts/5nzAvPaitHu
     *
     * "...OkHttpClient is designed to be treated as a singleton. By using a
     * single instance you are afforded a shared response cache, thread pool,
     * connection re-use, etc."
     */
    private final OkHttpClient mHttpClient;

    public Derpibooru() {
        mHttpClient = new OkHttpClient();
    }

    public OkHttpClient getHttpClient() {
        return mHttpClient;
    }
}
