package derpibooru.derpy.server.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import derpibooru.derpy.data.server.DerpibooruTagDetailed;

public class TagListParser implements ServerResponseParser<List<DerpibooruTagDetailed>> {
    private List<DerpibooruTagDetailed> mTagsToAppend;

    public TagListParser(List<DerpibooruTagDetailed> tagsToAppend) {
        mTagsToAppend = tagsToAppend;
    }

    @Override
    public List<DerpibooruTagDetailed> parseResponse(String rawResponse) throws JSONException {
        ArrayList<DerpibooruTagDetailed> output = new ArrayList<>();

        JSONObject json = new JSONObject(rawResponse);
        JSONArray tags = json.getJSONArray("tags");

        int tagCount = tags.length();
        for (int x = 0; x < tagCount; x++) {
            JSONObject source = tags.getJSONObject(x);

            DerpibooruTagDetailed tf = new DerpibooruTagDetailed(
                    source.getInt("id"), source.getInt("images"), source.getString("name"),
                    source.getString("description"), source.getString("spoiler_image_uri"));
            output.add(tf);
        }

        output.addAll(mTagsToAppend);
        return output;
    }
}