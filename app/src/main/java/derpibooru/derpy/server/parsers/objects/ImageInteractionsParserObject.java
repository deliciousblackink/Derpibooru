package derpibooru.derpy.server.parsers.objects;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import derpibooru.derpy.data.server.DerpibooruImageInteraction;

public class ImageInteractionsParserObject {
    private JSONArray mInteractions;
    private Map<Integer, EnumSet<DerpibooruImageInteraction.InteractionType>> mInteractionsByImage;

    public ImageInteractionsParserObject(String jsonArray) throws JSONException {
        mInteractions = new JSONArray(jsonArray);
        mInteractionsByImage = new HashMap<>();
        parseInteractions();
    }

    @NonNull
    public EnumSet<DerpibooruImageInteraction.InteractionType> getImageInteractionsForImage(int imageId) {
        if (mInteractionsByImage.get(imageId) != null) {
            return mInteractionsByImage.get(imageId);
        }
        return EnumSet.noneOf(DerpibooruImageInteraction.InteractionType.class);
    }

    private void parseInteractions() throws JSONException {
        int interactionCount = mInteractions.length();
        for (int x = 0; x < interactionCount; x++) {
            JSONObject interactionJson = mInteractions.getJSONObject(x);
            DerpibooruImageInteraction.InteractionType interaction =
                    getImageInteractionType(interactionJson);
            int imageId = getImageIdForInteraction(interactionJson);
            if (!mInteractionsByImage.containsKey(imageId)) {
                mInteractionsByImage.put(imageId, EnumSet.of(interaction));
            } else {
                mInteractionsByImage.get(imageId).add(interaction);
            }
        }
    }

    private int getImageIdForInteraction(JSONObject interaction) throws JSONException {
        return interaction.getInt("image_id");
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
}
