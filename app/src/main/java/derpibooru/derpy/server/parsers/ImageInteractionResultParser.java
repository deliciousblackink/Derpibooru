package derpibooru.derpy.server.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import derpibooru.derpy.data.server.DerpibooruImageInteraction;

public class ImageInteractionResultParser implements ServerResponseParser<DerpibooruImageInteraction> {
    private int mInternalImageId;
    private DerpibooruImageInteraction.InteractionType mType;

    public ImageInteractionResultParser(int internalImageId, DerpibooruImageInteraction.InteractionType interaction) {
        mInternalImageId = internalImageId;
        mType = interaction;
    }

    @Override
    public DerpibooruImageInteraction parseResponse(String rawResponse) throws JSONException {
        JSONObject json = new JSONObject(rawResponse);
        return new DerpibooruImageInteraction(json.getInt("score"), json.getInt("favourites"),
                                              json.getInt("up_vote_count"), json.getInt("down_vote_count"),
                                              mInternalImageId, mType);
    }
}
