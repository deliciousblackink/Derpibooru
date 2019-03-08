package derpibooru.derpy.server.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import derpibooru.derpy.data.server.DerpibooruImageInteraction;

public class ImageInteractionResultParser implements ServerResponseParser<DerpibooruImageInteraction> {
    private DerpibooruImageInteraction.InteractionType mType;

    public ImageInteractionResultParser(DerpibooruImageInteraction.InteractionType interaction) {
        mType = interaction;
    }

    @Override
    public DerpibooruImageInteraction parseResponse(String rawResponse) throws JSONException {
        JSONObject json = new JSONObject(rawResponse);
        return new DerpibooruImageInteraction(
                json.getInt("favourites"), json.getInt("upvotes"), json.getInt("downvotes"), mType);
    }
}
