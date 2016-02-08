package derpibooru.derpy.server.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import derpibooru.derpy.data.server.DerpibooruFilter;
import derpibooru.derpy.data.server.DerpibooruUser;

public class UserDataParser implements ServerResponseParser {
    private boolean mIsLoggedIn = false;

    @Override
    public Object parseResponse(String rawResponse) throws Exception {
        Document doc = Jsoup.parse(rawResponse);
        mIsLoggedIn = isLoggedIn(doc);
        Element userBox = doc.select("div.userbox").first();
        Element userScript = mIsLoggedIn ? doc.select("script").get(doc.select("script").size() - 2)
                                         : doc.select("script").last();
        return new DerpibooruUser(parseUsername(userBox), parseAvatarUrl(userScript.html()))
                .setCurrentFilter(parseCurrentFilter(doc, userScript.html()));
    }

    private boolean isLoggedIn(Document doc) {
        /* a quick filter switch is only present when the user is logged in */
        return doc.select("form#filter-quick-form").first() != null;
    }

    private String parseUsername(Element userBox) {
        Element profileLink = userBox.select("a.hide-desktop-t").first();
        if (profileLink != null) {
            String profileHref = profileLink.attr("href");
            Matcher m = Pattern.compile("(?:[/]profiles[/])(.*)")
                    .matcher(profileHref);
            return m.find() ? m.group(1) : "";
        }
        return "";
    }

    private String parseAvatarUrl(String userScriptHtml) {
        Matcher m = Pattern.compile("(?:window.booru.userAvatar = \")(.*)(?:\";)")
                .matcher(userScriptHtml);
        return m.find() ? m.group(1) : "";
    }

    private DerpibooruFilter parseCurrentFilter(Document doc, String userScriptHtml) {
        int id = getFilterId(userScriptHtml);
        String name = getFilterName(doc);
        List<Integer> spoileredTags = getSpoileredTags(userScriptHtml);
        return new DerpibooruFilter(id, name, spoileredTags);
    }

    private int getFilterId(String scriptHtml) {
        Matcher m = Pattern.compile("(?:window.booru.filterID = )([\\d]*)")
                .matcher(scriptHtml);
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }

    private String getFilterName(Document doc) {
        if (mIsLoggedIn) {
            return doc.select("form#filter-quick-form").first()
                    .select("option[selected]").first().text();
        } else {
            String filter = doc.select("div.userbox").first()
                    .select("a.hide-mobile").text();
            Matcher m = Pattern.compile("^(?:Filters )(?:\\()(.*)(?:\\))$")
                    .matcher(filter);
            m.find();
            return m.group(1);
        }
    }

    private List<Integer> getSpoileredTags(String scriptHtml) {
        Matcher m = Pattern.compile("(?:window.booru.spoileredTagList = )(?:\\[)(.*)(?:\\])")
                .matcher(scriptHtml);
        m.find();
        String tagIdsArray = m.group(1);
        if (!tagIdsArray.equals("")) {
            List<String> stringIds = new ArrayList<>(Arrays.asList(tagIdsArray.split(",")));
            List<Integer> intIds = new ArrayList<>();
            for (String stringId : stringIds) {
                intIds.add(Integer.parseInt(stringId));
            }
            return intIds;
        }
        return Collections.emptyList();
    }
}
