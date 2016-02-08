package derpibooru.derpy;

import android.app.Application;

import java.util.List;

import derpibooru.derpy.storage.CookieStrorage;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class Derpibooru extends Application {
    /* https://plus.google.com/118239425803358296962/posts/5nzAvPaitHu
     *
     * "...OkHttpClient is designed to be treated as a singleton. By using a
     * single instance you are afforded a shared response cache, thread pool,
     * connection re-use, etc."
     */
    private OkHttpClient mHttpClient;
    private CookieStrorage mCookieStorage;

    @Override
    public void onCreate() {
        super.onCreate();

        mCookieStorage = new CookieStrorage(getApplicationContext());
        mHttpClient = new OkHttpClient.Builder()
                .followRedirects(false) /* login page redirect is breaking the authentication */
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        mCookieStorage.setCookies(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        return mCookieStorage.getCookies(url.host());
                    }
                })
                .build();
    }

    public OkHttpClient getHttpClient() {
        return mHttpClient;
    }
}
