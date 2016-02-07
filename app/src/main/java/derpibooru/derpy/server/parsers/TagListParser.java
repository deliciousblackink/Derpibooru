package derpibooru.derpy.server.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import derpibooru.derpy.data.server.DerpibooruTagFull;

public class TagListParser implements ServerResponseParser {
    private List<DerpibooruTagFull> mTagsToAppend;

    public TagListParser() { }

    public TagListParser(List<DerpibooruTagFull> tagsToAppend) {
        mTagsToAppend = tagsToAppend;
    }

    @Override
    public Object parseResponse(String rawResponse) throws JSONException {
        ArrayList<DerpibooruTagFull> output = new ArrayList<>();

        JSONObject json = new JSONObject(rawResponse);
        JSONArray tags = json.getJSONArray("tags");

        int tagCount = tags.length();
        for (int x = 0; x < tagCount; x++) {
            JSONObject source = tags.getJSONObject(x);

            DerpibooruTagFull tf = new DerpibooruTagFull(source.getInt("id"),
                                                         source.getInt("images"),
                                                         source.getString("name"),
                                                         source.getString("description"),
                                                         source.getString("spoiler_image_uri"));
            output.add(tf);
        }

        if (mTagsToAppend != null) {
            output.addAll(mTagsToAppend);
        }

        return output;
    }
}