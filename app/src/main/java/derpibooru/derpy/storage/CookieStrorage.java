package derpibooru.derpy.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import okhttp3.Cookie;

public class CookieStrorage {
    /* TODO: memory caching */
    public static final String PREFERENCES_NAME = "Cookies";

    private SharedPreferences mPreferences;
    private Gson mGson;

    public CookieStrorage(Context context) {
        mPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
        mGson = new Gson();
    }

    public List<Cookie> getCookies(String domain) {
        String json = mPreferences.getString(domain, "");

        Type datasetListType = new TypeToken<Collection<Cookie>>() {}.getType();
        List<Cookie> list = mGson.fromJson(json, datasetListType);

        return (list != null) ? list : Collections.<Cookie>emptyList();
    }

    public void setCookies(String domain, List<Cookie> cookies) {
        String json = mGson.toJson(cookies, List.class);
        mPreferences.edit()
                .putString(domain, json)
                .apply();
    }
}
