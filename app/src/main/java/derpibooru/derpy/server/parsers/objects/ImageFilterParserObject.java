package derpibooru.derpy.server.parsers.objects;

import android.support.annotation.VisibleForTesting;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import derpibooru.derpy.R;
import derpibooru.derpy.data.comparators.DerpibooruTagTypeComparator;
import derpibooru.derpy.data.server.DerpibooruTagDetailed;

public class ImageFilterParserObject {
    @VisibleForTesting
    public static final String HIDDEN_TAG_IMAGE_RESOURCE_URI
            = "https://derpicdn.net/img/view/2015/11/8/1019239__safe_solo_princess+celestia_vector_meta_frown_derpibooru_hair+over+one+eye_-dot-svg+available_raised+eyebrow.png";
    /* pre-Lollipop doesn't support SVG */

    private final List<DerpibooruTagDetailed> mSpoileredTags;
    private final List<DerpibooruTagDetailed> mHiddenTags;

    public ImageFilterParserObject(List<DerpibooruTagDetailed> spoileredTags) {
        mSpoileredTags = spoileredTags;
        mHiddenTags = Collections.emptyList();
        sortTagList(mSpoileredTags);
    }

    public ImageFilterParserObject(List<DerpibooruTagDetailed> spoileredTags, List<DerpibooruTagDetailed> hiddenTags) {
        mSpoileredTags = spoileredTags;
        mHiddenTags = hiddenTags;
        sortTagList(mSpoileredTags);
        sortTagList(mHiddenTags);
    }

    private void sortTagList(List<DerpibooruTagDetailed> list) {
        /* if the image has multiple tags spoilered, it should use
         * the spoiler image for the ContentSafety one (e.g. "suggestive") */
        Collections.sort(list, new DerpibooruTagTypeComparator(true));
    }

    /**
     * Returns the URL pointing to the spoilered tag's image.
     * <p>
     * <strong>Note:</strong> if the image has multiple tags spoilered, the method picks the one corresponding
     * to {@link derpibooru.derpy.data.server.DerpibooruTag.TagType#ContentSafety}.
     *
     * @return an empty string if the image is not spoilered; the URL to the corresponding spoiler image otherwise.
     */
    public String getSpoileredTagImageUrl(JSONArray imageTagIds) throws JSONException {
        List<Integer> tagIds = integerListFromJson(imageTagIds);
        for (DerpibooruTagDetailed tag : mSpoileredTags) {
            if (tagIds.contains(tag.getId())) {
                return (!tag.getSpoilerUrl().isEmpty()) ? tag.getSpoilerUrl() : HIDDEN_TAG_IMAGE_RESOURCE_URI;
            }
        }
        return "";
    }

    /**
     * Returns the URL pointing to the hidden tag's image.
     *
     * @return an empty string if the image is not hidden; the URL to the hidden tag image otherwise.
     */
    public String getHiddenTagImageUrl(JSONArray imageTagIds) throws JSONException, IllegalStateException {
        if (mHiddenTags == null) throw new IllegalStateException("ImageFilterParserObject did not receive a list of hidden tags on initialization. Use the appropriate constructor.");
        List<Integer> tagIds = integerListFromJson(imageTagIds);
        for (DerpibooruTagDetailed tag : mHiddenTags) {
            if (tagIds.contains(tag.getId())) {
                return HIDDEN_TAG_IMAGE_RESOURCE_URI;
            }
        }
        return "";
    }

    /**
     * Returns the name of the spoilered tag.
     * <p>
     * <strong>Note:</strong> if the image has multiple tags spoilered, the method picks the one corresponding
     * to {@link derpibooru.derpy.data.server.DerpibooruTag.TagType#ContentSafety}.
     *
     * @return an empty string if the image is not spoilered; the name of the spoilered tag otherwise.
     */
    public String getSpoileredTagName(JSONArray imageTagIds) throws JSONException {
        List<Integer> tagIds = integerListFromJson(imageTagIds);
        for (DerpibooruTagDetailed tag : mSpoileredTags) {
            if (tagIds.contains(tag.getId())) {
                return tag.getName();
            }
        }
        return "";
    }

    /**
     * Returns the name of the hidden tag.
     * <p>
     * <strong>Note:</strong> if the image has multiple tags hidden, the method picks the one corresponding
     * to {@link derpibooru.derpy.data.server.DerpibooruTag.TagType#ContentSafety}.
     *
     * @return an empty string if the image is not hidden; the name of the hidden tag otherwise.
     */
    public String getHiddenTagName(JSONArray imageTagIds) throws JSONException {
        List<Integer> tagIds = integerListFromJson(imageTagIds);
        for (DerpibooruTagDetailed tag : mHiddenTags) {
            if (tagIds.contains(tag.getId())) {
                return tag.getName();
            }
        }
        return "";
    }

    private List<Integer> integerListFromJson(JSONArray array) throws JSONException {
        List<Integer> out = new ArrayList<>();
        for (int x = 0; x < array.length(); x++) {
            out.add(array.getInt(x));
        }
        return out;
    }
}
