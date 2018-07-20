package derpibooru.derpy.server.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import derpibooru.derpy.data.server.DerpibooruTagDetailed;
import derpibooru.derpy.server.providers.RankingImageListProvider.RankingsType;

public class RankingImageListParser extends ImageListParser {
    private RankingsType mRankingsType;

    public RankingImageListParser(RankingsType rankingsType, List<DerpibooruTagDetailed> spoileredTags) {
        super(spoileredTags);
        mRankingsType = rankingsType;
    }

    @Override
    public JSONArray getRootArray(JSONObject json) throws JSONException {
        return json.getJSONArray(mRankingsType == RankingsType.TopScoring ?
                "top_scoring" : "top_commented");
    }
}
