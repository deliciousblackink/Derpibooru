package derpibooru.derpy.server.parsers;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import derpibooru.derpy.data.server.DerpibooruFilter;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.parsers.objects.UserScriptParserObject;
import derpibooru.derpy.server.parsers.objects.HeaderParserObject;

public class UserDataParser implements ServerResponseParser<DerpibooruUser> {
    private HeaderParserObject mHeader;
    private UserScriptParserObject mUserScript;

    @Override
    public DerpibooruUser parseResponse(String rawResponse) throws Exception {
        Document doc = Jsoup.parse(rawResponse);
        mHeader = new HeaderParserObject(doc.select(".header").first().html());
        mUserScript = new UserScriptParserObject(doc.select("body").select("script").last().html());
        return new DerpibooruUser(mUserScript.getUsername(), mUserScript.getAvatarUrl())
                .setCurrentFilter(parseCurrentFilter());
    }
    private DerpibooruFilter parseCurrentFilter() throws JSONException {
        return new DerpibooruFilter(mUserScript.getFilterId(), mHeader.getFilterName(),
                                    mUserScript.getHiddenTagIds(), mUserScript.getSpoileredTagIds());
    }
}
