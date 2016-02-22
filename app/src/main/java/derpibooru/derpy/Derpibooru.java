package derpibooru.derpy;

import android.app.Application;

import java.util.List;

import derpibooru.derpy.storage.CookieStrorage;
import derpibooru.derpy.storage.UserDataStorage;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class Derpibooru extends Application {
    /**
     * An application-wide OkHttpClient that provides connection re-use, a shared response
     * cache, etc.
     *
     * @see <a href="https://plus.google.com/118239425803358296962/posts/5nzAvPaitHu">OkHttpClient is designed to be treated as a singleton</a> */
    private OkHttpClient mHttpClient;
    private CookieStrorage mCookieStorage;

    /**
     * Contains the API key for data manipulation requests. It is retrieved on the
     * application start and is <b>never</b> kept in the device's persistent storage. */
    private String mApiKey;

    @Override
    public void onCreate() {
        super.onCreate();
        mCookieStorage = new CookieStrorage(getApplicationContext());
        initializeHttpClient();
    }

    public OkHttpClient getHttpClient() {
        return mHttpClient;
    }

    private void initializeHttpClient() {
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
                }).build();
        /* FIXME: user data shouldn't be kept in a persistent storage */
        /* UserDataStorage, as it stands now, is just a disguised global variable. either make it explicitly
         * global, or look into some alternatives — dependency injection looks quite fitting there */
        new UserDataStorage(this).clearUserData();
    }

    /**
     * Returns the API key associated with the current user.
     */
    public String getApiKey() {
        return mApiKey;
    }

    /**
     * Sets the user's API key.
     */
    public void setApiKey(String key) {
        mApiKey = key;
    }
}
