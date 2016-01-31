package derpibooru.derpy.server.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import derpibooru.derpy.data.comparators.DerpibooruTagTypeComparator;
import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.data.server.DerpibooruTagFull;

public class ImageListParser implements ServerResponseParser {
    private List<DerpibooruTagFull> mSpoileredTags;

    public ImageListParser(List<DerpibooruTagFull> spoileredTags) {
        mSpoileredTags = spoileredTags;
    }

    public Object parseResponse(String rawResponse) throws JSONException {
        ArrayList<DerpibooruImageThumb> output = new ArrayList<>();

        JSONObject json = new JSONObject(rawResponse);
        JSONArray images = getRootArray(json);

        int imgCount = images.length();
        for (int x = 0; x < imgCount; x++) {
            JSONObject img = images.getJSONObject(x);
            List<Integer> imgTags = intListFromArray(img.getJSONArray("tag_ids"));

            DerpibooruImageThumb it =
                    new DerpibooruImageThumb(img.getInt("id_number"), img.getInt("score"),
                                             img.getInt("upvotes"), img.getInt("downvotes"),
                                             img.getInt("faves"), img.getInt("comment_count"),
                                             img.getJSONObject("representations").getString("thumb"),
                                             img.getString("image"),
                                             getSpoileredTagNames(imgTags), getSpoilerUrl(imgTags));
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

    private List<String> getSpoileredTagNames(List<Integer> imageTagIds) {
        List<String> spoilered = new ArrayList<>();
        for (DerpibooruTagFull tag : mSpoileredTags) {
            if (imageTagIds.contains(tag.getId())) {
                spoilered.add(tag.getName());
            }
        }
        return spoilered;
    }

    private String getSpoilerUrl(List<Integer> imageTagIds) {
        /* if the image has multiple tags spoilered, it should use
         * the spoiler image for the ContentSafety one (e.g. "suggestive") */
        Collections.sort(mSpoileredTags, new DerpibooruTagTypeComparator());
        for (DerpibooruTagFull tag : mSpoileredTags) {
            if (imageTagIds.contains(tag.getId())) {
                return tag.getSpoilerUrl();
            }
        }
        return "";
    }

    private List<Integer> intListFromArray(JSONArray array) throws JSONException {
        List<Integer> out = new ArrayList<>();
        for (int x = 0; x < array.length(); x++) {
            out.add(array.getInt(x));
        }
        return out;
    }
}
