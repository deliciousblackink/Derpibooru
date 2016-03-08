package derpibooru.derpy.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SearchHistoryStorage {
    private static final String PREFERENCES_NAME = "Search";
    private static final String PREFERENCES_SEARCH_KEY = PREFERENCES_NAME;
    private static final int MAXIMUM_ITEMS_STORED = 15;

    private SharedPreferences mPreferences;
    private Gson mGson;

    public SearchHistoryStorage(Context context) {
        mPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
        mGson = new Gson();
    }

    public List<String> getSearchHistory() {
        String json = mPreferences.getString(PREFERENCES_SEARCH_KEY, "");

        Type datasetListType = new TypeToken<Collection<String>>() {}.getType();
        List<String> list = mGson.fromJson(json, datasetListType);

        return (list != null) ? list : Collections.<String>emptyList();
    }

    public void addSearchQuery(String searchQuery) {
        ArrayList<String> storedQueries = new ArrayList<>(getSearchHistory());
        while (storedQueries.size() > (MAXIMUM_ITEMS_STORED - 1)) {
            storedQueries.remove(0);
        }
        storedQueries.add(searchQuery);
        String json = mGson.toJson(storedQueries, List.class);
        mPreferences.edit()
                .putString(PREFERENCES_SEARCH_KEY, json)
                .apply();
    }
}
