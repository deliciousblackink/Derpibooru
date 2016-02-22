package derpibooru.derpy.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import derpibooru.derpy.data.server.DerpibooruTagFull;

public class TagStorage {
    /* TODO: memory caching */
    private static final String PREFERENCES_NAME = "Tags";

    private SharedPreferences mPreferences;
    private Gson mGson;

    public TagStorage(Context context) {
        mPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
        mGson = new Gson();
    }

    public DerpibooruTagFull getTag(int tagId) {
        String json = mPreferences.getString(Integer.toString(tagId), "");
        if (!json.equals("")) {
            return mGson.fromJson(json, DerpibooruTagFull.class);
        }
        return null;
    }

    public void setTag(DerpibooruTagFull tag) {
        String json = mGson.toJson(tag, DerpibooruTagFull.class);
        mPreferences.edit()
                .putString(Integer.toString(tag.getId()), json)
                .apply();
    }
}