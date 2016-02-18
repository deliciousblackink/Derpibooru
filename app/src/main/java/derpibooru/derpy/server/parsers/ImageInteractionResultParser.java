package derpibooru.derpy.server.parsers;

import android.util.Log;

import derpibooru.derpy.data.server.DerpibooruImageInteraction;

public class ImageInteractionResultParser implements ServerResponseParser<DerpibooruImageInteraction> {
    @Override
    public DerpibooruImageInteraction parseResponse(String rawResponse) throws Exception {
        Log.e("interaction", rawResponse);
        return null;
    }
}
