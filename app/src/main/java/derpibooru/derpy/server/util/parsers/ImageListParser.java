package derpibooru.derpy.server.util.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import derpibooru.derpy.data.types.DerpibooruImageThumb;

public class ImageListParser implements ServerResponseParser {
    public Object parseResponse(String rawResponse) throws JSONException {
        ArrayList<DerpibooruImageThumb> output = new ArrayList<>();

        JSONObject json = new JSONObject(rawResponse);
        JSONArray images = json.getJSONArray("images");

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
}
