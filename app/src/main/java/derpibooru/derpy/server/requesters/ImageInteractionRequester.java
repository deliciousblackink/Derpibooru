package derpibooru.derpy.server.requesters;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import derpibooru.derpy.data.server.DerpibooruImageInteraction;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.parsers.ImageInteractionResultParser;

public class ImageInteractionRequester extends AuthenticatedApiRequester<DerpibooruImageInteraction> {
    private InteractionType mType;
    private String mApiKey;
    private int mImageId;

    public ImageInteractionRequester(Context context, QueryHandler<DerpibooruImageInteraction> handler) {
        super(context, handler);
    }

    public ImageInteractionRequester interaction(InteractionType type) {
        mType = type;
        return this;
    }

    public ImageInteractionRequester onImage(int imageId) {
        mImageId = imageId;
        return this;
    }

    @Override
    protected Map<String, String> generateForm() {
        HashMap<String, String> form = new HashMap<>();
        form.put("_method", "PUT");
        form.put("id", Integer.toString(mImageId));
        switch (mType) {
            case Fave:
                form.put("value", "true");
                break;
            case Upvote:
                form.put("value", "up");
                break;
            case Downvote:
                form.put("value", "down");
                break;
            case ClearVote:
                form.put("value", "false");
                break;
            case ClearFave:
                form.put("value", "false");
                break;
        }
        form.put("class", "Image");
        return form;
    }

    @Override
    protected String generateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(DERPIBOORU_DOMAIN).append(DERPIBOORU_API_ENDPOINT);
        if (mType == InteractionType.Fave || mType == InteractionType.ClearFave) {
            sb.append("interactions/fave");
        } else {
            sb.append("interactions/vote");
        }
        sb.append("?key=").append(mApiKey);
        return sb.toString();
    }

    @Override
    protected String getHttpMethod() {
        return "PUT";
    }

    @Override
    public void fetch() {
        fetchApiKey();
    }

    @Override
    protected void onApiKeyFetched(String apiKey) {
        mApiKey = apiKey;
        executeQuery(new ImageInteractionResultParser());
    }

    public enum InteractionType {
        Fave,
        Upvote,
        Downvote,
        ClearVote,
        ClearFave
    }
}
