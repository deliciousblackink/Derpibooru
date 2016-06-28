package derpibooru.derpy.server.requesters;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import derpibooru.derpy.data.server.DerpibooruImageInteraction;
import derpibooru.derpy.server.QueryHandler;
import derpibooru.derpy.server.parsers.ImageInteractionResultParser;

public class ImageInteractionRequester extends AuthenticatedApiRequester<DerpibooruImageInteraction> {
    private DerpibooruImageInteraction.InteractionType mType;
    private String mApiKey;
    private int mImageId;

    public ImageInteractionRequester(Context context, QueryHandler<DerpibooruImageInteraction> handler) {
        super(context, handler);
    }

    public ImageInteractionRequester interaction(DerpibooruImageInteraction.InteractionType type) {
        mType = type;
        return this;
    }

    public ImageInteractionRequester onImage(int imageId) {
        mImageId = imageId;
        return this;
    }

    @Override
    protected Map<String, String> generateForm() {
        Map<String, String> form = new HashMap<>(3);
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
            case ClearFave:
                form.put("value", "false");
                break;
        }
        form.put("key", mApiKey);
        return form;
    }

    @Override
    protected String generateUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(DERPIBOORU_DOMAIN).append(DERPIBOORU_API_ENDPOINT);
        if (mType == DerpibooruImageInteraction.InteractionType.Fave
                || mType == DerpibooruImageInteraction.InteractionType.ClearFave) {
            sb.append("interactions/fave.json");
        } else {
            sb.append("interactions/vote.json");
        }
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
        if (apiKey.equals("")) {
            mHandler.onQueryFailed();
        } else {
            mApiKey = apiKey;
            executeQuery(new ImageInteractionResultParser(mType));
        }
    }
}
