package derpibooru.derpy.server.parsers.objects;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import derpibooru.derpy.data.comparators.DerpibooruTagTypeComparator;
import derpibooru.derpy.data.server.DerpibooruTagDetailed;

public class ImageSpoilerParserObject {
    private List<DerpibooruTagDetailed> mSpoileredTags;

    public ImageSpoilerParserObject(List<DerpibooruTagDetailed> spoileredTags) {
        mSpoileredTags = spoileredTags;
    }

    public String getSpoilerUrl(JSONArray spoileredTagIds) throws JSONException {
        List<Integer> imageTagIds = intListFromArray(spoileredTagIds);
        /* if the image has multiple tags spoilered, it should use
         * the spoiler image for the ContentSafety one (e.g. "suggestive") */
        Collections.sort(mSpoileredTags, new DerpibooruTagTypeComparator());
        for (DerpibooruTagDetailed tag : mSpoileredTags) {
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
