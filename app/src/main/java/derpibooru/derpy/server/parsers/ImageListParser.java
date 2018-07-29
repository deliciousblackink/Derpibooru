package derpibooru.derpy.server.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import derpibooru.derpy.data.server.DerpibooruImageThumb;
import derpibooru.derpy.data.server.DerpibooruTagDetailed;
import derpibooru.derpy.server.parsers.objects.ImageFilterParserObject;
import derpibooru.derpy.server.parsers.objects.ImageInteractionsParserObject;

public class ImageListParser implements ServerResponseParser<List<DerpibooruImageThumb>> {
    private ImageFilterParserObject mSpoilers;
    private ImageInteractionsParserObject mInteractions;

    public ImageListParser(List<DerpibooruTagDetailed> spoileredTags) {
        mSpoilers = new ImageFilterParserObject(spoileredTags);
    }

    @Override
    public List<DerpibooruImageThumb> parseResponse(String rawResponse) throws JSONException {
        JSONObject json = new JSONObject(rawResponse);
        mInteractions = new ImageInteractionsParserObject(
                json.getJSONArray("interactions").toString());
        JSONArray jsonImages = getRootArray(json);
        List<DerpibooruImageThumb> imageThumbs = getImageThumbs(jsonImages);
        return imageThumbs;
    }

    public JSONArray getRootArray(JSONObject json) throws JSONException {
        if (!json.isNull("images")) {
            return json.getJSONArray("images");
        } else {
            /* image list parser is also used for search results, where the root tag is 'search' */
            return json.getJSONArray("search");
        }
    }

    private List<DerpibooruImageThumb> getImageThumbs(JSONArray images) throws JSONException {
        List<DerpibooruImageThumb> out = new ArrayList<>();
        int imgCount = images.length();
        for (int x = 0; x < imgCount; x++) {
            JSONObject img = images.getJSONObject(x);
            DerpibooruImageThumb it = new DerpibooruImageThumb(
                    img.getInt("id"), img.getInt("upvotes"), img.getInt("downvotes"),
                    img.getInt("faves"), img.getInt("comment_count"),
                    getAbsoluteUrl(img.getJSONObject("representations").getString("thumb")),
                    getAbsoluteUrl(img.getJSONObject("representations").getString("large")),
                    mSpoilers.getSpoileredTagImageUrl(img.getJSONArray("tag_ids")),
                    mInteractions.getImageInteractionsForImage(img.getInt("id")));
            out.add(it);
        }
        return out;
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return String.format("https:%s", relativeUrl);
    }
}
