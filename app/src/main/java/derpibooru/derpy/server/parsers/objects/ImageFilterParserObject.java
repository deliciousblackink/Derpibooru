package derpibooru.derpy.server.parsers.objects;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import derpibooru.derpy.R;
import derpibooru.derpy.data.comparators.DerpibooruTagTypeComparator;
import derpibooru.derpy.data.server.DerpibooruTagDetailed;

public class ImageFilterParserObject {
    private static final String HIDDEN_TAG_IMAGE_RESOURCE_URI = ("android.resource://derpibooru.derpy/" + R.drawable.hidden_tag);

    private final List<DerpibooruTagDetailed> mSpoileredTags;
    private final List<Integer> mHiddenTags;

    public ImageFilterParserObject(List<DerpibooruTagDetailed> spoileredTags) {
        mSpoileredTags = spoileredTags;
        mHiddenTags = Collections.emptyList();
    }

    public ImageFilterParserObject(List<DerpibooruTagDetailed> spoileredTags, List<Integer> hiddenTags) {
        mSpoileredTags = spoileredTags;
        mHiddenTags = hiddenTags;
    }

    /**
     * @return an empty string if the image is not spoilered; an url to the corresponding spoiler image otherwise.
     */
    public String getImageSpoilerUrl(JSONArray spoileredTagIds) throws JSONException {
        List<Integer> imageTagIds = intListFromArray(spoileredTagIds);
        /* if the image has multiple tags spoilered, it should use
         * the spoiler image for the ContentSafety one (e.g. "suggestive") */
        Collections.sort(mSpoileredTags, new DerpibooruTagTypeComparator(true));
        for (DerpibooruTagDetailed tag : mSpoileredTags) {
            if (imageTagIds.contains(tag.getId())) {
                return (!tag.getSpoilerUrl().isEmpty()) ? tag.getSpoilerUrl() : HIDDEN_TAG_IMAGE_RESOURCE_URI;
            }
        }
        return "";
    }

    /**
     * @return an empty string if the image is not hidden; an url to the hidden tag image otherwise.
     */
    public String getImageHiddenUrl(JSONArray hiddenTagIds) throws JSONException, IllegalStateException {
        if (mHiddenTags == null) throw new IllegalStateException("ImageFilterParserObject did not receive a list of hidden tags on initialization. Use the appropriate constructor.");
        return (Collections.disjoint(intListFromArray(hiddenTagIds), mHiddenTags)) ? "" : HIDDEN_TAG_IMAGE_RESOURCE_URI;
    }

    private List<Integer> intListFromArray(JSONArray array) throws JSONException {
        List<Integer> out = new ArrayList<>();
        for (int x = 0; x < array.length(); x++) {
            out.add(array.getInt(x));
        }
        return out;
    }
}
