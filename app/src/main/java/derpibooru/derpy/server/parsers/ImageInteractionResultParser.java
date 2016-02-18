package derpibooru.derpy.server.parsers;

import android.util.Log;

public class ImageInteractionResultParser implements ServerResponseParser {
    @Override
    public Object parseResponse(String rawResponse) throws Exception {
        Log.e("interaction", rawResponse);
        return null;
    }
}
