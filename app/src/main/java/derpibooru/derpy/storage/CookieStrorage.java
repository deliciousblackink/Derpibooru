package derpibooru.derpy.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class CookieStrorage {
    public static final String PREFERENCES_NAME = "ServerPrefs";

    private SharedPreferences mPreferences;

    public CookieStrorage(Context context) {
        mPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
    }

    public String getCookie() {
        return mPreferences.getString("cookie", "");
    }

    public void setCookie(String cookie) {
        mPreferences.edit()
                .putString("cookie", cookie).apply();
    }
}
