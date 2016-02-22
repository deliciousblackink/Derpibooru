package derpibooru.derpy.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import derpibooru.derpy.data.server.DerpibooruTagDetailed;

public class TagStorage {
    /* TODO: memory caching */
    private static final String PREFERENCES_NAME = "Tags";

    private SharedPreferences mPreferences;
    private Gson mGson;

    public TagStorage(Context context) {
        mPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
        mGson = new Gson();
    }

    public DerpibooruTagDetailed getTag(int tagId) {
        String json = mPreferences.getString(Integer.toString(tagId), "");
        if (!json.equals("")) {
            return mGson.fromJson(json, DerpibooruTagDetailed.class);
        }
        return null;
    }

    public void setTag(DerpibooruTagDetailed tag) {
        String json = mGson.toJson(tag, DerpibooruTagDetailed.class);
        mPreferences.edit()
                .putString(Integer.toString(tag.getId()), json)
                .apply();
    }
}