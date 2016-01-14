package derpibooru.derpy.server.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import derpibooru.derpy.data.types.Image;
import derpibooru.derpy.data.types.ImageThumb;

public class Json {
    private String mRawJson;

    public Json(String raw) {
        mRawJson = raw;
    }

    public ArrayList<ImageThumb> readImageThumbs() {
        ArrayList<ImageThumb> output = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(mRawJson);
            JSONArray images = json.getJSONArray("images");

            int imgCount = images.length();
            for (int x = 0; x < imgCount; x++) {
                JSONObject img = images.getJSONObject(x);

                /* TODO: move the magical strings into a dedicated data structure */
                ImageThumb it = new ImageThumb(img.getInt("id_number"),
                        img.getInt("score"), img.getInt("upvotes"),
                        img.getInt("downvotes"), img.getInt("faves"),
                        img.getInt("comment_count"),
                        img.getJSONObject("representations").getString("thumb"));

                output.add(it);
            }

            return output;
        } catch (JSONException e) {
            Log.e("Json readImageThumbs", "Could not process JSON response.");
            return new ArrayList<>();
        }
    }

    public Image readImage() {
        try {
            JSONObject img = new JSONObject(mRawJson);

            String[] tagNames = img.getString("tags").split(", ");
            JSONArray tagIds = img.getJSONArray("tag_ids");

            HashMap<Integer, String> tags = new HashMap<>();
            for (int x = 0; x < tagNames.length; x++) {
                tags.put(Integer.parseInt(tagIds.get(x).toString()),
                        tagNames[x]);
            }

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S", Locale.ENGLISH);

            /* TODO: move the magical strings into a dedicated data structure */
            Image i = new Image(img.getInt("id_number"),
                    img.getInt("score"), img.getInt("upvotes"),
                    img.getInt("downvotes"), img.getInt("faves"),
                    img.getInt("comment_count"),
                    img.getJSONObject("representations").getString("medium"),
                    format.parse(img.getString("created_at")),
                    tags, img.getString("source_url"),
                    img.getString("description"), img.getString("uploader"));

            return i;
        } catch (JSONException e) {
            Log.e("Json readImage", e.getMessage());
            return null;
        } catch (ParseException e) {
            Log.e("Json readImage", e.getMessage());
            return null;
        }
    }
}
