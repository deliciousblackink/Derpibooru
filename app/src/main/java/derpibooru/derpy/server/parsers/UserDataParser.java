package derpibooru.derpy.server.parsers;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import derpibooru.derpy.data.server.DerpibooruFilter;
import derpibooru.derpy.data.server.DerpibooruUser;
import derpibooru.derpy.server.parsers.objects.UserScriptParserObject;
import derpibooru.derpy.server.parsers.objects.UserboxParserObject;

public class UserDataParser implements ServerResponseParser<DerpibooruUser> {
    private UserboxParserObject mUserbox;
    private UserScriptParserObject mUserScript;

    @Override
    public DerpibooruUser parseResponse(String rawResponse) throws Exception {
        Document doc = Jsoup.parse(rawResponse);
        mUserbox = new UserboxParserObject(doc.select("div.userbox").first().html());
        mUserScript = new UserScriptParserObject(doc.select("body").select("script").last().html());
        return new DerpibooruUser(mUserScript.getUsername(), mUserScript.getAvatarUrl())
                .setCurrentFilter(parseCurrentFilter());
    }
    private DerpibooruFilter parseCurrentFilter() throws JSONException {
        return new DerpibooruFilter(mUserScript.getFilterId(), mUserbox.getFilterName(),
                                    mUserScript.getHiddenTagIds(), mUserScript.getSpoileredTagIds());
    }
}
