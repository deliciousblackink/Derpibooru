package derpibooru.derpy.server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import derpibooru.derpy.data.server.DerpibooruImageThumb;

class ImageListParser implements ServerResponseParser {
    public ImageListParser() {
    }

    public Object parseResponse(String rawResponse) throws JSONException {
        ArrayList<DerpibooruImageThumb> output = new ArrayList<>();

        JSONObject json = new JSONObject(rawResponse);
        JSONArray images = getRootArray(json);

        int imgCount = images.length();
        for (int x = 0; x < imgCount; x++) {
            JSONObject img = images.getJSONObject(x);

            DerpibooruImageThumb it =
                    new DerpibooruImageThumb(img.getInt("id_number"),
                                             img.getInt("score"), img.getInt("upvotes"),
                                             img.getInt("downvotes"), img.getInt("faves"),
                                             img.getInt("comment_count"),
                                             img.getJSONObject("representations").getString("thumb"),
                                             img.getString("image"));
            output.add(it);
        }
        return output;
    }

    private JSONArray getRootArray(JSONObject json) throws JSONException {
        if (!json.isNull("images")) {
            return json.getJSONArray("images");
        } else {
            /* image list parser is also used for search results,
             * where the root tag is 'search' */
            return json.getJSONArray("search");
        }
    }
}
