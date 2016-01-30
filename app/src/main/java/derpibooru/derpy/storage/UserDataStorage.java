package derpibooru.derpy.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import derpibooru.derpy.data.server.DerpibooruFilter;
import derpibooru.derpy.data.server.DerpibooruUser;

public class UserDataStorage {
    /* TODO: memory caching */
    public static final String PREFERENCES_NAME = "Derpibooru";

    private SharedPreferences mPreferences;
    private Gson mGson;

    public UserDataStorage(Context context) {
        mPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
        mGson = new Gson();
    }

    public DerpibooruUser getUserData() {
        String json = mPreferences.getString("user", "");
        if (!json.equals("")) {
            return mGson.fromJson(json, DerpibooruUser.class);
        }
        return null;
    }

    public void setUserData(DerpibooruUser userData) {
        String json = mGson.toJson(userData, DerpibooruUser.class);
        mPreferences.edit()
                .putString("user", json)
                .apply();
    }

    public void setCurrentFilter(DerpibooruFilter newFilter) {
        DerpibooruUser user = getUserData();
        user.setCurrentFilter(newFilter);
        setUserData(user);
    }

    /* TODO: clear user data from the device if "Remember me" is not set */
    public void clearUserData() {
        mPreferences.edit().remove("user").apply();
    }
}