package derpibooru.derpy.server.parsers;

import android.support.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import derpibooru.derpy.data.comparators.DerpibooruTagTypeComparator;
import derpibooru.derpy.data.server.DerpibooruImageInteraction;
import derpibooru.derpy.data.server.DerpibooruImage;
import derpibooru.derpy.data.server.DerpibooruTagDetailed;

public class ImageListParser implements ServerResponseParser<List<DerpibooruImage>> {
    private List<DerpibooruTagDetailed> mSpoileredTags;

    public ImageListParser(List<DerpibooruTagDetailed> spoileredTags) {
        mSpoileredTags = spoileredTags;
    }

    @Override
    public List<DerpibooruImage> parseResponse(String rawResponse) throws JSONException {
        JSONObject json = new JSONObject(rawResponse);
        JSONArray jsonImages = getRootArray(json);
        List<DerpibooruImage> imageThumbs = getImageThumbs(jsonImages);
        JSONArray jsonInteractions = json.getJSONArray("interactions");
        assignImageInteractionsToImageThumbs(imageThumbs, jsonInteractions);
        return imageThumbs;
    }

    private JSONArray getRootArray(JSONObject json) throws JSONException {
        if (!json.isNull("images")) {
            return json.getJSONArray("images");
        } else {
            /* image list parser is also used for search results, where the root tag is 'search' */
            return json.getJSONArray("search");
        }
    }

    private List<DerpibooruImage> getImageThumbs(JSONArray images) throws JSONException {
        List<DerpibooruImage> out = new ArrayList<>();
        int imgCount = images.length();
        for (int x = 0; x < imgCount; x++) {
            JSONObject img = images.getJSONObject(x);
            List<Integer> imgTags = intListFromArray(img.getJSONArray("tag_ids"));
            DerpibooruImage it = new DerpibooruImage(
                    img.getInt("id_number"), img.getInt("id"), img.getInt("upvotes"), img.getInt("downvotes"),
                    img.getInt("faves"), img.getInt("comment_count"),
                    getAbsoluteUrl(img.getJSONObject("representations").getString("thumb")),
                    getAbsoluteUrl(img.getJSONObject("representations").getString("large")),
                    getSpoilerUrl(imgTags));
            out.add(it);
        }
        return out;
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return String.format("https:%s", relativeUrl);
    }

    private void assignImageInteractionsToImageThumbs(List<DerpibooruImage> thumbs,
                                                      JSONArray interactions) throws JSONException {
        int actionCount = interactions.length();
        for (int x = 0; x < actionCount; x++) {
            JSONObject action = interactions.getJSONObject(x);
            DerpibooruImageInteraction.InteractionType imageInteractionType = getImageInteractionType(action);
            final int imageId = action.getInt("image_id");
            DerpibooruImage correspondingThumb = Iterables.find(thumbs, new Predicate<DerpibooruImage>() {
                @Override
                public boolean apply(DerpibooruImage it) {
                    return it.getInternalId() == imageId;
                }
            });
            correspondingThumb.getImageInteractions().add(imageInteractionType);
        }
    }

    @Nullable
    private DerpibooruImageInteraction.InteractionType getImageInteractionType(JSONObject interaction) throws JSONException {
        if (interaction.getString("interaction_type").equals("faved")) {
            return DerpibooruImageInteraction.InteractionType.Fave;
        } else if (interaction.getString("interaction_type").equals("voted")) {
            if (interaction.getString("value").equals("up")) {
                return DerpibooruImageInteraction.InteractionType.Upvote;
            } else {
                return DerpibooruImageInteraction.InteractionType.Downvote;
            }
        }
        return null;
    }

    private List<String> getSpoileredTagNames(List<Integer> imageTagIds) {
        List<String> spoilered = new ArrayList<>();
        for (DerpibooruTagDetailed tag : mSpoileredTags) {
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
