package derpibooru.derpy.server.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import derpibooru.derpy.data.server.DerpibooruFilter;

public class FilterListParser implements ServerResponseParser {
    public Object parseResponse(String rawResponse) throws JSONException {
        /* TODO: parse complex filters */
        ArrayList<DerpibooruFilter> filters = new ArrayList<>();

        JSONObject json = new JSONObject(rawResponse);
        JSONArray systemFilters = json.getJSONArray("system_filters");
        JSONArray userFilters = json.getJSONArray("user_filters");

        /* TODO: look into "search_filters" — what is it? how to handle it? */
        /*JSONArray searchFilters = json.getJSONArray("search_filters");*/

        filters.addAll(parseFilterArray(systemFilters));
        filters.addAll(parseFilterArray(userFilters));

        return filters;
    }

    private ArrayList<DerpibooruFilter> parseFilterArray(JSONArray array) throws JSONException {
        ArrayList<DerpibooruFilter> output = new ArrayList<>();

        int size = array.length();
        for (int x = 0; x < size; x++) {
            JSONObject f = array.getJSONObject(x);

            ArrayList<Integer> spoileredIds = intListFromArray(f.getJSONArray("spoilered_tag_ids"));
            ArrayList<String> hidden = stringListFromArray(f.getJSONArray("hidden_tags"));
            ArrayList<String> spoilered = stringListFromArray(f.getJSONArray("spoilered_tags"));

            /* for some reason, Derpibooru sets "system" to "null" for non-system
             * filters, so getBoolean() does not work */
            boolean system = f.getString("system").equals("true");

            DerpibooruFilter filter =
                    new DerpibooruFilter(f.getInt("id_number"),
                                         f.getString("name"),
                                         spoileredIds);
            filter.setAdditionalInfo(f.getString("description"),
                                     hidden, spoilered,
                                     system, f.getInt("user_count"));

            output.add(filter);
        }
        return output;
    }

    private ArrayList<String> stringListFromArray(JSONArray array) throws JSONException {
        ArrayList<String> out = new ArrayList<>();
        for (int y = 0; y < array.length(); y++) {
            out.add(array.getString(y));
        }
        return out;
    }

    private ArrayList<Integer> intListFromArray(JSONArray array) throws JSONException {
        ArrayList<Integer> out = new ArrayList<>();
        for (int y = 0; y < array.length(); y++) {
            out.add(array.getInt(y));
        }
        return out;
    }
}
